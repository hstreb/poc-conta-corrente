package org.exemplo.transacoes.conta;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.exemplo.transacoes.limite.LimiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ContaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContaConsumer.class);

    private final ObjectMapper objectMapper;
    private final ContaRepository contaRepository;
    private final ContaMapper contaMapper;
    private final LimiteRepository limiteRepository;

    public ContaConsumer(ObjectMapper objectMapper, ContaRepository contaRepository, ContaMapper contaMapper, LimiteRepository limiteRepository) {
        this.objectMapper = objectMapper;
        this.contaRepository = contaRepository;
        this.contaMapper = contaMapper;
        this.limiteRepository = limiteRepository;
    }

    @KafkaListener(id = "trancacoes", topics = "contas")
    @Transactional
    public void consumir(String conta) {
        LOGGER.debug("Consumindo evento de conta: {}", conta);
        try {
            var novaConta = contaMapper.map(objectMapper.readValue(conta, Conta.class));
            var contaAtual = contaRepository.findById(novaConta.getId());
            if (contaAtual.isEmpty()) {
                contaRepository.save(novaConta);
                limiteRepository.save(contaMapper.mapLimite(novaConta));
            } else if (contaAtual.filter(e -> e.equals(novaConta)).isEmpty()) {
                contaRepository.save(novaConta);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Erro ao consumir evento de conta", e);
        }
    }
}
