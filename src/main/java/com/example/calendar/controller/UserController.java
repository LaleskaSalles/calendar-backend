package com.example.calendar.controller;

import com.example.calendar.event.Event;
import com.example.calendar.event.EventRepository;
import com.example.calendar.event.EventResponseDTO;
import com.example.calendar.security.TokenService;
import com.example.calendar.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserRequestDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var userDetails = (User) auth.getPrincipal();
            var token = tokenService.generateToken((User) auth.getPrincipal());
            Long userId = userDetails.getId();
            String nameUser = userDetails.getUsername();

            return ResponseEntity.ok(new LoginResponseDTO(token, userId, nameUser));
        } catch (BadCredentialsException e){
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect Login or Password");
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred");
        }
    }
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody UserRequestDTO data){
        try{
            if(this.repository.findByLogin(data.login()) != null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The Login '"+ data.login() + "' already exists");
            }

            String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
            User newUser = new User(data.login(), encryptedPassword);
            this.repository.save(newUser);

            return  ResponseEntity.status(HttpStatus.CREATED)
                    .body("Registered successfully");
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred");
        }
    }


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isPresent()) {
            UserResponseDTO userResponseDTO = new UserResponseDTO(userOptional.get());
            return ResponseEntity.ok(userResponseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}/events")
    public ResponseEntity<List<EventResponseDTO>> getUserEvents(@PathVariable Long id) {
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            List<Event> userEvents = eventRepository.findByUser(user);

            List<EventResponseDTO> eventResponseDTOs = userEvents.stream()
                    .map(EventResponseDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(eventResponseDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
