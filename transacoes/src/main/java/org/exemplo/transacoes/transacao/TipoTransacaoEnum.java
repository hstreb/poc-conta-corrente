package org.exemplo.transacoes.transacao;

import java.util.Arrays;
import java.util.Optional;

public enum TipoTransacaoEnum {
    CREDITO(1),
    DEBITO(2),
    ESTORNO(3);

    public final Integer id;

    TipoTransacaoEnum(Integer id) {
        this.id = id;
    }

    public static Optional<TipoTransacaoEnum> valueOf(Integer id) {
        return Arrays.stream(values())
                .filter(t -> t.id.equals(id))
                .findFirst();
    }
}
