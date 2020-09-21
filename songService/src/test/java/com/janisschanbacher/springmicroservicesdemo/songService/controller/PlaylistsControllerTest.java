package com.janisschanbacher.springmicroservicesdemo.songService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.janisschanbacher.springmicroservicesdemo.songService.model.Playlist;
import com.janisschanbacher.springmicroservicesdemo.songService.model.Song;
import com.janisschanbacher.springmicroservicesdemo.songService.repository.IPlaylistRepository;
import com.janisschanbacher.springmicroservicesdemo.songService.repository.ISongRepository;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
@SpringBootTest
@AutoConfigureMockMvc
public class PlaylistsControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IPlaylistRepository playlistRepository;
    @Autowired
    private ISongRepository songRepository;

    @Test
    void given_getAllExistingPlaylistByOwner_then_returnPlaylists() throws Exception {
        mockMvc.perform(get("/songlists?userId=mmuster").header("currentUserId", "mmuster"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/songlists?userId=mmuster").header("currentUserId", "mmuster")
            .accept(MediaType.APPLICATION_XML))
            .andExpect(status().isOk())
            .andExpect(content().contentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8)))
            .andExpect(xpath("List/item/id").number((double) 3));
    }

    @Test
    void given_getPublicExistingPlaylistByUserIdNotOwner_then_returnPlaylists() throws Exception {
        mockMvc.perform(get("/songlists?userId=eschuler").header("currentUserId", "mmuster"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void given_getPlaylistByNonExistingPlaylistId_then_returnNotFound() throws Exception {
        mockMvc.perform(get("/songlists/999").header("currentUserId", "mmuster"))
            .andExpect(status().isNotFound());
    }

    @Test
    void given_getExistingPublicPlaylistByPlaylistIdWithOwnerAuthorization_then_returnPlaylist() throws Exception {
        mockMvc.perform(get("/songlists/1")
            .header("currentUserId", "mmuster"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("{id=1, ownerId=\"eschuler\", name=SPublicList, isPrivate=false, " +
                "songList=[{\"id\":1,\"title\":\"MacArthur Park\",\"artist\":\"Richard Harris\",\"label\":\"Dunhill Records\"," +
                "\"released\":1968},{\"id\":4,\"title\":\"Sussudio\",\"artist\":\"Phil Collins\",\"label\":\"Virgin\",\"released\":1985}" +
                ",{\"id\":6,\"title\":\"Achy Breaky Heart\",\"artist\":\"Billy Ray Cyrus\",\"label\":\"PolyGram Mercury\",\"released\":1992}" +
                ",{\"id\":5,\"title\":\"We Built This City\",\"artist\":\"Starship\",\"label\":\"Grunt\\/RCA\",\"released\":1985}]}"));
    }

    @Test
    void given_getNonExistingPlaylistByPlaylistId_then_returnNotFound() throws Exception {
        mockMvc.perform(get("/songlists/99").header("currentUserId", "mmuster"))
            .andExpect(status().isNotFound());
    }

    @Test
    void given_getExistingPrivatePlaylistByPlaylistId_when_currentUserIsNotOwner_then_returnForbidden() throws Exception {
        mockMvc.perform(get("/songlists/2").header("currentUserId", "mmuster"))
            .andExpect(status().isForbidden());
    }

    @Test
    void given_getExistingPrivatePlaylistByPlaylistId_when_currentUserIsOwner_then_returnPlaylistAnd200() throws Exception {
        mockMvc.perform(get("/songlists/3").header("currentUserId", "mmuster"))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"id\":3,\"ownerId\":\"mmuster\",\"name\":\"MPrivateList\"," +
                "\"isPrivate\":true,\"songList\":[{\"id\":4,\"title\":\"Sussudio\",\"artist\":\"Phil Collins\"," +
                "\"label\":\"Virgin\",\"released\":1985},{\"id\":8,\"title\":\"Who Let the Dogs Out?\"," +
                "\"artist\":\"Baha Men\",\"label\":\"S-Curve\",\"released\":2000}]}"));
    }

//  post playlist

    @Test
    void given_postPlaylistWithoutTitle_then_returnBadRequest() throws Exception {
        Playlist p = new Playlist(null, null, true, null);
        mockMvc.perform(post("/songlists").header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(p)))
            .andExpect(status().isBadRequest());

        Playlist p2 = new Playlist(null, "", true, null);
        mockMvc.perform(post("/songlists").header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(p2)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_postPlaylistWithNonExistingSong_then_returnBadRequest() throws Exception {
        List<Song> songs = Arrays.asList(new Song(55, "t", "a", "l", 2020));
        Playlist p = new Playlist(null, null, true, songs);
        mockMvc.perform(post("/songlists").header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(p)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_postPlaylistWithoutSongs_then_returnCreated() throws Exception {
        List<Song> songs = new ArrayList<>();
        Playlist p = new Playlist("mmuster", "new playlist", false, songs);
        mockMvc.perform(post("/songlists").header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(p)))
            .andExpect(status().isCreated());
    }

    @Test
    void given_postPlaylistWithSongsInvalidJson_then_returnBadRequest() throws Exception {
        mockMvc.perform(post("/songlists").header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString("{asdf: asdf ssss}")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void given_postPlaylistWithSongs_then_returnCreated() throws Exception {
        List<Song> songs = Arrays.asList(
            songRepository.findById(1).orElse(null),
            songRepository.findById(2).orElse(null));
        Playlist p = new Playlist("mmuster", "new playlist", false, songs);
        mockMvc.perform(post("/songlists").header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(p)))
            .andExpect(status().isCreated());
    }

    @Test
    void given_postPlaylistWithNotExistingSong_then_return400() throws Exception {
        List<Song> songs = Arrays.asList(
            songRepository.findById(1).orElse(null),
            new Song(1000, "Not existing Song", "Artist", "Label", 2050));
        Playlist p = new Playlist("mmuster", "new playlist", false, songs);
        mockMvc.perform(post("/songlists").header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(p)))
            .andExpect(status().isBadRequest());
    }

    // put playlist

    @Test
    void given_putWithNotExistingPlaylistId_then_notFound() throws Exception {
        List<Song> songs = Arrays.asList(songRepository.findById(1).orElse(null));
        Playlist playlist = new Playlist("mmuster", "name", false, songs);
        playlist.setId(999);
        mockMvc.perform(put("/songlists/999")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(playlist)))
            .andExpect(status().isNotFound());
    }

    @Test
    void given_putIdOfUrlNotEqualToIdField_then_badRequest() throws Exception {
        List<Song> songs = Arrays.asList(songRepository.findById(1).orElse(null));
        Playlist playlist = new Playlist("mmuster", "name", false, songs);
        playlist.setId(3);
        mockMvc.perform(put("/songlists/1")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(playlist)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("id field has to be equal to id specified in url"));
    }

    @Test
    void given_putOwnerIdNotEqualCurrentUserId_then_forbidden() throws Exception {
        List<Song> songs = Arrays.asList(songRepository.findById(1).orElse(null));
        Playlist playlist = new Playlist("mmuster", "name", false, songs);
        playlist.setId(1);
        mockMvc.perform(put("/songlists/1")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(playlist)))
            .andExpect(status().isForbidden());
    }

    @Test
    void given_putWithoutId_then_badRequest() throws Exception {
        List<Song> songs = Arrays.asList(songRepository.findById(1).orElse(null));
        Playlist playlist = new Playlist("mmuster", "name", false, songs);
        playlist.setId(null);
        mockMvc.perform(put("/songlists/3")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(playlist)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("id field is required"));
    }

    @Test
    void given_putWithoutName_then_badRequest() throws Exception {
        List<Song> songs = Arrays.asList(songRepository.findById(1).orElse(null));
        Playlist playlist = new Playlist("mmuster", null, false, songs);
        playlist.setId(3);
        mockMvc.perform(put("/songlists/3")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(playlist)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("name field is required"));
    }

    @Test
    void given_putWithEmptyName_then_badRequest() throws Exception {
        List<Song> songs = Arrays.asList(songRepository.findById(1).orElse(null));
        Playlist playlist = new Playlist("mmuster", "", false, songs);
        playlist.setId(3);
        mockMvc.perform(put("/songlists/3")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(playlist)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("name may not be empty"));
    }

    @Test
    @Transactional
        // avoid LazyInitializationException
    void given_put_then_savePlaylistAnd204() throws Exception {
        List<Song> songs = Arrays.asList(songRepository.findById(1).orElse(null));
        Playlist playlist = new Playlist("mmuster", "new name", false, songs);
        playlist.setId(3);
        mockMvc.perform(put("/songlists/3")
            .header("currentUserId", "mmuster")
            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
            .content(mapper.writeValueAsString(playlist)))
            .andExpect(status().isNoContent());
        Optional<Playlist> savedPlaylist = playlistRepository.findById(3);
        assertTrue(savedPlaylist.isPresent());
        assertTrue(EqualsBuilder.reflectionEquals(playlist, savedPlaylist.get()));
    }

    //delete playlist

    @Test
    void given_deletePlaylistByIdWithNonExistingId_then_returnNotFound() throws Exception {
        mockMvc.perform(delete("/songlists/999").header("currentUserId", "mmuster"))
            .andExpect(status().isNotFound());
    }

    @Test
    void given_deletePlaylistByIdWithUserNotOwner_then_returnForbidden() throws Exception {
        mockMvc.perform(delete("/songlists/1").header("currentUserId", "mmuster"))
            .andExpect(status().isForbidden());
    }

    @Test
    void given_deletePlaylistByIdWithUserIsOwner_then_returnNoContent() throws Exception {
        mockMvc.perform(delete("/songlists/4").header("currentUserId", "mmuster"))
            .andExpect(status().isNoContent());
    }
}
