package com.janisschanbacher.springmicroservicesdemo.discoveryserver;

import com.janisschanbacher.springmicroservicesdemo.discoveryserver.controller.GitVersionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DiscoveryServerApplicationTests {
    @Autowired
    private GitVersionController gitVersionController;

    @Test
    void contextLoads() {
        assertThat(gitVersionController).isNotNull();
    }

}

