package com.rabie.translationapp.service;

import com.rabie.translationapp.dto.TranslationResponse;
import com.rabie.translationapp.model.CustomMultiparseFile;
import com.rabie.translationapp.model.Post;
import com.rabie.translationapp.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;


@Service
public class PostService {
    private final PostRepository postRepository;
    private final TranslationService translationService;






    public PostService(PostRepository postRepository, TranslationService translationService) {
        this.postRepository = postRepository;
        this.translationService = translationService;
    }

    public List<Post> getPostsWithTranslation(String targetLanguage) {
        // Fetch all posts
        List<Post> posts = postRepository.findByTextIsNotNullOrFileIsNotNull();

        // Translate posts
        return posts.stream().map(post -> {
            // Translate text if it exists
            if (post.getText() != null || post.getFile() != null) {
System.out.println(post.getFile());
                TranslationResponse translationResponse = translationService.translateContent(
                        post.getText(),
                       post.getFile() ,
                        targetLanguage
                );

                if (translationResponse != null && translationResponse.getTranslated() != null) {
                    // Modify the post's text with translated content
                    post.setText(translationResponse.getTranslated().getText());
                    post.setFile(translationResponse.getTranslated().getFile());
                }
            }



            return post;
        }).collect(Collectors.toList());
    }

    public Post createPost(Long userId, String text, MultipartFile file) {
        try {
            Post post = new Post();
            post.setUserId(userId);
            post.setText(text);

            if (file != null && !file.isEmpty()) {
                // Encode file to Base64 before saving
                String base64File = Base64.getEncoder().encodeToString(file.getBytes());
                post.setFile(base64File);
            }

            return postRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating post", e);
        }
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }
}