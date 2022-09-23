package org.exemplo.transacoes.conta;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Table(name = "conta")
public class ContaEntity {
    @Id
    UUID id;
    @Version
    Long versao;
    String agencia;
    String conta;
    String estado;
    LocalDateTime dataCriacao;

    public ContaEntity() {
    }

    public ContaEntity(UUID id, String agencia, String conta, String estado, LocalDateTime dataCriacao) {
        this.id = id;
        this.agencia = agencia;
        this.conta = conta;
        this.estado = estado;
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
