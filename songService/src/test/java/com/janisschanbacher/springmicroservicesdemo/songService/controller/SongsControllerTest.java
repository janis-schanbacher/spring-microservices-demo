package com.janisschanbacher.springmicroservicesdemo.songService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.janisschanbacher.springmicroservicesdemo.songService.model.Song;
import com.janisschanbacher.springmicroservicesdemo.songService.repository.ISongRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SongsControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private ISongRepository songRepository;

    @Test
    void given_getIndex_then_returnSongsAnd200() throws Exception {
        mockMvc.perform(get("/songs").header("currentUserId", "mmuster"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(11)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("MacArthur Park"))
            .andExpect(jsonPath("$[0].artist").value("Richard Harris"))
            .andExpect(jsonPath("$[0].label").value("Dunhill Records"))
            .andExpect(jsonPath("$[0].released").value(1968));
    }

    @Test
    void given_getIndexWithAcceptXMLHeader_then_returnSongsAsXML() throws Exception {
        mockMvc.perform(get("/songs").header("currentUserId", "mmuster").accept(MediaType.APPLICATION_XML))
            .andExpect(status().isOk())
            .andExpect(content().contentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8)))
            .andExpect(xpath("List/item").nodeCount(12));
    }

    @Test
    void given_getWithExistingUserId_then_returnSongAnd200() throws Exception {
        mockMvc.perform(get("/songs/1").header("currentUserId", "mmuster"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("MacArthur Park"))
            .andExpect(jsonPath("$.artist").value("Richard Harris"))
            .andExpect(jsonPath("$.label").value("Dunhill Records"))
            .andExpect(jsonPath("$.released").value(1968));
    }

    @Test
    void given_getWithNotExistingUserId_then_status404() throws Exception {
        mockMvc.perform(get("/songs/99").header("currentUserId", "mmuster"))
            .andExpect(status().isNotFound());
    }

    @Test
    void given_getWithAcceptXMLHeader_then_returnSongAsXML() throws Exception {
        mockMvc.perform(get("/songs/1").header("currentUserId", "mmuster").accept(MediaType.APPLICATION_XML))
            .andExpect(status().isOk())
            .andExpect(content().contentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8)))
            .andExpect(xpath("Song/id").number((double) 1))
            .andExpect(xpath("Song/title").string("MacArthur Park"))
            .andExpect(xpath("Song/artist").string("Richard Harris"))
            .andExpect(xpath("Song/label").string("Dunhill Records"))
            .andExpect(xpath("Song/released").number((double) 1968));
    }

    @Test
    void given_postWithoutTitle_then_badRequest() throws Exception {
        Song song3 = new Song(null, null, "artist 3", "label 3", 2020);
        mockMvc.perform(post("/songs")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song3)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_postWithEmptyTitle_then_badRequest() throws Exception {
        Song song3 = new Song(null, "", "artist 3", "label 3", 2020);
        mockMvc.perform(post("/songs")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song3)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_post_then_saveSongAndReturnLocationAnd201() throws Exception {
        Song song3 = new Song(null, "new title 3", "new artist 3", "new label 3", 2022);
        mockMvc.perform(post("/songs")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song3)))
            .andExpect(header().string("Location", "/api/songs/11"))
            .andExpect(status().isCreated());
        assertTrue(songRepository.findById(11).isPresent());
    }

    @Test
    void given_postOnlyTitleAndId_then_saveSongAndReturnLocationAnd201() throws Exception {
        Song song3 = new Song(null, "new title 3", null, null, null);
        mockMvc.perform(post("/songs")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song3)))
            .andExpect(header().string("Location", "/api/songs/12"))
            .andExpect(status().isCreated());
        assertTrue(songRepository.findById(12).isPresent());
    }

    @Test
    void given_putWithNotExistingSongId_then_notFound() throws Exception {
        Song song3 = new Song(999, "title 3", "artist 3", "label 3", 2020);
        mockMvc.perform(put("/songs/999")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song3)))
            .andExpect(status().isNotFound());
    }

    @Test
    void given_putIdOfUrlNotEqualToIdField_then_badRequest() throws Exception {
        Song song2 = new Song(2, "title 2", "artist 2", "label 2", 2020);
        mockMvc.perform(put("/songs/1")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song2)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_putWithoutId_then_badRequest() throws Exception {
        Song song2 = new Song(null, "title 2", "artist 2", "label 2", 2020);
        mockMvc.perform(put("/songs/2")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song2)))
            .andExpect(status().isBadRequest());
    }

    //
    @Test
    void given_putWithoutTitle_then_badRequest() throws Exception {
        Song song2 = new Song(2, null, "artist 2", "label 2", 2020);
        mockMvc.perform(put("/songs/2")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song2)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_putWithEmptyTitle_then_badRequest() throws Exception {
        Song song2 = new Song(2, "", "artist 2", "label 2", 2020);
        mockMvc.perform(put("/songs/2")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song2)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_put_then_saveSongAnd204() throws Exception {
        Song song2 = new Song(2, "new title 2", "new artist 2", "new label 2", 2022);
        mockMvc.perform(put("/songs/2")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song2)))
            .andExpect(status().isNoContent());
        Optional<Song> savedSong = songRepository.findById(2);
        assertTrue(savedSong.isPresent());
        assertEquals(savedSong.get(), song2);
    }

    @Test
    void given_putOnlyTitleAndId_then_saveSongAnd204() throws Exception {
        Song song2 = new Song(2, "new title 3", null, null, null);
        mockMvc.perform(put("/songs/2")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(song2)))
            .andExpect(status().isNoContent());
        Optional<Song> savedSong = songRepository.findById(2);
        assertTrue(savedSong.isPresent());
        assertEquals(savedSong.get(), song2);
    }

    @Test
    void given_deleteWithNotExistingId_then_notFound() throws Exception {
        mockMvc.perform(delete("/songs/25").header("currentUserId", "mmuster"))
            .andExpect(status().isNotFound());
    }

    @Test
    void given_delete_then_deleteSongAnd204() throws Exception {
        mockMvc.perform(delete("/songs/7").header("currentUserId", "mmuster"))
            .andExpect(status().isNoContent());
        assertTrue(songRepository.findById(7).isEmpty());
    }
}
