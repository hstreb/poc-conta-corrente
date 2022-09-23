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

    public ContaEntity(Integer numero) {
        this.agencia = "0001";
        this.numero = numero;
        this.digitoVerificador = (numero + 5) % 10;
        this.estado = "ATIVA";
        this.dataCriacao = LocalDateTime.now();
    }
}
