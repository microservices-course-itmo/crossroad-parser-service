package com.wine.to.up.crossroad.parser.service.db.repositories;

import com.wine.to.up.crossroad.parser.service.db.entities.Wine;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WineRepository extends CrudRepository<Wine, Long> {
}
