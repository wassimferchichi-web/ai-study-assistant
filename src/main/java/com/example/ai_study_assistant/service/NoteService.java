package com.example.ai_study_assistant.service;

import com.example.ai_study_assistant.model.Note;
import com.example.ai_study_assistant.model.User;
import com.example.ai_study_assistant.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getAllNotes(Long userId) {
        return noteRepository.findByUserId(userId);
    }

    public Note createNote(Note note, User user) {
        note.setUser(user);
        return noteRepository.save(note);
    }

    public Note getNoteById(Long id, Long userId) {
        return noteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
    }

    public void deleteNote(Long id, Long userId) {
        noteRepository.deleteByIdAndUserId(id, userId);
    }
}