package com.example.calendar.controller;

import com.example.calendar.event.Event;
import com.example.calendar.event.EventRepository;
import com.example.calendar.event.EventRequestDTO;
import com.example.calendar.event.EventResponseDTO;
import com.example.calendar.user.User;
import com.example.calendar.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

        if (hasOverlappingEvents(user.getId(), data.start(), data.end())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Overlapping events not allowed");
        }

        Event eventData = new Event(data, user);

        this.repository.save(eventData);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Event successfully registered");
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity editEvent(@PathVariable Long id, @RequestBody EventRequestDTO data){

        Event eventToUpdate = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));

        if (hasOverlappingEventsForUser(eventToUpdate.getUser().getId(), id, data.start(), data.end())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Overlapping events not allowed");
        }

        eventToUpdate.updateDataEvent(data);

        repository.save(eventToUpdate);

        return ResponseEntity.ok().body("Event successfully edited");
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

        return ResponseEntity.ok().body("Event successfully deleted");
    }


    private boolean hasOverlappingEvents(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Event> userEvents = repository.findByUserId(userId);

        for (Event event : userEvents) {
            if (event.getStart().isBefore(end) && event.getEnd().isAfter(start)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasOverlappingEventsForUser(Long userId, Long eventId, LocalDateTime start, LocalDateTime end) {
        List<Event> userEvents = repository.findByUserId(userId);

        for (Event event : userEvents) {
            if (!event.getId().equals(eventId) && event.getStart().isBefore(end) && event.getEnd().isAfter(start)) {
                return true;
            }
        }
        return false;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        Optional<Event> eventOptional = repository.findById(id);

        if (eventOptional.isPresent()) {
            EventResponseDTO eventResponseDTO = new EventResponseDTO(eventOptional.get());
            return ResponseEntity.ok(eventResponseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
