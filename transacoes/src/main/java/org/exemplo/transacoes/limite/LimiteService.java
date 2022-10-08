package org.exemplo.transacoes.limite;

import org.exemplo.transacoes.transacao.TipoTransacaoEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class LimiteService {

    private final LimiteRepository limiteRepository;

    public LimiteService(LimiteRepository limiteRepository) {
        this.limiteRepository = limiteRepository;
    }

    public boolean validar(UUID conta, BigDecimal valor, TipoTransacaoEnum tipoTransacao) {
        return limiteRepository.findById(conta)
                .filter(l -> filter(l, valor, tipoTransacao))
                .isPresent();
    }

    @Transactional
    public void salvar(UUID conta, BigDecimal valor, TipoTransacaoEnum tipoTransacao) {
        var limite = limiteRepository.findById(conta)
                .map(l -> map(l, valor, tipoTransacao))
                .orElse(new LimiteEntity(conta, BigDecimal.ZERO.subtract(valor), BigDecimal.valueOf(500.0).subtract(valor)));
        limiteRepository.save(limite);
    }

    private LimiteEntity map(LimiteEntity limite, BigDecimal valor, TipoTransacaoEnum tipoTransacao) {
        var resultado = new LimiteEntity();
        resultado.setConta(limite.getConta());
        resultado.setVersao(limite.getVersao());
        if (TipoTransacaoEnum.CREDITO.equals(tipoTransacao)) {
            resultado.setSaldo(limite.getSaldo().add(valor));
            resultado.setSaldoDiario(limite.getSaldoDiario().add(valor));
        } else if (TipoTransacaoEnum.DEBITO.equals(tipoTransacao)) {
            resultado.setSaldo(limite.getSaldo().subtract(valor));
            resultado.setSaldoDiario(limite.getSaldoDiario().subtract(valor));
        }
        return resultado;
    }

    private boolean filter(LimiteEntity limite, BigDecimal valor, TipoTransacaoEnum tipoTransacao) {
        return !(TipoTransacaoEnum.DEBITO.equals(tipoTransacao)
                && (limite.getSaldoDiario().compareTo(valor) < 0
                || limite.getSaldo().add(BigDecimal.valueOf(1_000)).compareTo(valor) < 0));
    }
}
