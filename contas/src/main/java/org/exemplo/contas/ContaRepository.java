package org.exemplo.contas;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface ContaRepository extends CrudRepository<ContaEntity, UUID> {

    @Query(value = "SELECT nextval('conta_seq_numero')", nativeQuery = true)
    Integer getProximoNumeroConta();
}
