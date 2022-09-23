package org.exemplo.transacoes.conta;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContaMapper {

    ContaEntity map(Conta conta);
}
