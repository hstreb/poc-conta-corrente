package org.exemplo.transacoes.limite;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Table(name = "limite")
public class LimiteEntity {
    @Id
    private UUID conta;
    @Version
    private Integer versao;
    private BigDecimal saldo;
    private BigDecimal saldoDiario;

    public LimiteEntity() {
    }

    public LimiteEntity(UUID conta, BigDecimal saldo, BigDecimal saldoDiario) {
        this.conta = conta;
        this.saldo = saldo;
        this.saldoDiario = saldoDiario;
    }

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
        if (saldoDiario.compareTo(BigDecimal.valueOf(500)) <= 0)
            this.saldoDiario = saldoDiario;
        else
            this.saldoDiario = BigDecimal.valueOf(500);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LimiteEntity that = (LimiteEntity) o;
        return Objects.equals(conta, that.conta) && Objects.equals(versao, that.versao) && Objects.equals(saldo, that.saldo) && Objects.equals(saldoDiario, that.saldoDiario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conta, versao, saldo, saldoDiario);
    }
}
