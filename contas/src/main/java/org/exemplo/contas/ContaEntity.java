package org.exemplo.contas;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "conta")
class ContaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;
    private String agencia;
    private Integer numero;
    private Integer digitoVerificador;
    private String estado;

    private LocalDateTime dataCriacao;

    public ContaEntity() {
    }

    public ContaEntity(String agencia, Integer numero, String estado, Integer digitoVerificador, LocalDateTime dataCriacao) {
        this.agencia = agencia;
        this.numero = numero;
        this.digitoVerificador = digitoVerificador;
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(Integer digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
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
}
