package com.example.calendar.event;

import java.time.LocalDateTime;

public record EventRequestDTO(String title, String description, LocalDateTime start, LocalDateTime end, Long userId) {
}
