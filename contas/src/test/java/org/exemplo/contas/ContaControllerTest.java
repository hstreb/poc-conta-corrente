package org.exemplo.contas;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.exemplo.contas.ContaController.ContaRequest;
import org.exemplo.contas.ContaController.TitularRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ContaControllerTest {

    private static final UUID CONTA_ID = UUID.randomUUID();
    private static final UUID TITULAR_ID = UUID.randomUUID();
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final ListenableFuture<SendResult<String, String>> LISTENABLE_FUTURE = new ListenableFuture<>() {
        @Override
        public void addCallback(ListenableFutureCallback<? super SendResult<String, String>> callback) {
        }

        @Override
        public void addCallback(SuccessCallback<? super SendResult<String, String>> successCallback, FailureCallback failureCallback) {
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public SendResult<String, String> get() {
            return null;
        }

        @Override
        public SendResult<String, String> get(long timeout, TimeUnit unit) {
            return new SendResult<>(new ProducerRecord<>("", ""),
                    new RecordMetadata(new TopicPartition("contas", 0),
                            0L,
                            0,
                            0L,
                            28,
                            100));
        }
    };
    private static final String DOCUMENTO = "1122";
    private static final String NOME = "João";
    private static final String AGENCIA = "0001";
    private static final int NUMERO = 1234;
    private static final String ESTADO = "ATIVA";
    private static final ContaController.ContaResponse EXPERADO = new ContaController.ContaResponse(CONTA_ID,
            AGENCIA,
            "1234-9",
            Set.of(new ContaController.TitularResponse(TITULAR_ID, DOCUMENTO, NOME)),
            ESTADO,
            NOW);

    @Mock
    private ContaRepository contaRepository;
    @Mock
    private TitularRepository titularRepository;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @InjectMocks
    private ContaController contaController;

    @Test
    void deve_salvar_conta_corretamente() throws Exception {
        given(contaRepository.getProximoNumeroConta())
                .willReturn(NUMERO);
        given(contaRepository.save(any()))
                .willReturn(getContaEntity());
        given(titularRepository.save(any()))
                .willReturn(getTitularEntity());
        given(kafkaTemplate.send(any(), any(), any()))
                .willReturn(LISTENABLE_FUTURE);
        var resultado = contaController.criar(new ContaRequest(Set.of(new TitularRequest(DOCUMENTO, NOME))));
        assertThat(resultado, is(EXPERADO));
    }

    @Test
    void deve_buscar_conta_corretamente() {
        given(contaRepository.findById(any()))
                .willReturn(Optional.of(getContaEntity()));
        given(titularRepository.getAllByConta(any()))
                .willReturn(Set.of(getTitularEntity()));
        var resultado = contaController.buscar(CONTA_ID);
        assertThat(resultado, is(EXPERADO));
    }

    @Test
    void nao_deve_encontrar_conta_inexistente() {
        given(contaRepository.findById(any()))
                .willReturn(Optional.empty());
        assertThatThrownBy(() -> contaController.buscar(CONTA_ID))
                .isInstanceOf(ContaNotFoundException.class);
    }

    @Test
    void deve_deletar_conta_corretamente() {
        given(contaRepository.findById(any()))
                .willReturn(Optional.of(getContaEntity()));
        given(titularRepository.getAllByConta(any()))
                .willReturn(Set.of(getTitularEntity()));
        given(contaRepository.save(any()))
                .willReturn(getContaEntity());
        given(kafkaTemplate.send(any(), any(), any()))
                .willReturn(LISTENABLE_FUTURE);
        assertThatCode(() -> contaController.deletar(CONTA_ID))
                .doesNotThrowAnyException();
    }

    ContaEntity getContaEntity() {
        var entity = new ContaEntity();
        entity.id = CONTA_ID;
        entity.agencia = "0001";
        entity.numero = NUMERO;
        entity.digitoVerificador = 9;
        entity.estado = "ATIVA";
        entity.dataCriacao = NOW;
        return entity;
    }

    TitularEntity getTitularEntity() {
        var entity = new TitularEntity();
        entity.id = TITULAR_ID;
        entity.conta = CONTA_ID;
        entity.documento = DOCUMENTO;
        entity.nome = NOME;
        return entity;
    }
}