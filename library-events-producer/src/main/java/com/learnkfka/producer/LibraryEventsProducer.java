package com.learnkfka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkfka.controller.LibraryEventsController;
import com.learnkfka.domain.LibraryEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Component
public class LibraryEventsProducer {

    private static final Logger log = LoggerFactory.getLogger(LibraryEventsController.class);


    @Value("${spring.kafka.topic}")
    private String topicName;

    private KafkaTemplate<Integer, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public LibraryEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        var key = libraryEvent.libraryEventId();
        var value= objectMapper.writeValueAsString(libraryEvent);
       var completableFuture =  kafkaTemplate.send(topicName, key,value);

      return completableFuture.whenComplete((recordMetadata, exception) -> {
           if(exception == null) {
               // success
              handleSeccuss(key,value,recordMetadata);
               //System.out.println("Message sent successfully for the key: " + key + " and the value is: " + value + " partition is: " + recordMetadata.partition());
           } else {
               // failure
               handleFailure(key,value,exception);
              // System.out.println("Error sending the message and the exception is: " + exception.getMessage());
           }
       });
    }
//send approach to send message to kafka synchronous way
    public SendResult<Integer, String> sendLibraryEventApproach2(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        var key = libraryEvent.libraryEventId();
        var value= objectMapper.writeValueAsString(libraryEvent);
        var sendResult =  kafkaTemplate.send(topicName, key,value).get();
        handleSeccuss(key,value,sendResult);
        return sendResult;
    }

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEventApproach3(LibraryEvent libraryEvent) throws JsonProcessingException {
        var key = libraryEvent.libraryEventId();
        var value= objectMapper.writeValueAsString(libraryEvent);
        var producerRecord =  buildProducerRecord(key,value);
        var completableFuture =  kafkaTemplate.send(producerRecord);
        return completableFuture.whenComplete((recordMetadata, exception) -> {
            if(exception == null) {
                // success
                handleSeccuss(key,value,recordMetadata);
                //System.out.println("Message sent successfully for the key: " + key + " and the value is: " + value + " partition is: " + recordMetadata.partition());
            } else {
                // failure
                handleFailure(key,value,exception);
                // System.out.println("Error sending the message and the exception is: " + exception.getMessage());
            }
        });
    }

    private ProducerRecord<Integer,String> buildProducerRecord(Integer key, String value) {
       List<Header> recordHeaders = List.of(new RecordHeader("event-source","scanner".getBytes()));
        return new ProducerRecord<>(topicName,null,key,value,recordHeaders);
    }

    private void handleSeccuss(Integer key, String value, SendResult<Integer, String> recordMetadata) {
        log.info("Message sent successfully for the key: " + key + " and the value is: " + value + " partition is: " + recordMetadata.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable exception) {
        log.error("Error sending the message and the exception is: " + exception.getMessage());

    }
}
