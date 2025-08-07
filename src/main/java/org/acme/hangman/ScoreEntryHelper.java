package org.acme.hangman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScoreEntryHelper {

    public static int checkWhichLeaderboardPlace(ScoreEntry[] leaderboard, int toCheck) {
        if (!isInLeaderboard(leaderboard, toCheck)) {
            return -1;
        }
        for (int i = leaderboard.length; i > 0; i--) {
            int current = leaderboard[i - 1].getScore();
            if (toCheck < current) {
                return i;
            }
        }
        return 0;
    }

    public static boolean isInLeaderboard(ScoreEntry[] leaderboard, int scoreToCheck) {
        return scoreToCheck > leaderboard[leaderboard.length - 1].getScore();
    }


    public static ScoreEntry[] updateLeaderboard(ScoreEntry[] originalLeaderboard, ScoreEntry scoreToAdd) {
        ScoreEntry[] updatedLeaderboard = new ScoreEntry[originalLeaderboard.length];
        int placement = checkWhichLeaderboardPlace(originalLeaderboard, scoreToAdd.getScore());
        if (placement == -1) {
            return originalLeaderboard;
        }
        for (int i = placement; i >= 0 ; i--) {
            updatedLeaderboard[i] = originalLeaderboard[i];
        }
        updatedLeaderboard[placement] = scoreToAdd;
        for (int i = placement + 1; i < originalLeaderboard.length; i++) {
            updatedLeaderboard[i] = originalLeaderboard[i - 1];
        }
        return updatedLeaderboard;
    }

    public static void printLeaderboard(ScoreEntry[] leaderboard) {
        for (int i = 0; i < leaderboard.length; i++) {
            System.out.println(leaderboard[i]);
        }
        System.out.println("");
    }
    public static ScoreEntry[] loadLeaderboard(String filepath) {
        List<ScoreEntry> leaderbord = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // Skip header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Split line into fields
                String[] fields = line.split(",", -1); // -1 preserves empty fields

                // Parse fields
                int score = Integer.parseInt(fields[0]);
                int difficulty = Integer.parseInt(fields[1]);
                String name = fields[2];

                // Create object and add to list
                leaderbord.add(new ScoreEntry(score, difficulty, name));
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }

        return leaderbord.toArray(ScoreEntry[]::new);
    }

    public static void storeLeaderboard(ScoreEntry[] leaderboard, String filepath) {
        try (FileWriter writer = new FileWriter(filepath)) {
            // Write CSV header
            writer.append("Score,Difficulty,Name\n");

            // Write object data
            for (ScoreEntry entry : leaderboard) {
                writer.append(String.valueOf(entry.getScore()))
                        .append(",")
                        .append(String.valueOf(entry.getDifficulty()))
                        .append(",")
                        .append(entry.getInitials())
                        .append("\n");
            }
            System.out.println("CSV file gespeichert.");
        } catch (IOException e) {
            System.out.println("Fehler");
        }
    }
 }


