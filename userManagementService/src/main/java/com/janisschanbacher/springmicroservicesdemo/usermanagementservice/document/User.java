package com.janisschanbacher.springmicroservicesdemo.usermanagementservice.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SuppressWarnings("ALL")
@Document
public class User {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();
    @Id
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String role = "USER";

    /**
     * Constructor
     */
    public User() {
    }

    /**
     * Constructor, sets the provided values, hashes password, only overwrites the default value 'USER' of this.role,
     * if role is not null
     *
     * @param username  username to be set
     * @param firstName firstName to be set
     * @param lastName  lastName to be set
     * @param password  password to be hashed and set
     * @param role      role to be set
     */
    public User(String username, String firstName, String lastName, String password, String role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password != null ? encoder.encode(password) : null;
        if (role != null) this.role = role;
    }

    /**
     * Returns username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username
     *
     * @param username username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns firstName
     *
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets firstName
     *
     * @param firstName firstName to be set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns lastName
     *
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets lastName
     *
     * @param lastName lastName to be set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns password
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets hashed password
     *
     * @param password password to be set
     */
    public void setPassword(String password) {
        this.password = password != null ? encoder.encode(password) : null;
    }

    /**
     * Returns role
     *
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets role
     *
     * @param role role to be set
     */
    public void setRole(String role) {
        this.role = role;
    }
}
