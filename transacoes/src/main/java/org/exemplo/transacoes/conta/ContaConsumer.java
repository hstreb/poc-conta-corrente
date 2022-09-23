package org.exemplo.transacoes.conta;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContaConsumer.class);

    private final ObjectMapper objectMapper;
    private final ContaRepository contaRepository;
    private final ContaMapper contaMapper;

    public ContaConsumer(ObjectMapper objectMapper, ContaRepository contaRepository, ContaMapper contaMapper) {
        this.objectMapper = objectMapper;
        this.contaRepository = contaRepository;
        this.contaMapper = contaMapper;
    }

    @KafkaListener(id = "trancacoes", topics = "contas")
    public void consumir(String conta) {
        LOGGER.debug("Consumindo evento de conta: {}", conta);
        try {
            var novaConta = contaMapper.map(objectMapper.readValue(conta, Conta.class));
            contaRepository.findById(novaConta.getId())
                    .filter(e -> e.equals(novaConta))
                    .orElseGet(() -> contaRepository.save(novaConta));
        } catch (JsonProcessingException e) {
            LOGGER.error("Erro ao consumir evento de conta", e);
        }
    }
}
