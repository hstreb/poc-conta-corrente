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

    public TransacaoController(ContaRepository contaRepository, TransacaoRepository transacaoRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    TransacaoResponse criar(@PathVariable("conta") UUID conta, @RequestBody TransacaoRequest request) throws Exception {
        LOGGER.debug("Criar transacao: conta={}", conta);
        contaRepository.findById(conta)
                .orElseThrow(() -> new ContaNotFoundException("Conta não encontrada!"));
        var entity = transacaoRepository.save(new TransacaoEntity(conta,
                request.tipoTransacao().id,
                request.data(),
                request.valor(),
                request.participante() == null ? null : request.participante().banco(),
                request.participante() == null ? null : request.participante().agencia(),
                request.participante() == null ? null : request.participante().conta(),
                request.descricao()));
        var response = new TransacaoResponse(entity.id,
                entity.conta,
                TipoTransacaoEnum.valueOf(entity.tipoTransacao).orElse(TipoTransacaoEnum.CREDITO),
                entity.dataTransacao,
                entity.valor,
                new Participante(entity.participanteBanco, entity.participanteAgencia, entity.participanteConta),
                entity.descricao,
                null,
                entity.dataCriacao);
        gerarEvento(response);
        return response;
    }

    @DeleteMapping("/{transacao}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deletar(@PathVariable("conta") UUID conta, @PathVariable("transacao") UUID transacao) throws Exception {
        LOGGER.debug("Estornar transacao: conta={}, transacao={}", conta, transacao);
        contaRepository.findById(conta)
                .orElseThrow(() -> new ContaNotFoundException("Conta não encontrada!"));
        var entity = transacaoRepository.findById(transacao)
                .orElseThrow(() -> new ContaNotFoundException("Transação não encontrada!"));
        var nova = new TransacaoEntity(conta,
                entity.valor,
                entity.participanteBanco,
                entity.participanteAgencia,
                entity.participanteConta,
                transacao);
        var estorno = transacaoRepository.save(nova);
        var response = new TransacaoResponse(estorno.id,
                estorno.conta,
                TipoTransacaoEnum.valueOf(estorno.tipoTransacao).orElse(TipoTransacaoEnum.ESTORNO),
                estorno.dataTransacao,
                estorno.valor,
                new Participante(estorno.participanteBanco, estorno.participanteAgencia, estorno.participanteConta),
                estorno.descricao,
                estorno.transacaoReferente,
                estorno.dataCriacao);
        gerarEvento(response);
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

    record TransacaoRequest(LocalDate data, BigDecimal valor, TipoTransacaoEnum tipoTransacao,
                            Participante participante, String descricao) {
    }

    record TransacaoResponse(
            UUID id,
            UUID conta,
            TipoTransacaoEnum tipoTransacao,
            LocalDate data,
            BigDecimal valor,
            Participante participante,
            String descricao,
            UUID transacaoReferente,
            LocalDateTime dataCriacao
    ) {
    }
}
