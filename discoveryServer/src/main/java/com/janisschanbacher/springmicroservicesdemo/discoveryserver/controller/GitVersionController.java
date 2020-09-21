package com.janisschanbacher.springmicroservicesdemo.discoveryserver.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(value = "/discovery/version")
public class GitVersionController {

    private String gitVersion;

    /**
     * Constructor, loads gitVersion from git.properties
     */
    public GitVersionController() {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("git.properties")) {
            assert in != null;
            this.gitVersion = IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            this.gitVersion = "Could not load git.properties, check logs!";
            e.printStackTrace();
        }
    }

    /**
     * Returns a ResponseEntity with gitVersion as body
     *
     * @return a ResponseEntity with gitVersion as body
     */
    @GetMapping
    public ResponseEntity<String> getGitProperties() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(this.gitVersion);
    }
}
