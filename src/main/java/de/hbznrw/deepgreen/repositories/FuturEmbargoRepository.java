package de.hbznrw.deepgreen.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hbznrw.deepgreen.entities.FuturEmbargo;

@Repository
public interface FuturEmbargoRepository extends CrudRepository<FuturEmbargo, String>{

}
