package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class for requesting a random word from a (at start-up) loaded text file.
 */
public class WordHandler {
    private String[] words;

    /**
     * Constructor starting the class by loading the dictionary.
     */
    public WordHandler() throws ServerException {
        createList();
    }

    private void createList() throws ServerException {
        FileReader fileReader;
        try {
            fileReader = new FileReader(new java.io.File("words.txt"));
        } catch (FileNotFoundException e) {
            throw new ServerException("Could not read file \"words.txt\"", e);
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        ArrayList<String> tempList = new ArrayList<>();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                tempList.add(line);
            }
        } catch (IOException e) {
        throw new ServerException("Could not add file to memory, reading from buffer failed...", e);
    }
        words = tempList.toArray(new String[0]);
        try {
            fileReader.close();
        } catch (IOException e) {
            throw new ServerException("Could not close file reader...", e);
        }
    }

    /**
     * Ask for a random word in the dictionary.
     *
     * @return A <code>String</code> with a random word.
     */
    public String randomWord() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, words.length);
        return words[randomNum];
    }
}
