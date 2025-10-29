package com.example.UserAuth.service;

import com.example.UserAuth.entity.JournalEntry;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface JournalEntryService {
    void saveEntry(JournalEntry journalEntry, String userName);

    Optional<JournalEntry> findById(ObjectId id);

    boolean deleteById(ObjectId id, String userName);
}
