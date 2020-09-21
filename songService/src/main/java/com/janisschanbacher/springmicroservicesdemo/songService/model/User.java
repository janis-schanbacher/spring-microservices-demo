// The song-service is no longer responsible for user management and neither are they stored in its database.

//package com.janisschanbacher.springmicroservicesdemo.songService.model;
//
//import javax.persistence.*;
//
//@Entity
//@Table(name = "users")
//public class User {
//
//    @Id
//    @Column(name = "userId", updatable = false, nullable = false)
//    private String userId;
//
//    @Column(name = "password", nullable = false)
//    private String password;
//
//    @Column(name = "firstName")
//    private String firstName;
//
//    @Column(name = "lastName")
//    private String lastName;
//
//    /**
//     * Returns userId
//     * @return userId
//     */
//    public String getUserId() {
//        return userId;
//    }
//
//    /**
//     * Sets userId
//     * @param userId userId to be set
//     */
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    /**
//     * Returns password
//     * @return password
//     */
//    public String getPassword() {
//        return password;
//    }
//
//    /**
//     * Sets password
//    * @param password Password to be set
//     */
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    /**
//     * Returns firstName
//     * @return firstName
//     */
//    public String getFirstName() {
//        return firstName;
//    }
//
//    /**
//     * Sets firstName
//     * @param firstName FirstName to be set
//     */
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    /**
//     * Returns lasName
//     * @return lasName
//     */
//    public String getLastName() {
//        return lastName;
//    }
//
//    /**
//     * Sets lastName
//     * @param lastName LastName to be set
//     */
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//}
