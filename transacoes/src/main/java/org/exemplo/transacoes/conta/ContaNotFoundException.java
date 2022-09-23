package org.exemplo.transacoes.conta;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ContaNotFoundException extends RuntimeException {
    public ContaNotFoundException(String mensagem) {
        super(mensagem);
    }
}
