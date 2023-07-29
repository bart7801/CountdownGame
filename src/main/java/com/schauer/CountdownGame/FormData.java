package com.schauer.CountdownGame;
/*
   The FormData class is a simple Java POJO (Plain Old Java Object) that serves as a data transfer object (DTO) for the form submission. It is used to capture the input from the user in the HTML form and pass it to the controller. The class has a single field, letters, which represents the letters input provided by the user.

    private String letters: The field to store the user input, which represents the letters for which the application will find valid words.

    Constructors:
        Default constructor: This constructor is used to create an instance of the FormData class without any initial values. It is often required by Spring to create form backing objects.
        Constructor with parameters: This constructor is used to create an instance of the FormData class with the letters field initialized to the provided value.

    Getter and Setter methods:
        getLetters(): A getter method that returns the value of the letters field.
        setLetters(): A setter method that sets the value of the letters field.

The FormData class is used to bind the form field with the name "letters" in the index.html template. When the form is submitted, Spring automatically maps the form data to an instance of the FormData class, and the controller's findWords() method receives this instance as a parameter, extracting the user input (letters) and processing it to find valid words.
*/
public class FormData {
    private String letters;

    // Default constructor
    public FormData() {
    }

    // Constructor with parameters
    public FormData(String letters) {
        this.letters = letters;
    }

    // Getter and Setter methods for the 'letters' field
    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }
}
