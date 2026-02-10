package com.schauer.CountdownGame;

import java.io.*;
import java.util.*;

public class CountdownGame {

    public static void main(String[] args) {
        System.out.println("Welcome to Countdown Game!");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the set of letters: ");
        String letters = scanner.nextLine().toLowerCase();

        int maxLength = letters.length();
        List<String> allWords = readDictionaryFromFile("polishdictionary.txt");
        List<String> validWords = new ArrayList<>();

        while (maxLength >= 1 && validWords.isEmpty()) {
            validWords = findWordsWithLetters(allWords, letters, maxLength);
            maxLength--;
        }

        // Sort words from longest to shortest, and in case of the same length - alphabetically
        Collections.sort(validWords, new WordComparator());

        // Display results in the console
        System.out.println("Number of found words: " + validWords.size());
        System.out.println("All words containing the given letters:");
        for (String word : validWords) {
            System.out.println(word);
        }

        String outputFileName = "words_with_given_letters.txt";
        saveWordsToFile(validWords, outputFileName);

        System.out.println("All words containing the given letters have been saved to a file.");
    }

    // Method to read the dictionary from a text file in the "resources" folder
    private static List<String> readDictionaryFromFile(String filename) {
        List<String> words = new ArrayList<>();
        InputStream dictionaryStream = CountdownGame.class.getResourceAsStream("/" + filename);
        if (dictionaryStream == null) {
            return words;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(dictionaryStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    // Method to find words containing the given letters with a maximum length
    private static List<String> findWordsWithLetters(List<String> allWords, String letters, int maxLength) {
        List<String> validWords = new ArrayList<>();
        for (String word : allWords) {
            int wordLength = word.length();
            if (wordLength <= maxLength && containsOnlyGivenLetters(word, letters)) {
                validWords.add(word);
            }
        }
        return validWords;
    }

    // Method to check if a word contains the given letters in the required quantity
    private static boolean containsOnlyGivenLetters(String word, String letters) {
        String tempLetters = new String(letters); // Create a copy of the set of letters
        for (char letter : word.toCharArray()) {
            int index = tempLetters.indexOf(letter);
            if (index == -1) {
                return false;
            }
            tempLetters = tempLetters.substring(0, index) + tempLetters.substring(index + 1); // Remove the found letter
        }
        return true;
    }

    // Method to count occurrences of a letter in a word
    private static int countLetterOccurrences(String word, char letter) {
        int count = 0;
        for (char c : word.toCharArray()) {
            if (c == letter) {
                count++;
            }
        }
        return count;
    }

    // Method to save words to a text file
    private static void saveWordsToFile(List<String> words, String filename) {
        String absolutePath = System.getProperty("user.dir") + File.separator + filename;
        try (PrintWriter pw = new PrintWriter(new FileWriter(absolutePath))) {
            for (String word : words) {
                pw.println(word); // Save the word on a new line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Comparator to sort words from longest to shortest, and in case of the same length - alphabetically
    private static class WordComparator implements Comparator<String> {
        @Override
        public int compare(String word1, String word2) {
            int lengthComparison = Integer.compare(word2.length(), word1.length());
            if (lengthComparison == 0) {
                return word1.compareTo(word2);
            }
            return lengthComparison;
        }
    }
}
