package org.example.webbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.domain.KeywordMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaConsumer {

    private final SimpMessagingTemplate template;

    @KafkaListener(topics = "keyword-matches", groupId = "web-backend_keyword-matches_group")
    public void consume(ConsumerRecord<String, KeywordMatch> record) throws IOException {
        KeywordMatch km = record.value();
        log.debug(String.format("Category: %s, Keyword: %s, Id: %d",
                km.getCategory(),
                km.getKeyword(),
                km.getHackerNewsItemId()));

        template.convertAndSend("/stream/keyword-matches", km.toString());
    }

}
