package com.janisschanbacher.springmicroservicesdemo.usermanagementservice.controller;

import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.document.User;
import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns a List containing all Users of the userRepository
     *
     * @return a List containing all Users of the userRepository
     */
    @GetMapping(value = "")
    public List<User> getAll() {
        return userRepository.findAllByOrderByUsername();
    }

    /**
     * Returns a ResponseEntity containing the user specified by username and status 200
     * or an empty body and status 404 if no user with the specified username is found.
     *
     * @param username username that specifies the user to be returned
     * @return a ResponseEntity containing the user specified by username and status 200
     * or an empty body and status 404 if no user with the specified username is found.
     */
    @GetMapping(value = "/{username}")
    public ResponseEntity<User> getUser(
        @PathVariable("username") String username,
        @RequestHeader(value = "isAdmin", defaultValue = "false") String isAdmin
    ) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (user.get().getRole().equals("ADMIN") && !Boolean.parseBoolean(isAdmin)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Saves the provided user.
     *
     * @param isAdmin specifies if the current user is an admin and thus may create an admin user
     * @param user    the user to be saved
     * @return a RespoinseEntity with status 201 and location header that specifies the uri to access the user,
     * or if not successful a ResponseEntity with status 400 and error message or status 403.
     */
    @PostMapping(value = "")
    public ResponseEntity<String> createUser(
        @RequestHeader(value = "isAdmin", defaultValue = "false") String isAdmin,
        @RequestBody User user
    ) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("username field is required and may not be empty");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("username is not available");
        }
        if (user.getPassword() == null) {
            return ResponseEntity.badRequest().body("password field is required");
        }
        if (user.getRole().equals("ADMIN") && !Boolean.parseBoolean(isAdmin)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            user = userRepository.save(user);
            return ResponseEntity.created(new URI("/api/users/" + user.getUsername())).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Replaces the user specified by id with the provided user
     *
     * @param currentUserId userId of the current user
     * @param isAdmin       specifies if the current user is an admin and thus may grant admin rights
     * @param user          User to replace the old User
     * @param username      Identifier of the user to be updated
     * @return a ResponseEntity with status 204 and location header, or 400/404 and error message if not successful
     */
    @PutMapping("/{username}")
    public ResponseEntity<String> updateUser(
        @RequestHeader(value = "currentUserId") String currentUserId,
        @RequestHeader(value = "isAdmin", defaultValue = "false") String isAdmin,
        @RequestBody User user,
        @PathVariable(value = "username") String username
    ) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("username field is required and may not be empty");
        }
        if (!user.getUsername().equals(username)) {
            return ResponseEntity.badRequest().body("username field has to equal username specified in url");
        }
        if (user.getPassword() == null) {
            return ResponseEntity.badRequest().body("password field is required");
        }
        if (!username.equals(currentUserId) && !Boolean.parseBoolean(isAdmin)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Optional<User> oldUser = userRepository.findByUsername(username);
        if (oldUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (user.getRole().equals("ADMIN") && !Boolean.parseBoolean(isAdmin)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            user = userRepository.save(user);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, "/api/songs/" + user.getUsername());
            return ResponseEntity.noContent().headers(headers).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletes the user specified by username if the current user is the user to be deleted or an admin.
     *
     * @param currentUserId userId of the current user
     * @param isAdmin       specifies if the current user is an admin and thus may delete other users
     * @param username      specifies the user to be deleted
     * @return a ResponseEntity wit status 204 or 403/404 if not successfully
     */
    @DeleteMapping(value = "/{username}")
    public ResponseEntity<String> deleteUser(
        @RequestHeader(value = "currentUserId", defaultValue = "") String currentUserId,
        @RequestHeader(value = "isAdmin", defaultValue = "false") String isAdmin,
        @PathVariable(value = "username") String username
    ) {
        if (!username.equals(currentUserId) && !Boolean.parseBoolean(isAdmin)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            if (userRepository.deleteUserByUsername(username) < 1) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
