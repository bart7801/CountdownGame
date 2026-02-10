package com.schauer.CountdownGame;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CountdownGameController {

    private List<String> allWords = new ArrayList<>();

    public CountdownGameController() {
        // Read the default dictionary (English) from the file when the controller is initialized
        allWords = readDictionaryFromFile("englishdictionary.txt");
    }

    @RequestMapping("/")
    public String index(@ModelAttribute("formData") FormData formData) {
        // This method returns the name of the Thymeleaf template to be displayed when accessing the root URL ("/").
        // In this case, it returns "index", which corresponds to "index.html".
        return "index";
    }

    @PostMapping("/find-words")
    public String findWords(@ModelAttribute("formData") FormData formData, Model model) {
        // This method is called when the user submits the form on the "index" page.
        // It receives the form data through the @ModelAttribute annotation, and the Model to add attributes for rendering the "result" page.

        String letters = formData.getLetters().toLowerCase(); // Get the set of letters from the form
        String language = formData.getLanguage(); // Get the selected language from the form
        Map<String, String> wordDefinitions = new HashMap<>();
        if ("english".equals(language)) {
            wordDefinitions = readDictionaryWithDefinitions("Oxford English Dictionary.txt");
            allWords = new ArrayList<>(wordDefinitions.keySet());
            if (allWords.isEmpty()) {
                allWords = readDictionaryFromFile("englishdictionary.txt");
            }
        } else {
            allWords = readDictionaryFromFile("polishdictionary.txt");
        }

        int maxLength = letters.length();
        List<String> validWords = new ArrayList<>();
        Map<Integer, Integer> wordLengthCount = new LinkedHashMap<>(); // Store the count of valid words by length

        while (maxLength >= 1 && validWords.isEmpty()) {
            // Find words with the given letters and update the wordLengthCount
            validWords = findWordsWithLetters(allWords, letters, maxLength, wordLengthCount);
            maxLength--;
        }

        // Sort words from longest to shortest, and in case of the same length - alphabetically
        validWords.sort(new WordComparator());

        List<WordResult> wordResults = new ArrayList<>();
        for (String word : validWords) {
            wordResults.add(new WordResult(word, wordDefinitions.get(word)));
        }

        // Sort the wordLengthCount map by key (word length) in reverse order to get the longest words first
        Map<Integer, Integer> sortedWordLengthCount = wordLengthCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        model.addAttribute("foundWords", validWords); // Add the list of valid words to the model
        model.addAttribute("numberOfWords", validWords.size()); // Add the number of valid words to the model
        model.addAttribute("wordLengthCount", sortedWordLengthCount); // Add the word length count to the model
        model.addAttribute("wordResults", wordResults); // Add words paired with definitions for display

        // Save the words to the file
        String outputFileName = "words_with_given_letters.txt";
        saveWordsToFile(validWords, outputFileName);

        // This method returns the name of the Thymeleaf template to be displayed when the form is submitted.
        // In this case, it returns "result", which corresponds to "result.html".
        return "result";
    }

    // Method to read the dictionary from a text file in the "resources" folder
    private List<String> readDictionaryFromFile(String filename) {
        List<String> words = new ArrayList<>();
        InputStream dictionaryStream = getClass().getClassLoader().getResourceAsStream(filename);
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

    private Map<String, String> readDictionaryWithDefinitions(String filename) {
        Map<String, String> words = new LinkedHashMap<>();
        InputStream dictionaryStream = getClass().getClassLoader().getResourceAsStream(filename);
        if (dictionaryStream == null) {
            return words;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(dictionaryStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) {
                    continue;
                }
                int separatorIndex = trimmedLine.indexOf("  ");
                if (separatorIndex == -1) {
                    continue;
                }
                String word = trimmedLine.substring(0, separatorIndex).trim().toLowerCase();
                String definition = trimmedLine.substring(separatorIndex).trim();
                if (!word.isEmpty()) {
                    words.put(word, definition);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    // Method to find words containing the given letters with a maximum length
    private List<String> findWordsWithLetters(List<String> allWords, String letters, int maxLength, Map<Integer, Integer> wordLengthCount) {
        List<String> validWords = new ArrayList<>();
        for (String word : allWords) {
            int wordLength = word.length();
            if (wordLength <= maxLength && containsOnlyGivenLetters(word, letters)) {
                validWords.add(word);
                wordLengthCount.put(wordLength, wordLengthCount.getOrDefault(wordLength, 0) + 1); // Update the word length count
            }
        }
        return validWords;
    }

    // Method to check if a word contains the given letters in the required quantity
    private boolean containsOnlyGivenLetters(String word, String letters) {
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

    // Method to save words to a text file
    private void saveWordsToFile(List<String> words, String filename) {
        String absolutePath = System.getProperty("user.dir") + File.separator + filename;
        try (PrintWriter pw = new PrintWriter(new FileWriter(absolutePath))) {
            for (String word : words) {
                pw.println(word); // Save the word on a new line
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class WordResult {
        private final String word;
        private final String definition;

        public WordResult(String word, String definition) {
            this.word = word;
            this.definition = definition;
        }

        public String getWord() {
            return word;
        }

        public String getDefinition() {
            return definition;
        }
    }
}
