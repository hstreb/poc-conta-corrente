package org.exemplo.transacoes.transacao;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "transacao")
public class TransacaoEntity {

    @Id
    private UUID id;
    @Version
    private Long versao;
    private UUID conta;
    private Integer tipoTransacao;
    private LocalDate dataTransacao;
    private BigDecimal valor;
    private String participanteBanco;
    private String participanteAgencia;
    private String participanteConta;
    private String descricao;
    private UUID transacaoReferente;
    private LocalDateTime dataCriacao;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public UUID getConta() {
        return conta;
    }

    public void setConta(UUID conta) {
        this.conta = conta;
    }

    public Integer getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(Integer tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public LocalDate getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDate dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getParticipanteBanco() {
        return participanteBanco;
    }

    public void setParticipanteBanco(String participanteBanco) {
        this.participanteBanco = participanteBanco;
    }

    public String getParticipanteAgencia() {
        return participanteAgencia;
    }

    public void setParticipanteAgencia(String participanteAgencia) {
        this.participanteAgencia = participanteAgencia;
    }

    public String getParticipanteConta() {
        return participanteConta;
    }

    public void setParticipanteConta(String participanteConta) {
        this.participanteConta = participanteConta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UUID getTransacaoReferente() {
        return transacaoReferente;
    }

    public void setTransacaoReferente(UUID transacaoReferente) {
        this.transacaoReferente = transacaoReferente;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
