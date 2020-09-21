package com.janisschanbacher.springmicroservicesdemo.songService.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SongModelTest {
    Song s;

    @BeforeEach
    public void setup() {
        s = new Song(1, "Title", "Artist", "Label", 2020);
    }

    @Test
    void testConstructor() {
        new Song(2, "title", "artist", "label", 2020);
        new Song();
    }

    @Test
    void testGetId() {
        assertEquals(1, s.getId());
    }

    @Test
    void testGetTitle() {
        assertEquals("Title", s.getTitle());

    }

    @Test
    void testGetArtist() {
        assertEquals("Artist", s.getArtist());
    }

    @Test
    void testGetLabel() {
        assertEquals("Label", s.getLabel());
    }

    @Test
    void testGetReleased() {
        assertEquals(2020, s.getReleased());
    }

    @Test
    void testSetId() {
        assertEquals(1, s.getId());
    }

    @Test
    void testSetTitle() {
        s.setTitle("new title");
        assertEquals("new title", s.getTitle());

    }

    @Test
    void testSetArtist() {
        s.setArtist("new artist");
        assertEquals("new artist", s.getArtist());
    }

    @Test
    void testSetLabel() {
        s.setLabel("new Label");
        assertEquals("new Label", s.getLabel());
    }

    @Test
    void testSetReleased() {
        s.setReleased(2019);
        assertEquals(2019, s.getReleased());
    }

    @Test
    void testEquals() {
        Song different = new Song();
        assertNotEquals(different, s);
        different.setId(1);
        assertNotEquals(different, s);
        different.setTitle("Title");
        assertNotEquals(different, s);
        different.setArtist("Artist");
        assertNotEquals(different, s);
        different.setLabel("Label");
        assertNotEquals(different, s);
        different.setReleased(2020);
        assertEquals(different, s);
    }
}
