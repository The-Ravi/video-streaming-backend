package com.api.videostreaming.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.api.videostreaming.entities.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    // Exclude soft-deleted videos
    List<Video> findByIsActiveTrue();
    List<Video> findByDirectorContainingIgnoreCase(String director);

     // Correct way to check if a video title exists (case insensitive)
    boolean existsByTitleIgnoreCase(String title);
    
    // Alternative Custom Query (Optional)
    @Query("SELECT COUNT(v) > 0 FROM Video v WHERE LOWER(v.title) = LOWER(:title)")
    boolean doesTitleExist(@Param("title") String title);


    @Query("SELECT v FROM Video v " +
       "JOIN v.metadata m " +
       "LEFT JOIN v.cast c " +  // Use LEFT JOIN for cast to prevent filtering videos without cast members
       "WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :searchPhrase, '%')) " +
       "OR LOWER(v.director) LIKE LOWER(CONCAT('%', :searchPhrase, '%')) " +
       "OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :searchPhrase, '%')) " +
       "OR LOWER(c) LIKE LOWER(CONCAT('%', :searchPhrase, '%'))")
    Page<Video> searchVideos(String searchPhrase, Pageable pageable);

    Page<Video> findAll(Pageable pageable);

}

