package com.janisschanbacher.springmicroservicesdemo.songService.repository;

import com.janisschanbacher.springmicroservicesdemo.songService.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IPlaylistRepository extends JpaRepository<Playlist, Integer> {
    /**
     * Returns a List containing all Playlists with ownerId equal to the specified ownerId from the databasse.
     *
     * @param ownerId ownerId to filter Playlists
     * @return a List containing all Playlists with ownerId equal to the specified ownerId
     */
    List<Playlist> findAllByOwnerId(String ownerId);

    /**
     * Removes the song specified by songId from all playlists.
     *
     * @param songId songId of the song to removed
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM playlists_songs WHERE song_id = ?1", nativeQuery = true)
    void removeSongFromPlaylists(int songId);
}
