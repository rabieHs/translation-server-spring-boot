package com.rabie.translationapp.repository;


import com.rabie.translationapp.model.Post;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Primary
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTextIsNotNullOrFileIsNotNull();
}