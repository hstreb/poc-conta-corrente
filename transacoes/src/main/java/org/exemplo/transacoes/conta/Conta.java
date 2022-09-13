package org.exemplo.transacoes.conta;

import java.time.LocalDateTime;
import java.util.UUID;

record Conta(UUID id, String agencia, String conta, String estado, LocalDateTime dataCriacao) {
}
