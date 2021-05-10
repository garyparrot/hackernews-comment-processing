package org.example.keywords;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostgresTableRecord {
    private long hacker_news_item_id;
    private String topic;
}
