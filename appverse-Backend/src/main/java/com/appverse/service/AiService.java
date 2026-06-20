package com.appverse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiService {

    // 1. Pulls your secret key from application.properties safely
    @Value("${gemini.api.key}")
    private String apiKey;
    
    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=";

    private final RestTemplate restTemplate;

    public AiService() {
        this.restTemplate = new RestTemplate();
    }

    // --- AI FEATURE 1: SENTIMENT ANALYSIS ---
    public String analyzeReviewSentiment(String reviewText) {
        // 2. Give the AI a strict persona and instruction
        String prompt = "You are an App Store moderator. Analyze this review: '" + reviewText + "'. " +
                        "Respond with ONLY ONE WORD: POSITIVE, NEGATIVE, or NEUTRAL.";

        // 3. Format the JSON payload exactly how Google expects it
        String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}";

        // 4. Set up the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            // 5. Send the request to Gemini and get the answer!
            String response = restTemplate.postForObject(GEMINI_API_URL + apiKey, request, String.class);
            return extractTextFromResponse(response);
        } catch (Exception e) {
            System.err.println("Gemini API Error: " + e.getMessage());
            return "NEUTRAL"; // Safe fallback if the API is down
        }
    }

    // A helper method to clean up the JSON string we get back from Google
    private String extractTextFromResponse(String rawJson) {
        try {
            return rawJson.split("\"text\": \"")[1].split("\"")[0].trim();
        } catch (Exception e) {
            return "NEUTRAL";
        }
    }
}