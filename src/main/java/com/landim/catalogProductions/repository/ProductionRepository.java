package com.landim.catalogProductions.repository;

import com.landim.catalogProductions.entity.Production;
import com.landim.catalogProductions.entity.Type;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductionRepository extends MongoRepository<Production, String> {
    boolean existsByTitle(String title);
    List<Production> findByType(Type type);
    List<Production> findByGradeGreaterThanEqual(Double grade);
    List<Production> findByTypeAndGradeGreaterThanEqual(Type type, Double grade);
    List<Production> findByTitleContainingIgnoreCase(String title);
}
