package com.janisschanbacher.springmicroservicesdemo.usermanagementservice.repository;

import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Returns a list containing all users from the database ordered by Username
     *
     * @return a list containing all users from the database ordered by Username
     */
    List<User> findAllByOrderByUsername();

    /**
     * Returns an Optional containing the user specified by username from the database or an empty Optional if
     * no user with the specified username exists
     *
     * @param username username of the user to be returned
     * @return Optional containing the specified user of an empty Optional if no user is found
     */
    Optional<User> findByUsername(String username);

    /**
     * Deletes the user specified by username and returns the number of deleted users
     * (0 or 1, because username is an id).
     *
     * @param username username of the user to be deleted
     * @return 1 if deletion is successful, else 0
     */
    int deleteUserByUsername(String username);
}
