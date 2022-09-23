package org.exemplo.transacoes.transacao;

import org.exemplo.transacoes.transacao.TransacaoController.TransacaoRequest;
import org.exemplo.transacoes.transacao.TransacaoController.TransacaoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransacaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "versao", ignore = true)
    @Mapping(target = "tipoTransacao", source = "request.tipoTransacao.id")
    @Mapping(target = "participanteBanco", source = "request.participante.banco")
    @Mapping(target = "participanteAgencia", source = "request.participante.agencia")
    @Mapping(target = "participanteConta", source = "request.participante.conta")
    @Mapping(target = "dataCriacao", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "transacaoReferente", ignore = true)
    TransacaoEntity map(UUID conta, TransacaoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "versao", ignore = true)
    @Mapping(target = "tipoTransacao", expression = "java(org.exemplo.transacoes.transacao.TipoTransacaoEnum.ESTORNO.id)")
    @Mapping(target = "dataTransacao", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "dataCriacao", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "transacaoReferente", source = "id")
    TransacaoEntity mapEstorno(TransacaoEntity entity);

    @Mapping(target = "tipoTransacao", source = "entity.tipoTransacao")
    @Mapping(target = "participante.banco", source = "participanteBanco")
    @Mapping(target = "participante.agencia", source = "participanteAgencia")
    @Mapping(target = "participante.conta", source = "participanteConta")
    TransacaoResponse map(TransacaoEntity entity);


    default TipoTransacaoEnum map(Integer tipoTransacao) {
        return TipoTransacaoEnum.valueOf(tipoTransacao)
                .orElse(TipoTransacaoEnum.CREDITO);
    }
}
