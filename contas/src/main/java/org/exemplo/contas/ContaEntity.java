package org.exemplo.contas;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "conta")
class ContaEntity {
    @Id
    UUID id;
    @Version
    Long versao;
    String agencia;
    Integer numero;
    Integer digitoVerificador;
    String estado;

    LocalDateTime dataCriacao;

    public ContaEntity() {
    }

    public ContaEntity(String agencia, Integer numero, String estado, Integer digitoVerificador, LocalDateTime dataCriacao) {
        this.agencia = agencia;
        this.numero = numero;
        this.digitoVerificador = digitoVerificador;
        this.estado = estado;
        this.dataCriacao = dataCriacao;
    }
}
