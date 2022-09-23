package org.exemplo.transacoes.transacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.exemplo.transacoes.conta.ContaNotFoundException;
import org.exemplo.transacoes.conta.ContaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/contas/{conta}/transacoes")
public class TransacaoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransacaoController.class);

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final TransacaoMapper transacaoMapper;

    public TransacaoController(ContaRepository contaRepository, TransacaoRepository transacaoRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, TransacaoMapper transacaoMapper) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.transacaoMapper = transacaoMapper;
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponse criar(@PathVariable("conta") UUID conta, @RequestBody TransacaoRequest request) throws Exception {
        LOGGER.debug("Criar transacao: conta={}", conta);
        if (contaRepository.findById(conta).isEmpty()) {
            throw new ContaNotFoundException("Conta não encontrada!");
        }
        var entity = transacaoRepository.save(transacaoMapper.map(conta, request));
        var response = transacaoMapper.map(entity);
        gerarEvento(response);
        return response;
    }

    @DeleteMapping("/{transacao}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable("conta") UUID conta, @PathVariable("transacao") UUID transacao) throws Exception {
        LOGGER.debug("Estornar transacao: conta={}, transacao={}", conta, transacao);
        if (contaRepository.findById(conta).isEmpty()) {
            throw new ContaNotFoundException("Conta não encontrada!");
        }
        var entity = transacaoRepository.findById(transacao)
                .orElseThrow(() -> new ContaNotFoundException("Transação não encontrada!"));
        var estorno = transacaoRepository.save(transacaoMapper.mapEstorno(entity));
        gerarEvento(transacaoMapper.map(estorno));
    }

    @Transactional
    public void gerarEvento(TransacaoResponse response) throws Exception {
        var data = objectMapper.writeValueAsString(response);
        var result = kafkaTemplate.send("transacoes", response.conta().toString(), data)
                .get(11L, TimeUnit.SECONDS);
        LOGGER.debug("Evento de transacao enviada: id={}, metadata={}}", response.conta(), result.getRecordMetadata());
    }

    record Participante(String banco, String agencia, String conta) {
    }

    record TransacaoRequest(LocalDate dataTransacao, BigDecimal valor, TipoTransacaoEnum tipoTransacao,
                            Participante participante, String descricao) {
    }

    record TransacaoResponse(
            UUID id,
            UUID conta,
            TipoTransacaoEnum tipoTransacao,
            LocalDate dataTransacao,
            BigDecimal valor,
            Participante participante,
            String descricao,
            UUID transacaoReferente,
            LocalDateTime dataCriacao
    ) {
    }
}
