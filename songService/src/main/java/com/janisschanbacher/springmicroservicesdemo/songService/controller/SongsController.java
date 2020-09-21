package com.janisschanbacher.springmicroservicesdemo.songService.controller;

import com.janisschanbacher.springmicroservicesdemo.songService.model.Song;
import com.janisschanbacher.springmicroservicesdemo.songService.repository.IPlaylistRepository;
import com.janisschanbacher.springmicroservicesdemo.songService.repository.ISongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/songs")
public class SongsController {
    @Autowired
    private ISongRepository songRepository;

    @Autowired
    private IPlaylistRepository playlistRepository;

    // Access values of cloud config server
    // Alternatively with @ConfigurationProperties("propertyGroupName") annotation on SongsController class
//    @Value("${message: default value}")
//    private String message;

    /**
     * Returns a ResponseEntity with status 200 and all songs
     *
     * @return a ResponseEntity with status 200 and all songs
     */
    @GetMapping("")
    public ResponseEntity<List<Song>> getAllSongs() {
        return ResponseEntity.ok(songRepository.findAll());
    }

    /**
     * Returns a ResponseEntity with status 200 and the song specified by id,
     * or 404 no song with the provided id exists.
     *
     * @param id id of the song to be returned
     * @return a ResponseEntity with status 200 and the song specified by id,
     * or 404 no song with the provided id exists.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Song> getSong(@PathVariable(value = "id") Integer id) {
        Optional<Song> song = songRepository.findById(id);
        return song.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Saves the provided song
     *
     * @param s Song to be saved
     * @return a ResponseEntity with status 200 and location header, or 400, if title is empty or missing
     */
    @PostMapping("")
    public ResponseEntity<String> createSong(@RequestBody Song s) {
        if (s.getTitle() == null) {
            return ResponseEntity.badRequest().body("title field is required");
        }
        if (s.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("title may not be empty");
        }
        try {
            s = songRepository.save(s);
            return ResponseEntity.created(new URI("/api/songs/" + s.getId())).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Replaces the song specified by id with the provided song
     *
     * @param s  Song to replace the old Song
     * @param id Identifier of the song to be updated
     * @return a ResponseEntity with status 204 and location header, or 400/404 and error message if not successful
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateSong(@RequestBody Song s, @PathVariable(value = "id") Integer id) {
        if (s.getId() == null) {
            return ResponseEntity.badRequest().body("id field is required");
        }
        if (!s.getId().equals(id)) {
            return ResponseEntity.badRequest().body("id field has to be equal to id specified in url");
        }
        if (s.getTitle() == null) {
            return ResponseEntity.badRequest().body("title field is required");
        }
        if (s.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("title may not be empty");
        }
        if (songRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            s = songRepository.save(s);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, "/api/songs/" + s.getId());
            return ResponseEntity.noContent().headers(headers).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletes the song specified by id
     *
     * @param id Id of the song to be saved
     * @return a ResponseEntity with status 204, or 400/404 if not successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSong(@PathVariable(value = "id") Integer id) {
        if (songRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            playlistRepository.removeSongFromPlaylists(id);
            songRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
