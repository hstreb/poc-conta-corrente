package org.exemplo.transacoes.transacao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransacaoRepository extends CrudRepository<TransacaoEntity, UUID> {
}
