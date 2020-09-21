package com.janisschanbacher.springmicroservicesdemo.songService;

import com.janisschanbacher.springmicroservicesdemo.songService.controller.GitVersionController;
import com.janisschanbacher.springmicroservicesdemo.songService.controller.PlaylistsController;
import com.janisschanbacher.springmicroservicesdemo.songService.controller.SongsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SongServiceApplicationTest {

    @Autowired
    private GitVersionController gitVersionController;

    @Autowired
    private PlaylistsController playlistsController;

    @Autowired
    private SongsController songsController;

    @Test
    void contextLoads() {
        assertThat(gitVersionController).isNotNull();
        assertThat(playlistsController).isNotNull();
        assertThat(songsController).isNotNull();
    }
}
