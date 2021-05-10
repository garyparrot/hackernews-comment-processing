package org.example.webbackend.repositories;

import org.example.webbackend.domain.Keyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;

@Repository
public interface KeywordRepository extends CrudRepository<Keyword, Integer> {

    Page<Keyword> findAll(Pageable pageable);

}
