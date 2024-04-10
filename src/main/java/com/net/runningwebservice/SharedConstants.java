package com.net.runningwebservice;

import java.util.ArrayList;
import java.util.List;

public class SharedConstants {
    static final String SOURCE = "http://www.semanticweb.org/guind/ontologies/runningeventontology";
    static final String NS = SOURCE + "#";
    static final String output_filename = "/Users/net/Downloads/running-web-service/src/main/resources/WriteInstance3.rdf";
    static final String rulesPath = "/Users/net/Downloads/running-web-service/src/main/resources/testrules1.rules";
    static final String runURI = "http://www.semanticweb.org/guind/ontologies/runningeventontology#";
    static final String ontologyPath = "/Users/net/Downloads/running-web-service/src/main/resources/RunningEventOntologyFinal2.rdf";
    static  final String SECRET_KEY = "OIgDYngM08yDE6z7NQi8RqwzIOqErSaZM1crYnAK0tI";

    public static ArrayList<String> formatEventNames(List<String> eventNames) {
        ArrayList<String> formattedNames = new ArrayList<>();
        for (String eventName : eventNames) {
            StringBuilder formattedName = new StringBuilder();
            formattedName.append(eventName.charAt(0)); // Add the first character as-is

            // Iterate through the rest of the characters in the event name
            for (int i = 1; i < eventName.length(); i++) {
                char currentChar = eventName.charAt(i);
                // Check if the current character is an uppercase letter or a digit
                if (Character.isUpperCase(currentChar) || Character.isDigit(currentChar)) {
                    // If the current character is a digit and the previous character is not a digit,
                    // or if the current character is uppercase, add a space before it
                    if (Character.isDigit(currentChar) && !Character.isDigit(eventName.charAt(i - 1))) {
                        formattedName.append(' ');
                    } else if (Character.isUpperCase(currentChar)) {
                        formattedName.append(' ');
                    }
                }
                formattedName.append(currentChar); // Add the current character to the formatted name
            }
            formattedNames.add(formattedName.toString());
        }
        return formattedNames;
    }
}
