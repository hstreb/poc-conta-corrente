package org.exemplo.transacoes.conta;

import org.exemplo.transacoes.limite.LimiteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContaMapper {

    @Mapping(target = "versao", ignore = true)
    ContaEntity map(Conta conta);

    /*
        private UUID conta;
    @Version
    private Integer versao;
    private BigDecimal saldo;
    private BigDecimal saldoDiario;
    private BigDecimal saldoChequeEspecial;
     */
    @Mapping(target = "conta", source = "id")
    @Mapping(target = "versao", ignore = true)
    @Mapping(target = "saldo", constant = "0.0")
    @Mapping(target = "saldoDiario", constant = "0.0")
    @Mapping(target = "saldoChequeEspecial", constant = "1000.0")
    LimiteEntity mapLimite(ContaEntity conta);
}
