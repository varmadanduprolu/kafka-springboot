package com.learnkfka.unit.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkfka.controller.LibraryEventsController;
import com.learnkfka.domain.LibraryEvent;
import com.learnkfka.producer.LibraryEventsProducer;
import com.learnkfka.unit.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    LibraryEventsProducer libraryEventProducer;

    @Test
    void postLibraryEvent() throws Exception {
        //given

        LibraryEvent libraryEvent = TestUtil.libraryEventRecord();

        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);

        //expect
        mockMvc.perform(post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @Test
    void postLibraryEvent_4xx() throws Exception {
        //given

        LibraryEvent libraryEvent = TestUtil.libraryEventRecordWithInvalidBook();

        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);
        //expect
        String expectedErrorMessage = "book.bookId - must not be null, book.bookName - must not be blank";
        mockMvc.perform(post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));

    }

    @Test
    void updateLibraryEvent() throws Exception {

        //given


        String json = objectMapper.writeValueAsString(TestUtil.libraryEventRecordUpdate());
        when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);

        //expect
        mockMvc.perform(
                        put("/v1/libraryevent")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void updateLibraryEvent_withNullLibraryEventId() throws Exception {

        //given

        String json = objectMapper.writeValueAsString(TestUtil.libraryEventRecordUpdateWithNullLibraryEventId());
        when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);

        //expect
        mockMvc.perform(
                        put("/v1/libraryevent")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Please pass the LibraryEventId"));

    }

    @Test
    void updateLibraryEvent_withNullInvalidEventType() throws Exception {

        //given

        String json = objectMapper.writeValueAsString(TestUtil.newLibraryEventRecordWithLibraryEventId());
        //when(libraryEventProducer.sendLibraryEvent_Approach2(isA(LibraryEvent.class))).thenReturn(null);

        //expect
        mockMvc.perform(
                        put("/v1/libraryevent")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Only UPDATE event type is supported"));

    }
}
