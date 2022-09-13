package org.exemplo.transacoes.conta;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface ContaRepository extends CrudRepository<ContaEntity, UUID> {
}
