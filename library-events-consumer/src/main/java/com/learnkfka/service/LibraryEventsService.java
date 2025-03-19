package com.learnkfka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LibraryEventsService {

    public void processLibraryEvents(ConsumerRecord<Integer,String> consumerRecord){

    }
}
