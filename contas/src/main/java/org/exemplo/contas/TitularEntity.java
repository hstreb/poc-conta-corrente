package org.exemplo.contas;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "titular")
class TitularEntity {
    @Id
    UUID id;
    @Version
    Long versao;
    UUID conta;
    String documento;
    String nome;

    public TitularEntity() {
    }

    public TitularEntity(UUID conta, String documento, String nome) {
        this.conta = conta;
        this.documento = documento;
        this.nome = nome;
    }
}
