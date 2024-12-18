package com.rabie.translationapp.controller;

import com.rabie.translationapp.model.Post;
import com.rabie.translationapp.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(
            @RequestParam(required = false, defaultValue = "english") String language
    ) {
        List<Post> posts = postService.getPostsWithTranslation(language);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestParam Long userId,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) MultipartFile file

    ) {
        Post createdPost = postService.createPost(userId, text, file);
        return ResponseEntity.ok(createdPost);
    }
}