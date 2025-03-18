package com.learnkfka.intg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkfka.domain.Book;
import com.learnkfka.domain.LibraryEvent;
import com.learnkfka.domain.LibraryEventType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
//@EmbeddedKafka(topics = {"library-events"}, partitions = 1)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
})
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        var configs = new HashMap<String, Object>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "library-events-listener-group");
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        consumer = new DefaultKafkaConsumerFactory<Integer, String>(configs)
                .createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void postLibraryEvent() throws Exception {
        // given
        Book book = new Book(123, "Kafka Using Spring Boot", "Dilip");
        LibraryEvent libraryEvent = new LibraryEvent(null, LibraryEventType.NEW, book);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        // when
        ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange(
                "/v1/libraryevent",
                HttpMethod.POST,
                request,
                LibraryEvent.class
        );

        // then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);
        assert consumerRecords.count() == 2;
        consumerRecords.forEach(record -> {
            var libraryEventActual = record.value();
            assert libraryEventActual.contains("Kafka Using Spring Boot");
        });
    }

    @Test
    void postLibraryEvent_4xx() throws Exception {
        // given
        Book book = new Book(null, null, "Dilip");
        LibraryEvent libraryEvent = new LibraryEvent(null, LibraryEventType.NEW, book);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        // when
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "/v1/libraryevent",
                HttpMethod.POST,
                request,
                String.class
        );

        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}