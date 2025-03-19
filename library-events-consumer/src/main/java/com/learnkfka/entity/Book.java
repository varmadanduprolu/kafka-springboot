package com.learnkfka.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue
    private Integer bookId;
    private  String bookName;
    private String bookAuthor;
   @OneToOne
   @JoinColumn(name = "libraryEventId")
    private LibraryEvent libraryEvent;
}
