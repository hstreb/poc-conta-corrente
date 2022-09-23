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
    UUID id;
    @Version
    Long versao;
    UUID conta;
    Integer tipoTransacao;
    LocalDate dataTransacao;
    BigDecimal valor;
    String participanteBanco;
    String participanteAgencia;
    String participanteConta;
    String descricao;
    UUID transacaoReferente;
    LocalDateTime dataCriacao;

    public TransacaoEntity() {
    }

    public TransacaoEntity(UUID conta,
                           Integer tipoTransacao,
                           LocalDate dataTransacao,
                           BigDecimal valor,
                           String participanteBanco,
                           String participanteAgencia,
                           String participanteConta,
                           String descricao) {
        this.conta = conta;
        this.tipoTransacao = tipoTransacao;
        this.dataTransacao = dataTransacao;
        this.valor = valor;
        this.participanteBanco = participanteBanco;
        this.participanteAgencia = participanteAgencia;
        this.participanteConta = participanteConta;
        this.descricao = descricao;
        this.dataCriacao = LocalDateTime.now();
    }

    public TransacaoEntity(UUID conta,
                           BigDecimal valor,
                           String participanteBanco,
                           String participanteAgencia,
                           String participanteConta,
                           UUID transacaoReferente) {
        this.conta = conta;
        this.tipoTransacao = TipoTransacaoEnum.ESTORNO.id;
        this.dataTransacao = LocalDate.now();
        this.valor = valor;
        this.participanteBanco = participanteBanco;
        this.participanteAgencia = participanteAgencia;
        this.participanteConta = participanteConta;
        this.descricao = "Estorno transação %s".formatted(transacaoReferente);
        this.transacaoReferente = transacaoReferente;
        this.dataCriacao = LocalDateTime.now();
    }
}
