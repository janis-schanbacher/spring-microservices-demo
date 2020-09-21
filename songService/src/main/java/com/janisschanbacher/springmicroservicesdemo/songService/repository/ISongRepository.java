package com.janisschanbacher.springmicroservicesdemo.songService.repository;

import com.janisschanbacher.springmicroservicesdemo.songService.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISongRepository extends JpaRepository<Song, Integer> {
}
