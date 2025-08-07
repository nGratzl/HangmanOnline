package org.acme.hangman;

import java.util.Objects;

public class ScoreEntry {

    private int difficulty;
    private int score;
    private String initials; // store the player name as three capital letters or - if not supplied

    public ScoreEntry(int score, int difficulty, String name) {
        this.score = score;
        this.difficulty = difficulty;
        this.initials = name;
        while (this.initials.length() < 3) {
            this.initials = this.initials + "-";
        }
        this.initials = this.initials.toUpperCase().substring(0, 3);

    }

    public int getScore() {
        return score;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getInitials() {
        return initials;
    }

    @Override
    public String toString() {
        return "Dein Highscore {" +
                "difficulty = " + difficulty +
                ", score = " + score +
                ", initials = '" + initials + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ScoreEntry that)) return false;
        return getDifficulty() == that.getDifficulty() && getScore() == that.getScore() && Objects.equals(getInitials(), that.getInitials());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDifficulty(), getScore(), getInitials());
    }
}
