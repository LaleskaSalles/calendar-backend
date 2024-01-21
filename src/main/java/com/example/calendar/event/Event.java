package com.example.calendar.event;

import com.example.calendar.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "events")

@Entity(name = "events")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime start;
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Event(EventRequestDTO data, User user) {
        this.title = data.title();
        this.start = data.start();
        this.end = data.end();
        this.user = user;
    }

    public void updateDataEvent(EventRequestDTO data) {
        if (data.title() != null) {
            this.title = data.title();
        }

        if (data.start() != null) {
            this.start = data.start();
        }

        if (data.end() != null) {
            this.end = data.end();
        }
    }

}
