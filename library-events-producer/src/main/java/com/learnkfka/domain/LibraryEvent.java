package com.learnkfka.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record LibraryEvent(
        Integer libraryEventId,
        @NotNull LibraryEventType libraryEventType,
        @Valid @NotNull Book book
) {
}

