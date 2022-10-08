package org.exemplo.transacoes.conta;

import org.exemplo.transacoes.limite.LimiteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContaMapper {

    @Mapping(target = "versao", ignore = true)
    ContaEntity map(Conta conta);

    @Mapping(target = "conta", source = "id")
    @Mapping(target = "versao", ignore = true)
    @Mapping(target = "saldo", constant = "0.0")
    @Mapping(target = "saldoDiario", constant = "500.0")
    LimiteEntity mapLimite(ContaEntity conta);
}
