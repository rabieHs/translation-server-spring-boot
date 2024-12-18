package com.rabie.translationapp.dto;

import lombok.Data;

@Data
public class TranslationResponse {
    private OriginalContent original;
    private TranslatedContent translated;

    @Data
    public static class OriginalContent {
        private String text;
        private String file;
    }

    @Data
    public static class TranslatedContent {
        private String text;
        private String file;
    }
}