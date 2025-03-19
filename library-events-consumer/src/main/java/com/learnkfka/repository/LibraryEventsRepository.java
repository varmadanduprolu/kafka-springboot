package com.learnkfka.repository;

import com.learnkfka.entity.LibraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryEventsRepository extends JpaRepository<Integer, LibraryEvent> {
}
