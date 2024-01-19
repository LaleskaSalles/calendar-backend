package com.example.calendar.user;

import com.example.calendar.event.Event;
import java.util.List;

public record UserResponseDTO(Long id, String login, String password, List<Event> events) {
    public UserResponseDTO(User user){
        this(user.getId(), user.getLogin(), user.getPassword(), user.getEvents());
    }
}
