package com.example.ai_study_assistant.controller;
import java.util.List;
import com.example.ai_study_assistant.model.Note;
import com.example.ai_study_assistant.model.User;
import com.example.ai_study_assistant.service.AiService;
import com.example.ai_study_assistant.service.AuthService;
import com.example.ai_study_assistant.service.NoteService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NoteController {

    private final NoteService noteService;
    private final AiService aiService;
    private final AuthService authService;

    public NoteController(NoteService noteService, AiService aiService, AuthService authService) {
        this.noteService = noteService;
        this.aiService = aiService;
        this.authService = authService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.getUserByEmail(auth.getName());
    }

    @GetMapping("/notes")
    public List<Note> getAllNotes() {
        return noteService.getAllNotes(getCurrentUser().getId());
    }

    @PostMapping("/notes")
    public Note createNote(@RequestBody Note note) {
        return noteService.createNote(note, getCurrentUser());
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteById(id, getCurrentUser().getId()));
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id, getCurrentUser().getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notes/{id}/summarize")
    public ResponseEntity<Map<String, String>> summarize(@PathVariable Long id) {
        Note note = noteService.getNoteById(id, getCurrentUser().getId());
        String summary = aiService.summarize(note.getContent());
        return ResponseEntity.ok(Map.of("summary", summary));
    }

    @PostMapping("/notes/{id}/quiz")
    public ResponseEntity<Map<String, String>> generateQuiz(@PathVariable Long id) {
        Note note = noteService.getNoteById(id, getCurrentUser().getId());
        String quiz = aiService.generateQuiz(note.getContent());
        return ResponseEntity.ok(Map.of("quiz", quiz));
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody Map<String, Object> payload) {
        String message = (String) payload.getOrDefault("message", "");
        String context = (String) payload.getOrDefault("context", "");
        List<Map<String, String>> history = (List<Map<String, String>>) payload.getOrDefault("history", new java.util.ArrayList<>());
        String response = aiService.chat(message, context, history);
        return ResponseEntity.ok(Map.of("response", response));
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            String content;

            if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
                PDDocument document = PDDocument.load(file.getBytes());
                PDFTextStripper stripper = new PDFTextStripper();
                content = stripper.getText(document);
                document.close();
            } else {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                file.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                content = sb.toString();
            }

            Note note = new Note();
            note.setTitle(filename);
            note.setContent(content);
            Note saved = noteService.createNote(note, getCurrentUser());

            return ResponseEntity.ok(Map.of(
                    "message", "File uploaded successfully",
                    "noteId", String.valueOf(saved.getId()),
                    "title", String.valueOf(saved.getTitle())
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}