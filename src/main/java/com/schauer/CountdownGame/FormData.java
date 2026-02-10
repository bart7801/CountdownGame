package com.schauer.CountdownGame;
/*
   The FormData class is a simple Java POJO (Plain Old Java Object) that serves as a data transfer object (DTO) for
   the form submission. It captures the input from the user in the HTML form and passes it to the controller.

    private String letters: The field to store the user input representing the letters for which the application will
    find valid words.
    private String language: The field to store the selected language for dictionary lookup.

    Constructors:
        Default constructor: Used to create an instance of the FormData class without any initial values. It is often
        required by Spring to create form backing objects.
        Constructor with parameters: Used to create an instance of the FormData class with the letters and language
        fields initialized to the provided values.

    Getter and Setter methods:
        getLetters(): A getter method that returns the value of the letters field.
        setLetters(): A setter method that sets the value of the letters field.
        getLanguage(): A getter method that returns the selected language.
        setLanguage(): A setter method that sets the selected language.

   The FormData class is used to bind the form fields in the index.html template. When the form is submitted, Spring
   automatically maps the form data to an instance of the FormData class, and the controller's findWords() method
   receives this instance as a parameter, extracting the user input and processing it to find valid words.
*/
public class FormData {
    private String letters;
    private String language;

    // Default constructor
    public FormData() {
    }

    // Constructor with parameters
    public FormData(String letters, String language) {
        this.letters = letters;
        this.language = language;
    }

    // Getter and Setter methods for the 'letters' field
    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
