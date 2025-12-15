package com.example.UserAuth.entity;

import com.example.UserAuth.enums.Sentiment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "journal_entries")
@Data
@NoArgsConstructor
public class JournalEntry {
    @Id
    private ObjectId id;

    @NotNull
    private String title;

    private String content;

    private LocalDateTime date;

    private Sentiment sentiment;
}
