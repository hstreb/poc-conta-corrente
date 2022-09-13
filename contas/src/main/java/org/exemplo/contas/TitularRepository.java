package org.exemplo.contas;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
interface TitularRepository extends CrudRepository<TitularEntity, UUID> {
    Set<TitularEntity> getAllByConta(UUID conta);
}
