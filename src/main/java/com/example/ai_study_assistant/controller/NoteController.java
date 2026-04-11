package com.example.ai_study_assistant.controller;

import com.example.ai_study_assistant.model.Note;
import com.example.ai_study_assistant.service.AiService;
import com.example.ai_study_assistant.service.NoteService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.ResponseEntity;
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

    public NoteController(NoteService noteService, AiService aiService) {
        this.noteService = noteService;
        this.aiService = aiService;
    }

    @GetMapping("/notes")
    public List<Note> getAllNotes() {
        return noteService.getAllNotes();
    }

    @PostMapping("/notes")
    public Note createNote(@RequestBody Note note) {
        return noteService.createNote(note);
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notes/{id}/summarize")
    public ResponseEntity<Map<String, String>> summarize(@PathVariable Long id) {
        Note note = noteService.getNoteById(id);
        String summary = aiService.summarize(note.getContent());
        return ResponseEntity.ok(Map.of("summary", summary));
    }

    @PostMapping("/notes/{id}/quiz")
    public ResponseEntity<Map<String, String>> generateQuiz(@PathVariable Long id) {
        Note note = noteService.getNoteById(id);
        String quiz = aiService.generateQuiz(note.getContent());
        return ResponseEntity.ok(Map.of("quiz", quiz));
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody Map<String, String> payload) {
        String message = payload.getOrDefault("message", "");
        String context = payload.getOrDefault("context", "");
        String response = aiService.chat(message, context);
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
            Note saved = noteService.createNote(note);

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