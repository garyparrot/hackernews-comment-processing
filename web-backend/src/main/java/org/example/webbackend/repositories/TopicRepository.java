package org.example.webbackend.repositories;

import org.example.webbackend.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends org.springframework.data.repository.Repository<Topic, String> {

    Iterable<Topic> findAll();

}
