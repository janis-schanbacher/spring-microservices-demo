package com.janisschanbacher.springmicroservicesdemo.zuulGateway;

import com.janisschanbacher.springmicroservicesdemo.zuulGateway.controller.GitVersionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SpringZuulApplicationTests {
    @Autowired
    private GitVersionController gitVersionController;

    @Test
    void contextLoads() {
        assertThat(gitVersionController).isNotNull();
    }
}
