package org.acme.hangman;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.util.*;

@ApplicationScoped
public class Hangman {

    private static final String LEADERBOARD_FILE_NAME = "leaderboard.csv";
    private static String[][] wordList = {
            {"baum","stein","hund","eule","hund"},
            {"bildung","computer","fenster","europa","pflanze"},
            {"himmelskoerper","krankenhaus","weihnachten","tastatur","bleistift", "Mississipi"}
    };
    private static int selectedDifficulty = 0;
    private static int selectedWordIndex = -1;
    private static Set<Character> targetCharacters = new HashSet<>();
    private static int allowedTries = 10;
    private static int failedTries = 0;
    private static Set<Character> triedLetters = new HashSet<>();
    private static ScoreEntry[] leaderboard;

    @Startup
    public void init() {
        Log.info("Init");
        initializeLeaderbord(5);
        /*while (true) {
            startGame();
            chooseDifficultyMode(scan);
            scanHowManyFailedTriesMax(scan);
            chooseRandomWord();
            checkIfGameWonOrLost(scan);
        }
        */
    }


    private static String readUserName(Scanner scanner) {
        System.out.println("Gib jetzt deinen Namen ein");
        return scanner.next();
    }

    private void initializeLeaderbord(int numPositions) {
        File file = new File(LEADERBOARD_FILE_NAME);
        if (file.exists()) {
            System.out.println("Bestehendes Leaderboard geladen.");
            leaderboard = ScoreEntryHelper.loadLeaderboard(LEADERBOARD_FILE_NAME);
        } else {
            System.out.println("Neues Leaderboard erstellt.");
            leaderboard = new ScoreEntry[numPositions];
            for (int i = 0; i < leaderboard.length; i++) {
                leaderboard[i] = new ScoreEntry(0, 0, "");
            }
        }
    }

    private static void startGame () {
        selectedDifficulty = 0;
        selectedWordIndex = -1;
        targetCharacters = new HashSet<>();
        allowedTries = 10;
        failedTries = 0;
        triedLetters = new HashSet<>();
    }

    private static void chooseRandomWord() {
        Random random = new Random();
        selectedWordIndex = random.nextInt(wordList[selectedDifficulty].length);
        char[] selectedWordArray = returnTargetWord().toCharArray();
        for (int i = 0; i < selectedWordArray.length; i++) {
            targetCharacters.add(selectedWordArray[i]);
        }

    }

    private static void chooseDifficultyMode(Scanner scan) {
        System.out.println("Bitte gib einen Schwierigkeitsgrad ein! 1 für einfach, 2 für medium, 3 für schwer.");
        selectedDifficulty = scanNextUserNumber(scan, 1, wordList.length) - 1;
    }

    private static void scanHowManyFailedTriesMax(Scanner scan) {
        System.out.println("Versuche mein Wort zu erraten. Davor gib ein, wie viel Fehler du machen darfst (Maximal 10)");
        allowedTries = scanNextUserNumber(scan,0,10);
    }

    private static void checkIfGameWonOrLost(Scanner scan) {
        /*
        while (true) {
            String outputWord = generateOutput(triedLetters);
            System.out.println(outputWord);
            char triedLetter = scanNextUserCharacter(scan);
            if (returnTargetWord().indexOf(triedLetter) == -1 && !triedLetters.contains(triedLetter)) {
                failedTries++;
                if (failedTries > allowedTries) {
                    System.out.println("Verloren! Das Wort war " + returnTargetWord() + ".");
                    askIfContinueGame(scan);
                    break;
                }
            }
            triedLetters.add(triedLetter);
            if (allCharactersCorrect(targetCharacters, triedLetters)) {
                System.out.println("Gewonnen! Du hast nur " + failedTries + " Fehlversuche gebraucht. \n Das Wort war " + returnTargetWord());
                askIfContinueGame(scan);
                break;
            }
        }
        */
    }

    private static int calculateScore () {
        double score = (returnTargetWord().length() * 100.0) / ((double) triedLetters.size());
        return (int) score;
    }

    private static void askIfContinueGame(Scanner scan) {
        if (ScoreEntryHelper.isInLeaderboard(leaderboard, calculateScore())) {
            ScoreEntry score = new ScoreEntry(
                    calculateScore(),
                    selectedDifficulty,
                    readUserName(scan)
            );
            leaderboard = ScoreEntryHelper.updateLeaderboard(leaderboard, score);
        } else {
            System.out.println("Kein Highscore erreicht.");
        }
        ScoreEntryHelper.printLeaderboard(leaderboard);
        System.out.println("Um das Spiel zu beenden, schreibe 0. Um nocheinmal zu spielen, schreibe 1");
        int continueGame = scanNextUserNumber(scan, 0, 1);
        if (continueGame == 0) {
            ScoreEntryHelper.storeLeaderboard(leaderboard, LEADERBOARD_FILE_NAME);
            System.exit(0);
        }
    }

    private static String returnTargetWord() {
        return wordList[selectedDifficulty][selectedWordIndex];
    }

    /**
     * Reads the numeric input from the given scanner and returns it as a number between minimum
     * and maximum (both incl.). Repeats parsing until a valid number is entered.
     * @param scan The input scanner
     * @param minimum Lower bound (incl.)
     * @param maximum Upper bound (incl.)
     * @return The valid parsed number between minimum and maximum
     */
    private static int scanNextUserNumber(Scanner scan, int minimum, int maximum) {
        while (true) {
            String userInput = scan.next();
            try {
                int numberInput = Integer.parseInt(userInput);
                if (numberInput >= minimum && numberInput <= maximum) {
                    return numberInput;
                }
                System.out.println("Bitte gib eine Zahl zwischen " + minimum + " und " + maximum + " ein.");
            } catch (NumberFormatException nfe) {
                System.out.println("Das ist keine gültige Eingabe!");
            }
        }
    }

    public char getNextUserCharacter(String input) {
        String characters = "abcdefghijklmnopqrstuvwxyz";
        char userInput = input.toLowerCase().charAt(0);
        if (characters.indexOf(userInput) != -1) {
            return userInput;
        } else {
            System.out.println("Eingabe ungültig. Gib bitte einen Buchstaben ein!");
            return '!';
        }
    }

    private static char scanNextUserCharacter(Scanner scan) {
        String characters = "abcdefghijklmnopqrstuvwxyz";
        while (true) {
            System.out.println("Gib jetzt einen Buchstaben ein! Du hast bis jetzt folgende Buchstaben geraten:" + triedLetters);
            System.out.println("Fehlversuche: " + failedTries);
            char userInput = scan.next().toLowerCase().charAt(0);
            // userInput >= 'a' && userInput <= 'z'
            if (characters.indexOf(userInput) != -1) {
                return userInput;
            } else {
                System.out.println("Eingabe ungültig. Gib bitte einen Buchstaben ein!");
            }
        }
    }

    public String generateOutput(Set<Character> triedLetters) {
        String targetWord = returnTargetWord();
        int targetLength = targetWord.length();
        char[] returnCharacters = new char[targetLength];
        String returnString = "";
        Arrays.fill(returnCharacters,'-');

        for (int i = 0; i < targetLength; i++) {
            char expectedChar = targetWord.charAt(i);
            if (triedLetters.contains(expectedChar)) {
                returnCharacters[i] = expectedChar;
            }
            returnString += returnCharacters[i];
        }
        return returnString;
    }

    private static boolean allCharactersCorrect(Set<Character> targetCharacters, Set<Character> triedLetters) {
        return (triedLetters.containsAll(targetCharacters));
    }

}
