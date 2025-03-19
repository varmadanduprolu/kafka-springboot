package com.learnkfka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkfka.entity.Book;
import com.learnkfka.entity.LibraryEvent;
import com.learnkfka.repository.BookRepository;
import com.learnkfka.repository.LibraryEventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class LibraryEventsService {

    private static final Logger log = LoggerFactory.getLogger(LibraryEventsService.class);
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private LibraryEventsRepository libraryEventsRepository;
    @Autowired
    private BookRepository bookRepository;

    public void processLibraryEvents(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
      LibraryEvent libraryEvent=   objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
      log.info("Library event : {}", libraryEvent);

      if (libraryEvent.getBook()!=null && libraryEvent.getLibraryEventId()==999){
            throw new RecoverableDataAccessException(
                    "Duplicate key exception",
                    new RuntimeException("Duplicate key exception"));
      }


      switch (libraryEvent.getLibraryEventType()){
          case NEW :
              save(libraryEvent);
              break;
          case UPDATE:
              validate(libraryEvent);
              save(libraryEvent);
              break;
          default:
              log.info("invalid library event types");
      }
    }

    private void validate(LibraryEvent libraryEvent) {
        if (libraryEvent.getLibraryEventId()==null)
            throw new IllegalArgumentException("Library Event Id is required");
        Optional<LibraryEvent> optionalLibraryEvent = libraryEventsRepository.findById(libraryEvent.getLibraryEventId());
        if (!optionalLibraryEvent.isPresent()){
            throw new IllegalArgumentException("not a valid Library Event ");
        }
        log.info("Successfully validated the library Event {} ", libraryEvent);
    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
        log.info("Successfully Persisted the libary Event {} ", libraryEvent);
    }
}
