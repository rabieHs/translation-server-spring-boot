package com.rabie.translationapp.service;

import com.rabie.translationapp.dto.TranslationResponse;
import com.rabie.translationapp.model.CustomMultiparseFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class TranslationService {
    private final RestTemplate restTemplate;

    @Value("${translation.api.base-url}")
    private String translationApiBaseUrl;

    public TranslationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    public static MultipartFile convertBase64ToMultipartFile(String base64String, String fileName) {
        try {
            // Check if the Base64 string is null or empty
            if (base64String == null || base64String.isEmpty()) {
                throw new IllegalArgumentException("Base64 string is null or empty");
            }

            // Decode the Base64 string
            byte[] fileContent = Base64.getDecoder().decode(base64String);

            // Create and return a MultipartFile
            return new CustomMultiparseFile(fileContent, fileName);
        } catch (IllegalArgumentException e) {
            // Handle empty or null Base64 string
            throw new RuntimeException("Invalid Base64 string", e);
        } catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("Error converting Base64 string to MultipartFile", e);
        }
    }

    public TranslationResponse translateContent(String text, String file, String targetLanguage) {
        try {
            if (text != null && file == null) {
                return translateText(text, targetLanguage);
            } else if (text == null && file != null) {
                final MultipartFile fileToTranslate = convertBase64ToMultipartFile(file, "file");
                return translatePdf(fileToTranslate, targetLanguage);
            } else if (text != null && file != null) {
                final MultipartFile fileToTranslate = convertBase64ToMultipartFile(file, "file");

                return translateMultiple(text, fileToTranslate, targetLanguage);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private TranslationResponse translateText(String text, String targetLanguage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var requestBody = new TextTranslationRequest(text, targetLanguage);
        HttpEntity<TextTranslationRequest> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<TranslationResponse> response = restTemplate.postForEntity(
                translationApiBaseUrl + "/translate/text",
                request,
                TranslationResponse.class
        );

        return response.getBody();
    }

    private TranslationResponse translatePdf(MultipartFile file, String targetLanguage) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        body.add("target_language", targetLanguage);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<TranslationResponse> response = restTemplate.postForEntity(
                translationApiBaseUrl + "/translate/pdf",
                request,
                TranslationResponse.class
        );

        return response.getBody();
    }

    private TranslationResponse translateMultiple(String text, MultipartFile file, String targetLanguage) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("text", text);
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        body.add("target_language", targetLanguage);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<TranslationResponse> response = restTemplate.postForEntity(
                translationApiBaseUrl + "/translate/multiple",
                request,
                TranslationResponse.class
        );

        return response.getBody();
    }

    private static class TextTranslationRequest {
        public String text;
        public String target_language;

        public TextTranslationRequest(String text, String targetLanguage) {
            this.text = text;
            this.target_language = targetLanguage;
        }
    }
}