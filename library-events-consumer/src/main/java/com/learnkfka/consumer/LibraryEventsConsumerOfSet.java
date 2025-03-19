package com.learnkfka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
public class LibraryEventsConsumerOfSet {

    private static final Logger log = LoggerFactory.getLogger(LibraryEventsConsumerOfSet.class);

//    @KafkaListener(topics = {"library-events"})
//    public void onMessage(ConsumerRecord<Integer,String> consumerRecord){
//        log.info("ConsumerRecord : {}", consumerRecord);
//
//    }

//    @Override
//    @KafkaListener(topics = {"library-events"})
//    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {
//        log.info("ConsumerRecord : {}", consumerRecord);
//        acknowledgment.acknowledge();
//    }
}
