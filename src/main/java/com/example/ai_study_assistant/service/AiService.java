package com.example.ai_study_assistant.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String callGroq(JSONArray messages) {
        try {
            String url = "https://api.groq.com/openai/v1/chat/completions";

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
            System.out.println("Groq response: " + rawBody);

            JSONObject json = new JSONObject(rawBody);
            if (!json.has("choices")) return "AI error: " + rawBody;

            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            return "AI error: " + e.getMessage();
        }
    }

    private String callGroqSingle(String prompt) {
        JSONArray messages = new JSONArray();
        JSONObject msg = new JSONObject();
        msg.put("role", "user");
        msg.put("content", prompt);
        messages.put(msg);
        return callGroq(messages);
    }

    public String summarize(String noteContent) {
        String shortContent = noteContent.substring(0, Math.min(noteContent.length(), 800));
        return callGroqSingle("Summarize this note with key points and important terms:\n" + shortContent);
    }

    public String generateQuiz(String noteContent) {
        String shortContent = noteContent.substring(0, Math.min(noteContent.length(), 800));
        String prompt = "Generate exactly 5 quiz questions based on this content.\n"
                + "Return ONLY a valid JSON array, no extra text, no markdown.\n"
                + "Format:\n"
                + "[\n"
                + "  {\n"
                + "    \"question\": \"question text\",\n"
                + "    \"options\": [\"A. option1\", \"B. option2\", \"C. option3\", \"D. option4\"],\n"
                + "    \"answer\": \"A\",\n"
                + "    \"explanation\": \"brief explanation\"\n"
                + "  }\n"
                + "]\n\n"
                + "Content:\n" + shortContent;
        return callGroqSingle(prompt);
    }

    public String checkAnswer(String question, String options, String userAnswer, String correctAnswer, String explanation) {
        String prompt = "The student answered a quiz question.\n\n"
                + "Question: " + question + "\n"
                + "Options: " + options + "\n"
                + "Student's answer: " + userAnswer + "\n"
                + "Correct answer: " + correctAnswer + "\n"
                + "Explanation: " + explanation + "\n\n"
                + "Tell the student if they are correct or wrong. "
                + "If wrong, explain why the correct answer is right. Keep it brief and encouraging.";
        return callGroqSingle(prompt);
    }

    public String chat(String userMessage, String context, List<Map<String, String>> history) {
        JSONArray messages = new JSONArray();

        // System message
        JSONObject system = new JSONObject();
        system.put("role", "system");
        system.put("content", context.isEmpty()
                ? "You are a helpful study assistant. Answer clearly and concisely. Remember the full conversation."
                : "You are a helpful study assistant. Use this context to answer questions:\n\n" + context.substring(0, Math.min(context.length(), 600)));
        messages.put(system);

        // Add conversation history (last 10 messages for memory)
        int start = Math.max(0, history.size() - 10);
        for (int i = start; i < history.size(); i++) {
            Map<String, String> h = history.get(i);
            JSONObject msg = new JSONObject();
            msg.put("role", h.get("role"));
            msg.put("content", h.get("content"));
            messages.put(msg);
        }

        // Add current message
        JSONObject current = new JSONObject();
        current.put("role", "user");
        current.put("content", userMessage);
        messages.put(current);

        return callGroq(messages);
    }
}