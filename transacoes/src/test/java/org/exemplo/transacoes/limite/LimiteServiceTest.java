package org.exemplo.transacoes.limite;

import org.exemplo.transacoes.transacao.TipoTransacaoEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.exemplo.transacoes.transacao.TipoTransacaoEnum.CREDITO;
import static org.exemplo.transacoes.transacao.TipoTransacaoEnum.DEBITO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LimiteServiceTest {

    private static final UUID CONTA = UUID.randomUUID();

    @Mock
    private LimiteRepository limiteRepository;

    @InjectMocks
    private LimiteService limiteService;

    @ParameterizedTest
    @MethodSource("argumentsStream")
    void deve_validar_limite(int saldo, int saldoDiario, BigDecimal valor, TipoTransacaoEnum tipoTransacao, boolean resultado) {
        given(limiteRepository.findById(any()))
                .willReturn(getEntity(saldo, saldoDiario));

        assertThat(limiteService.validar(CONTA, valor, tipoTransacao), is(resultado));
    }

    @Test
    void deve_salvar_novo_limite() {
        var entity = getEntity(0, 500).get();
        entity.setVersao(1);
        given(limiteRepository.findById(CONTA))
                .willReturn(Optional.empty());
        given(limiteRepository.save(any()))
                .willReturn(entity);
        limiteService.salvar(CONTA, BigDecimal.TEN, DEBITO);
        verify(limiteRepository, times(1)).save(any());
    }

    @Test
    void deve_salvar_limite_em_um_debito() {
        var entity = getEntity(90, 490).get();
        given(limiteRepository.findById(CONTA))
                .willReturn(getEntity(100, 500));
        given(limiteRepository.save(entity))
                .willReturn(entity);
        limiteService.salvar(CONTA, BigDecimal.TEN, DEBITO);
        verify(limiteRepository, times(1)).save(entity);
    }

    @Test
    void deve_salvar_limite_em_um_credito() {
        var entity = getEntity(110, 500).get();
        given(limiteRepository.findById(CONTA))
                .willReturn(getEntity(100, 500));
        given(limiteRepository.save(entity))
                .willReturn(entity);
        limiteService.salvar(CONTA, BigDecimal.TEN, CREDITO);
        verify(limiteRepository, times(1)).save(entity);
    }

    private static Stream<Arguments> argumentsStream() {
        return Stream.of(
                Arguments.of(100, 500, BigDecimal.valueOf(200), DEBITO, true),
                Arguments.of(100, 500, BigDecimal.valueOf(200), CREDITO, true),
                Arguments.of(100, 500, BigDecimal.valueOf(600), DEBITO, false),
                Arguments.of(-900, 500, BigDecimal.valueOf(200), DEBITO, false),
                Arguments.of(-500, 500, BigDecimal.valueOf(600), DEBITO, false)
        );
    }

    private static Optional<LimiteEntity> getEntity(int saldo, int saldoDiario) {
        var entity = new LimiteEntity();
        entity.setConta(CONTA);
        entity.setVersao(1);
        entity.setSaldo(BigDecimal.valueOf(saldo));
        entity.setSaldoDiario(BigDecimal.valueOf(saldoDiario));
        return Optional.of(entity);
    }

}