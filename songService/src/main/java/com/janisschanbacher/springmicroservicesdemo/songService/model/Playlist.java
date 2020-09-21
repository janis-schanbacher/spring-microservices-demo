package com.janisschanbacher.springmicroservicesdemo.songService.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;
import java.util.List;

//@SuppressWarnings("JpaDataSourceORMInspection") // data source is secified via cloud config
@Entity
@Table(name = "playlists")
@JacksonXmlRootElement
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    //     @ManyToOne
    //     @JoinColumn(name = "ownerId")
    //     @JsonIgnoreProperties({"password", "firstName", "lastName" })
    //     private User ownerId;
    private String ownerId;

    @Column(name = "name")
    private String name;

    @Column(name = "private")
    private boolean isPrivate;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "playlists_songs",
        joinColumns = {@JoinColumn(name = "playlist_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "song_id", referencedColumnName = "id")})
    private List<Song> songList;

    /**
     * Constructor
     */
    public Playlist() {
    }

    /***
     * Constructor, sets the provided values
     * @param ownerId ownerId to be set
     * @param name name to be set
     * @param isPrivate isPrivate to be set
     * @param songList songList to be set
     */
    public Playlist(String ownerId, String name, boolean isPrivate, List<Song> songList) {
        this.ownerId = ownerId;
        this.name = name;
        this.isPrivate = isPrivate;
        this.songList = songList;
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
     * @param id id to be set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns ownerId
     *
     * @return ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Sets ownerId
     *
     * @param ownerId ownerId to be set
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Returns name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name
     *
     * @param name name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns isPrivate
     *
     * @return isPrivate
     */
    public boolean getIsPrivate() {
        return isPrivate;
    }

    /**
     * Sets isPrivate
     *
     * @param isPrivate isPrivate to be set
     */
    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    /**
     * Returns songList
     *
     * @return songList
     */
    public List<Song> getSongList() {
        return songList;
    }

    /**
     * Sets songList
     *
     * @param songList songList to be set
     */
    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }
}
