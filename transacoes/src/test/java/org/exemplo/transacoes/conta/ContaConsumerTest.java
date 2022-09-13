package org.exemplo.transacoes.conta;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaConsumerTest {

    @Mock
    private ContaRepository contaRepository;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    @InjectMocks
    private ContaConsumer contaConsumer;

    @Test
    void deve_consumir_mensagem_e_salvar_nova_conta_corretamente() {
        var evento = """
                {"id":"6132176f-bed6-42ea-82d4-21631372da33","agencia":"0001","conta":"1234-4","titulares":[{"id":"642466af-fbc4-4e73-8ad6-7514c818a385","documento":"0001","nome":"Cliente"}],"estado":"ATIVA","dataCriacao":"2022-01-01T12:00:00.000000000"}
                """;
        var entity = new ContaEntity(UUID.fromString("6132176f-bed6-42ea-82d4-21631372da33"),
                "0001",
                "1234-4",
                "ATIVA",
                LocalDateTime.of(LocalDate.of(2022, 1, 1), LocalTime.NOON));
        given(contaRepository.findById(any()))
                .willReturn(Optional.empty());
        given(contaRepository.save(any()))
                .willReturn(entity);
        contaConsumer.consumir(evento);
        verify(contaRepository, times(1)).save(entity);
    }

    @Test
    void deve_consumir_mensagem_e_ignorar_conta_existente() {
        var evento = """
                {"id":"0d10b122-076b-43a3-89cc-1d6ecd77632f","agencia":"0001","conta":"0001-1","titulares":[{"id":"642466af-fbc4-4e73-8ad6-7514c818a385","documento":"0001","nome":"Cliente"}],"estado":"ATIVA","dataCriacao":"2022-01-01T12:00:00.000000000"}
                """;
        var entity = new ContaEntity(UUID.fromString("0d10b122-076b-43a3-89cc-1d6ecd77632f"),
                "0001",
                "0001-1",
                "ATIVA",
                LocalDateTime.of(LocalDate.of(2022, 1, 1), LocalTime.NOON));
        given(contaRepository.findById(any()))
                .willReturn(Optional.of(entity));
        contaConsumer.consumir(evento);
        verify(contaRepository, never()).save(entity);
    }

    @Test
    void deve_consumir_mensagem_e_atualizar_conta_existente() {
        var evento = """
                {"id":"0d10b122-076b-43a3-89cc-1d6ecd77632f","agencia":"0001","conta":"0001-1","titulares":[{"id":"642466af-fbc4-4e73-8ad6-7514c818a385","documento":"0001","nome":"Cliente"}],"estado":"INATIVA","dataCriacao":"2022-01-01T12:00:00.000000000"}
                """;
        var entityExistente = new ContaEntity(UUID.fromString("0d10b122-076b-43a3-89cc-1d6ecd77632f"),
                "0001",
                "0001-1",
                "ATIVA",
                LocalDateTime.of(LocalDate.of(2022, 1, 1), LocalTime.NOON));
        var entity = new ContaEntity(UUID.fromString("0d10b122-076b-43a3-89cc-1d6ecd77632f"),
                "0001",
                "0001-1",
                "INATIVA",
                LocalDateTime.of(LocalDate.of(2022, 1, 1), LocalTime.NOON));
        given(contaRepository.findById(any()))
                .willReturn(Optional.of(entityExistente));
        contaConsumer.consumir(evento);
        verify(contaRepository, times(1)).save(entity);
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
}