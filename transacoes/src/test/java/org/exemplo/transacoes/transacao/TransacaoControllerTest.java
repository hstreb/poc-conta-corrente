package org.exemplo.transacoes.transacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.exemplo.transacoes.conta.ContaEntity;
import org.exemplo.transacoes.conta.ContaNotFoundException;
import org.exemplo.transacoes.conta.ContaRepository;
import org.exemplo.transacoes.transacao.TransacaoController.TransacaoRequest;
import org.exemplo.transacoes.transacao.TransacaoController.TransacaoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.exemplo.transacoes.transacao.TipoTransacaoEnum.CREDITO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {

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
                    new RecordMetadata(new TopicPartition("transacoes", 0),
                            0L,
                            0,
                            0L,
                            28,
                            100));
        }
    };
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Mock
    private ContaRepository contaRepository;
    @Mock
    private TransacaoRepository transacaoRepository;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @Spy
    private TransacaoMapper transacaoMapper = Mappers.getMapper(TransacaoMapper.class);

    @InjectMocks
    private TransacaoController transacaoController;

    @Test
    void deve_criar_uma_transacao_corretamente() throws Exception {
        given(contaRepository.findById(any()))
                .willReturn(Optional.of(getContaEntity()));
        given(transacaoRepository.save(any()))
                .willReturn(getTransacaoEntity());
        given(kafkaTemplate.send(any(), any(), any()))
                .willReturn(LISTENABLE_FUTURE);
        var resultado = transacaoController.criar(UUID.fromString("f29283b3-6e25-4400-8e6e-81224f97ebeb"),
                new TransacaoRequest(LocalDate.of(2022, 1, 1), TEN, CREDITO, null, null));
        assertThat(resultado, is(new TransacaoResponse(UUID.fromString("0d10b122-076b-43a3-89cc-1d6ecd77632f"),
                UUID.fromString("f29283b3-6e25-4400-8e6e-81224f97ebeb"),
                CREDITO,
                LocalDate.of(2022, 1, 1),
                TEN,
                new TransacaoController.Participante(null, null, null),
                null,
                null,
                NOW)));
    }

    @Test
    void nao_deve_criar_transacao_para_conta_inexistente() {
        given(contaRepository.findById(any()))
                .willReturn(Optional.empty());
        assertThatThrownBy(() -> transacaoController.criar(UUID.fromString("f29283b3-6e25-4400-8e6e-81224f97ebeb"),
                new TransacaoRequest(LocalDate.of(2022, 1, 1), TEN, CREDITO, null, null)))
                .isInstanceOf(ContaNotFoundException.class);
    }

    @Test
    void deve_estornar_uma_transacao_corretamente() throws Exception {
        given(contaRepository.findById(any()))
                .willReturn(Optional.of(getContaEntity()));
        given(transacaoRepository.findById(any()))
                .willReturn(Optional.of(getTransacaoEntity()));
        given(transacaoRepository.save(any()))
                .willReturn(getTransacaoEntity());
        given(kafkaTemplate.send(any(), any(), any()))
                .willReturn(LISTENABLE_FUTURE);
        assertThatCode(() -> transacaoController.deletar(UUID.fromString("f29283b3-6e25-4400-8e6e-81224f97ebeb"),
                UUID.fromString("0d10b122-076b-43a3-89cc-1d6ecd77632f")))
                .doesNotThrowAnyException();
    }

    @Test
    void nao_deve_estornar_transacao_para_conta_inexistente() {
        given(contaRepository.findById(any()))
                .willReturn(Optional.empty());
        assertThatThrownBy(() -> transacaoController.deletar(UUID.fromString("f29283b3-6e25-4400-8e6e-81224f97ebeb"),
                UUID.fromString("0d10b122-076b-43a3-89cc-1d6ecd77632f")))
                .isInstanceOf(ContaNotFoundException.class);
    }

    ContaEntity getContaEntity() {
        var entity = new ContaEntity();
        entity.setId(UUID.fromString("f29283b3-6e25-4400-8e6e-81224f97ebeb"));
        entity.setAgencia("0001");
        entity.setConta("1234-5");
        entity.setEstado("ATIVA");
        entity.setDataCriacao(NOW);
        return entity;
    }

    TransacaoEntity getTransacaoEntity() {
        var entity = new TransacaoEntity();
        entity.setId(UUID.fromString("0d10b122-076b-43a3-89cc-1d6ecd77632f"));
        entity.setVersao(0L);
        entity.setConta(UUID.fromString("f29283b3-6e25-4400-8e6e-81224f97ebeb"));
        entity.setTipoTransacao(CREDITO.id);
        entity.setDataTransacao(LocalDate.of(2022, 1, 1));
        entity.setValor(TEN);
        entity.setDataCriacao(NOW);
        return entity;
    }
}