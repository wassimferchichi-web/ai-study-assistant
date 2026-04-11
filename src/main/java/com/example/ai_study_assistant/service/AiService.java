package com.example.ai_study_assistant.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String callGroq(String prompt) {
        try {
            String url = "https://api.groq.com/openai/v1/chat/completions";

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);

            JSONArray messages = new JSONArray();
            messages.put(message);

            JSONObject body = new JSONObject();
            body.put("model", "llama-3.1-8b-instant");
            body.put("messages", messages);
            body.put("max_tokens", 2048);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String rawBody = response.body();
            System.out.println("Groq raw response: " + rawBody);

            JSONObject json = new JSONObject(rawBody);

            if (!json.has("choices")) {
                return "AI error: " + rawBody;
            }

            return json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            return "AI error: " + e.getMessage();
        }
    }

    public String summarize(String noteContent) {
        String shortContent = noteContent.substring(0, Math.min(noteContent.length(), 800));
        String prompt = "Summarize this note briefly with key points and important terms:\n" + shortContent;
        return callGroq(prompt);
    }

    public String generateQuiz(String noteContent) {
        String shortContent = noteContent.substring(0, Math.min(noteContent.length(), 800));
        String prompt = "Generate 10 quiz questions with 4 multiple choice options (A,B,C,D), "
                + "the correct answer, and a brief explanation for each. "
                + "Format each question clearly numbered 1-10.\n\n"
                + "Based on this content:\n" + shortContent;
        return callGroq(prompt);
    }

    public String chat(String userMessage, String context) {
        String prompt = context.isEmpty()
                ? "You are a helpful study assistant. Answer this question clearly and concisely:\n" + userMessage
                : "You are a helpful study assistant. Use this context to answer the question.\n\n"
                + "Context:\n" + context.substring(0, Math.min(context.length(), 600)) + "\n\n"
                + "Question: " + userMessage;
        return callGroq(prompt);
    }
}