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
    private Integer bookId;
    private String bookName;
    private String bookAuthor;
    @OneToOne
    @JoinColumn(name = "libraryEventId")
    private LibraryEvent libraryEvent;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public LibraryEvent getLibraryEvent() {
        return libraryEvent;
    }

    public void setLibraryEvent(LibraryEvent libraryEvent) {
        this.libraryEvent = libraryEvent;
    }
}
