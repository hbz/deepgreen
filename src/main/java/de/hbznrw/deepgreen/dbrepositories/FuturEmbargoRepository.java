package de.hbznrw.deepgreen.dbrepositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.hbznrw.deepgreen.dbentities.FuturEmbargo;

@Repository
public interface FuturEmbargoRepository extends CrudRepository<FuturEmbargo, String>{

}
