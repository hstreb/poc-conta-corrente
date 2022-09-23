package org.exemplo.transacoes.limite;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LimiteRepository extends CrudRepository<LimiteEntity, UUID> {
}
