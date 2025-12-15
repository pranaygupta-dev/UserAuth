package com.example.UserAuth.scheduler;

import com.example.UserAuth.entity.JournalEntry;
import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepositoryImpl;
import com.example.UserAuth.service.EmailService;
import com.example.UserAuth.service.SentimentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

//    @Scheduled(cron = "0 0 9 * * SUN")
    @Scheduled(cron = "0 * * ? * *")
    public void fetchUsersAndSendSaMail(){
        // Implementation will go here
        List<User> users = userRepository.getUserForSA();
        for(User user : users){
//            List<JournalEntry> journalEntries = user.getJournalEntries();
//            List<String> filteredEntries = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x -> x.getContent()).collect(Collectors.toList());
//            String entry = String.join("", filteredEntries);
//            String sentiment = sentimentAnalysisService.getSentiment(entry);
            emailService.sendEmail(user.getEmail(), "Weekly Sentiment Analysis", "Your weekly sentiment analysis is ready.");
        }
    }
}
