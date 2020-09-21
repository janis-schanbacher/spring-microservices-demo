package com.janisschanbacher.springmicroservicesdemo.songService.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;
import java.util.Objects;

@SuppressWarnings("JpaDataSourceORMInspection") // data source is secified via cloud config
@Entity
@Table(name = "songs")
@JacksonXmlRootElement
public class Song {

    @Id
    //Generated value without strategy means  you can generate the ids yourself
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "artist")
    private String artist;

    @Column(name = "label")
    private String label;

    @Column(name = "released")
    private Integer released;

    /**
     * Constructor
     */
    public Song() {
    }

    /**
     * Constructor that sets all arguments
     *
     * @param id       Song Id
     * @param title    Song title
     * @param artist   Song artist
     * @param label    Label of the song
     * @param released Release date of the song
     */
    public Song(Integer id, String title, String artist, String label, Integer released) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.label = label;
        this.released = released;
    }

    /**
     * Returns id
     *
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id
     *
     * @param id Id to be set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns title
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title
     *
     * @param title Title to be set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns artist
     *
     * @return artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Sets artist
     *
     * @param artist Artist to be set
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Returns label
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label
     *
     * @param label Label to be set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns released
     *
     * @return released
     */
    public Integer getReleased() {
        return released;
    }

    /**
     * Sets released
     *
     * @param released Release date to be set
     */
    public void setReleased(Integer released) {
        this.released = released;
    }

    /**
     * Returns true if obj is an instance of Song and all attributes match those of the calling Song instance
     *
     * @param obj Object to be compared with
     * @return true, if obj is an instance of Song and all attributes match those of the calling Song instance
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Song) {
            Song s = (Song) obj;

            return Objects.equals(this.id, s.id)
                && Objects.equals(this.title, s.title)
                && Objects.equals(this.artist, s.artist)
                && Objects.equals(this.label, s.label)
                && Objects.equals(this.released, s.released);
        }
        return false;
    }
}
