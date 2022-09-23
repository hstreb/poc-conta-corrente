package org.exemplo.transacoes.conta;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.exemplo.transacoes.limite.LimiteEntity;
import org.exemplo.transacoes.limite.LimiteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaConsumerTest {

    private static final LocalDateTime NOW = LocalDateTime.of(LocalDate.of(2022, 1, 1), LocalTime.NOON);

    @Mock
    private ContaRepository contaRepository;
    @Mock
    private LimiteRepository limiteRepository;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    @Spy
    private ContaMapper contaMapper = Mappers.getMapper(ContaMapper.class);
    @InjectMocks
    private ContaConsumer contaConsumer;

    @Test
    void deve_consumir_mensagem_e_salvar_nova_conta_corretamente() {
        var evento = """
                {"id":"6132176f-bed6-42ea-82d4-21631372da33","agencia":"0001","conta":"1234-9","titulares":[{"id":"642466af-fbc4-4e73-8ad6-7514c818a385","documento":"0001","nome":"Cliente"}],"estado":"ATIVA","dataCriacao":"2022-01-01T12:00:00.000000000"}
                """;
        var entity = getContaEntity();
        var limiteEntity = getLimiteEntity();
        given(contaRepository.findById(any()))
                .willReturn(Optional.empty());
        given(contaRepository.save(any()))
                .willReturn(entity);
        given(limiteRepository.save(any()))
                .willReturn(limiteEntity);
        contaConsumer.consumir(evento);
        verify(contaRepository, times(1)).save(entity);
        verify(limiteRepository, times(1)).save(any());
    }

    @Test
    void deve_consumir_mensagem_e_ignorar_conta_existente() {
        var evento = """
                {"id":"0d10b122-076b-43a3-89cc-1d6ecd77632f","agencia":"0001","conta":"0001-1","titulares":[{"id":"642466af-fbc4-4e73-8ad6-7514c818a385","documento":"0001","nome":"Cliente"}],"estado":"ATIVA","dataCriacao":"2022-01-01T12:00:00.000000000"}
                """;
        var entity = getContaEntity();
        given(contaRepository.findById(any()))
                .willReturn(Optional.of(entity));
        contaConsumer.consumir(evento);
        verify(contaRepository, never()).save(entity);
        verify(limiteRepository, never()).save(any());
    }

    @Test
    void deve_consumir_mensagem_e_atualizar_conta_existente() {
        var evento = """
                {"id":"6132176f-bed6-42ea-82d4-21631372da33","agencia":"0001","conta":"1234-9","titulares":[{"id":"642466af-fbc4-4e73-8ad6-7514c818a385","documento":"0001","nome":"Cliente"}],"estado":"INATIVA","dataCriacao":"2022-01-01T12:00:00.000000000"}
                """;
        var entityExistente = getContaEntity();
        var entity = getContaEntity();
        entity.setEstado("INATIVA");
        given(contaRepository.findById(any()))
                .willReturn(Optional.of(entityExistente));
        contaConsumer.consumir(evento);
        verify(contaRepository, times(1)).save(entity);
        verify(limiteRepository, never()).save(any());
    }

    @Test
    void deve_consumir_mensagem_e_validar_uuid() {
        var evento = """
                {"id":"xxx","agencia":"0001","conta":"0001-1","titulares":[{"id":"642466af-fbc4-4e73-8ad6-7514c818a385","documento":"0001","nome":"Cliente"}],"estado":"ATIVA","dataCriacao":"2022-01-01T12:00:00.000000000"}
                """;
        contaConsumer.consumir(evento);
        verify(contaRepository, never()).findById(any());
        verify(contaRepository, never()).save(any());
    }

    private ContaEntity getContaEntity() {
        var entity = new ContaEntity();
        entity.setId(UUID.fromString("6132176f-bed6-42ea-82d4-21631372da33"));
        entity.setAgencia("0001");
        entity.setConta("1234-9");
        entity.setEstado("ATIVA");
        entity.setDataCriacao(NOW);
        return entity;
    }

    private LimiteEntity getLimiteEntity() {
        var entity = new LimiteEntity();
        entity.setConta(UUID.fromString("6132176f-bed6-42ea-82d4-21631372da33"));
        entity.setSaldo(ZERO);
        entity.setSaldoDiario(ZERO);
        entity.setSaldoChequeEspecial(new BigDecimal("1000.0"));
        return entity;
    }
}