package org.exemplo.transacoes.limite;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "limite")
public class LimiteEntity {
    @Id
    private UUID conta;
    @Version
    private Integer versao;
    private BigDecimal saldo;
    private BigDecimal saldoDiario;
    private BigDecimal saldoChequeEspecial;

    public UUID getConta() {
        return conta;
    }

    public void setConta(UUID conta) {
        this.conta = conta;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getSaldoDiario() {
        return saldoDiario;
    }

    public void setSaldoDiario(BigDecimal saldoDiario) {
        this.saldoDiario = saldoDiario;
    }

    public BigDecimal getSaldoChequeEspecial() {
        return saldoChequeEspecial;
    }

    public void setSaldoChequeEspecial(BigDecimal saldoChequeEspecial) {
        this.saldoChequeEspecial = saldoChequeEspecial;
    }
}
