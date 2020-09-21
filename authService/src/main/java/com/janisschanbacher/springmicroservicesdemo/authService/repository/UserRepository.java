package com.janisschanbacher.springmicroservicesdemo.authService.repository;

import com.janisschanbacher.springmicroservicesdemo.authService.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Returns an Optional containing the user specified by username from the database or an empty Optional if
     * no user with the specified username exists
     *
     * @param username username of the user to be returned
     * @return Optional containing the specified user of an empty Optional if no user is found
     */
    Optional<User> findByUsername(String username);
}
