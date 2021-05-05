package org.example.webbackend.domain;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "keyword_matches")
public class KeywordMatch {

    @Id
    int id;
    long hackerNewsItemId;
    String keyword;
    String category;

}
