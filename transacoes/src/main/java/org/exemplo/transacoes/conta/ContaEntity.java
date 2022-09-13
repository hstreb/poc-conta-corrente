package org.exemplo.transacoes.conta;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conta")
class ContaEntity {
    @Id
    private UUID id;
    private String agencia;
    private String conta;
    private String estado;
    private LocalDateTime dataCriacao;

    public ContaEntity() {
    }

    public ContaEntity(UUID id, String agencia, String conta, String estado, LocalDateTime dataCriacao) {
        this.id = id;
        this.agencia = agencia;
        this.conta = conta;
        this.estado = estado;
        this.dataCriacao = dataCriacao;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContaEntity that = (ContaEntity) o;
        return Objects.equals(id, that.id)
                && Objects.equals(agencia, that.agencia)
                && Objects.equals(conta, that.conta)
                && Objects.equals(estado, that.estado)
                && Objects.equals(dataCriacao, that.dataCriacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, agencia, conta, estado, dataCriacao);
    }
}
