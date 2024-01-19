package com.example.calendar.controller;

import com.example.calendar.event.Event;
import com.example.calendar.event.EventRepository;
import com.example.calendar.event.EventRequestDTO;
import com.example.calendar.event.EventResponseDTO;
import com.example.calendar.user.User;
import com.example.calendar.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("events")
public class EventController {

    @Autowired
    private EventRepository repository;

    @Autowired
    private UserRepository userRepository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ResponseEntity saveEvent(@RequestBody EventRequestDTO data){
        User user = userRepository.findById(data.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + data.userId()));
        Event eventData = new Event(data, user);

        this.repository.save(eventData);

        return ResponseEntity.ok().build();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity editEvent(@PathVariable Long id, @RequestBody EventRequestDTO data){
        User user = userRepository.findById(data.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        Event eventToUpdate = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));

        eventToUpdate.updateDataEvent(data, user);

        repository.save(eventToUpdate);

        return ResponseEntity.ok().build();
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<EventResponseDTO> getAll(){
        List<EventResponseDTO> eventList = repository.findAll().stream().map(EventResponseDTO::new).collect(Collectors.toList());
        return eventList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteEvent(@PathVariable Long id){
        Event eventToDelete = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));

        repository.delete(eventToDelete);

        return ResponseEntity.ok().build();
    }


}
