package com.janisschanbacher.springmicroservicesdemo.authService.model;

public class UserCredentials {
    private String userId, password;

    /**
     * Constructor
     */
    public UserCredentials() {
    }


    /**
     * Returns userId
     *
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets userId
     *
     * @param userId userId to be set
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
     * Sets password
     *
     * @param password password to be set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
