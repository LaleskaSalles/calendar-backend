package com.example.calendar.event;

import com.example.calendar.user.User;

import java.time.LocalDateTime;

public record EventResponseDTO(Long id, String title, LocalDateTime start, LocalDateTime end, User user) {
    public EventResponseDTO(Event event){
        this(event.getId(), event.getTitle(), event.getStart(), event.getEnd(), event.getUser());
    }
}
