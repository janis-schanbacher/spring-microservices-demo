package com.janisschanbacher.springmicroservicesdemo.songService.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistModelTest {
    List<Song> songs;
    Playlist p;

    @BeforeEach
    public void setup() {
        songs = Arrays.asList(
            new Song(1, "Title", "Artist", "Label", 2020),
            new Song(2, "Title2", "Artist2", "Label2", 2019));
        p = new Playlist("OwnerId", "Name", false, songs);
        p.setId(1);
    }

    @Test
    void testConstructor() {
        new Playlist("mmuster", "name", false, songs);
        new Playlist();
    }

    @Test
    void testGetId() {
        assertEquals(1, p.getId());
    }

    @Test
    void testGetOwnerId() {
        assertEquals("OwnerId", p.getOwnerId());
    }

    @Test
    void testGetName() {
        assertEquals("Name", p.getName());
    }

    @Test
    void testGetIsPrivate() {
        assertFalse(p.getIsPrivate());
    }

    @Test
    void testGetSongList() {
        assertEquals(songs, p.getSongList());
    }

    @Test
    void testSetId() {
        assertEquals(1, p.getId());
    }

    @Test
    void testSetOwnerId() {
        p.setOwnerId("newOwnerId");
        assertEquals("newOwnerId", p.getOwnerId());

    }

    @Test
    void testSetName() {
        p.setName("newName");
        assertEquals("newName", p.getName());
    }

    @Test
    void testSetIsPrivate() {
        p.setIsPrivate(true);
        assertTrue(p.getIsPrivate());
    }

    @Test
    void testSetSongList() {
        ArrayList<Song> newSongs = new ArrayList<>();
        p.setSongList(newSongs);
        assertEquals(newSongs, p.getSongList());
    }
}
