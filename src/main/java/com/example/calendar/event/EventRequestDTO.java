package com.example.calendar.event;

import java.time.LocalDateTime;

public record EventRequestDTO(String title, LocalDateTime start, LocalDateTime end, Long userId) {
}
