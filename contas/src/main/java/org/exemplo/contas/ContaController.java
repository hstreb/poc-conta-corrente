package org.exemplo.contas;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contas")
class ContaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContaController.class);

    private final ContaRepository contaRepository;
    private final TitularRepository titularRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    ContaController(ContaRepository contaRepository, TitularRepository titularRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.contaRepository = contaRepository;
        this.titularRepository = titularRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    @ResponseStatus(HttpStatus.CREATED)
    public ContaResponse criar(@RequestBody ContaRequest contaRequest) throws Exception {
        LOGGER.debug("Criar conta: titulares={}", contaRequest.titulares());
        var numero = contaRepository.getProximoNumeroConta();
        var conta = contaRepository.save(new ContaEntity(numero));
        var titulares = contaRequest.titulares.stream()
                .map(t -> titularRepository.save(new TitularEntity(conta.id, t.documento, t.nome)))
                .collect(Collectors.toSet());
        var response = map(conta, titulares);
        gerarEvento(response);
        return response;
    }

    @GetMapping("/{conta}")
    public ContaResponse buscar(@PathVariable UUID conta) {
        LOGGER.debug("Buscar conta: {}", conta);
        return contaRepository.findById(conta)
                .map(c -> map(c, titularRepository.getAllByConta(conta)))
                .orElseThrow(() -> new ContaNotFoundException("Conta n??o encontrada!"));
    }

    @DeleteMapping("/{conta}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable UUID conta) throws Exception {
        LOGGER.debug("Deletar conta: {}", conta);
        var contaEntity = contaRepository.findById(conta)
                .orElseThrow(() -> new ContaNotFoundException("Conta n??o encontrada!"));
        contaEntity.estado = "INATIVA";
        contaRepository.save(contaEntity);
        var titulares = titularRepository.getAllByConta(conta);
        var response = map(contaEntity, titulares);
        gerarEvento(response);
    }

    @Transactional
    public void gerarEvento(ContaResponse response) throws Exception {
        var data = objectMapper.writeValueAsString(response);
        var result = kafkaTemplate.send("contas", response.id().toString(), data)
                .get(11L, TimeUnit.SECONDS);
        LOGGER.debug("Evento de Conta enviada: id={}, metadata={}}", response.id(), result.getRecordMetadata());
    }

    private ContaResponse map(ContaEntity conta, Set<TitularEntity> titulares) {
        return new ContaResponse(conta.id,
                conta.agencia,
                "%d-%d".formatted(conta.numero, conta.digitoVerificador),
                titulares.stream()
                        .map(t -> new TitularResponse(t.id, t.documento, t.nome))
                        .collect(Collectors.toSet()),
                conta.estado,
                conta.dataCriacao);
    }

    record TitularRequest(String documento, String nome) {
    }

    record TitularResponse(UUID id, String documento, String nome) {
    }

    record ContaRequest(Set<TitularRequest> titulares) {
    }

    record ContaResponse(UUID id, String agencia, String conta, Set<TitularResponse> titulares, String estado,
                         LocalDateTime dataCriacao) {
    }
}
