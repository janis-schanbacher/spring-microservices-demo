package com.janisschanbacher.springmicroservicesdemo.songService.controller;

import com.janisschanbacher.springmicroservicesdemo.songService.model.Playlist;
import com.janisschanbacher.springmicroservicesdemo.songService.model.Song;
import com.janisschanbacher.springmicroservicesdemo.songService.repository.IPlaylistRepository;
import com.janisschanbacher.springmicroservicesdemo.songService.repository.ISongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/songlists")
public class PlaylistsController {
    @Autowired
    private IPlaylistRepository playlistRepository;

    @Autowired
    private ISongRepository songRepository;

    /**
     * Returns a ResponseEntity with status 200 and all public playlists of the user specified by userId. If
     * userId specifies the current user private lists are also returned
     *
     * @param currentUserId userId of the current user
     * @param userId        userId of the user from whom the playlists shall be returned
     * @return a ResponseEntity with status 200 and all public playlists of the user specified by userId. If
     * userId specifies the current user private lists are also returned.
     */
    @GetMapping("")
    public ResponseEntity<List<Playlist>> getPlaylistsByUserId(
        @RequestHeader(value = "currentUserId") String currentUserId,
        @RequestParam(value = "userId") String userId
    ) {
        List<Playlist> playlists = playlistRepository.findAllByOwnerId(userId);
        if (!currentUserId.equals(userId)) {
            playlists.removeIf(Playlist::getIsPrivate);
        }

        return ResponseEntity.ok(playlists);
    }

    /**
     * Returns a ResponseEntity with status 200 and the playlist specified by id,
     * 404 if no playlist with the provided id exists or 403 if the playlist is private and not owned by the current User.
     *
     * @param currentUserId userId of the current user
     * @param id            id of the playlist to be returned
     * @return a ResponseEntity with status 200 and the playlist specified by id,
     * 404 if no playlist with the provided id exists or 403 if the playlist is private and not owned by the current User.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylistByPlaylistId(
        @RequestHeader(value = "currentUserId") String currentUserId,
        @PathVariable(value = "id") Integer id
    ) {
        Optional<Playlist> playlist = playlistRepository.findById(id);
        if (playlist.isEmpty()) return ResponseEntity.notFound().build();
        if (playlist.get().getIsPrivate() && !playlist.get().getOwnerId().equals(currentUserId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(playlist.get());
    }

    /**
     * Saves the provided playlist
     *
     * @param currentUserId userId of the current user
     * @param p             Playlist to be saved
     * @return a ResponseEntity with status 200 and location header, or 400/404 if not successful
     */
    @PostMapping("")
    public ResponseEntity<String> createPlaylist(
        @RequestHeader(value = "currentUserId") String currentUserId,
        @RequestBody Playlist p
    ) {
        if (p.getName() == null || p.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Name may not be empty");
        }

        p.setOwnerId(currentUserId);

        List<Song> songs = p.getSongList();
        if (songs == null) songs = new LinkedList<>();
        for (Song s : songs) {
            if (songRepository.findById(s.getId()).isEmpty()) {
                return ResponseEntity.badRequest().body("The song with id: " + s.getId() + " was not found");
            }
        }

        try {
            p = playlistRepository.save(p);
            return ResponseEntity.created(new URI("/api/songlists/" + p.getId())).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Replaces the playlist specified by id with the provided playlist
     *
     * @param currentUserId userId of the current user
     * @param p             Playlist to replace the old Playlist
     * @param id            Identifier of the playlist to be updated
     * @return a ResponseEntity with status 204 and location header, or 400/403/404 and error message if not successful
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePlaylist(
        @RequestHeader(value = "currentUserId") String currentUserId,
        @RequestBody Playlist p,
        @PathVariable(value = "id") Integer id
    ) {
        if (p.getId() == null) {
            return ResponseEntity.badRequest().body("id field is required");
        }
        if (!p.getId().equals(id)) {
            return ResponseEntity.badRequest().body("id field has to be equal to id specified in url");
        }
        if (p.getName() == null) {
            return ResponseEntity.badRequest().body("name field is required");
        }
        if (p.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("name may not be empty");
        }

        Optional<Playlist> oldPlaylist = playlistRepository.findById(id);
        if (oldPlaylist.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!oldPlaylist.get().getOwnerId().equals(currentUserId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        p.setOwnerId(currentUserId);

        List<Song> songs = p.getSongList();
        if (songs == null) songs = new LinkedList<>();
        for (Song s : songs) {
            if (songRepository.findById(s.getId()).isEmpty()) {
                return ResponseEntity.badRequest().body("The song with id: " + s.getId() + " was not found");
            }
        }

        try {
            p = playlistRepository.save(p);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, "/api/songlists/" + p.getId());
            return ResponseEntity.noContent().headers(headers).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletes the playlist specified by id
     *
     * @param currentUserId userId of the current user
     * @param id            Id of the playlist to be deleted
     * @return a ResponseEntity with status 204, or 400/403/404 if not successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlaylist(@RequestHeader(value = "currentUserId") String currentUserId,
                                                 @PathVariable(value = "id") Integer id) {
        Optional<Playlist> p = playlistRepository.findById(id);
        if (p.isEmpty()) return ResponseEntity.notFound().build();

        if (!p.get().getOwnerId().equals(currentUserId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            playlistRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
