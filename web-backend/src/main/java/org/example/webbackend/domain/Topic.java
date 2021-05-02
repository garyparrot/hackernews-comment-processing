package org.example.webbackend.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "topic_list")
public class Topic {

    @Id
    String topicName;

}
