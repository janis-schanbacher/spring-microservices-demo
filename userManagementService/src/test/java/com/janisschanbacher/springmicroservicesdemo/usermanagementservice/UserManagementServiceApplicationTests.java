package com.janisschanbacher.springmicroservicesdemo.usermanagementservice;

import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.controller.GitVersionController;
import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.controller.UsersController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserManagementServiceApplicationTests {
    @Autowired
    private UsersController usersController;

    @Autowired
    private GitVersionController gitVersionController;

    @Test
    void contextLoads() {
        assertThat(usersController).isNotNull();
        assertThat(gitVersionController).isNotNull();
    }
}
