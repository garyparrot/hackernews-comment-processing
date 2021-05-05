package org.example.webbackend.repositories;

import org.example.webbackend.domain.KeywordMatch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordMatchRepository extends org.springframework.data.repository.Repository<KeywordMatch, Integer> {

    List<KeywordMatch> findAllByOrderByIdDesc(Pageable pageable);
    List<KeywordMatch> findAllByCategoryOrderByIdDesc(String category, Pageable pageable);
    KeywordMatch findById(int id);
}
