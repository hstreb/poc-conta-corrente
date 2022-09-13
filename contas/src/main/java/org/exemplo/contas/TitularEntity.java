package org.exemplo.contas;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "titular")
class TitularEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;
    private UUID conta;
    private String documento;
    private String nome;

    public TitularEntity() {
    }

    public TitularEntity(UUID conta, String documento, String nome) {
        this.conta = conta;
        this.documento = documento;
        this.nome = nome;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getConta() {
        return conta;
    }

    public void setConta(UUID conta) {
        this.conta = conta;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
