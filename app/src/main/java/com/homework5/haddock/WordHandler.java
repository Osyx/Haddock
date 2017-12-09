package com.homework5.haddock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class WordHandler {
    private String[] words;


    public WordHandler() throws IOException {
        createList();
    }

    private void createList() throws IOException {
        FileReader fileReader = new FileReader(new java.io.File("words.txt"));
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        ArrayList<String> tempList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            tempList.add(line);
        }
        words = tempList.toArray(new String[0]);
        fileReader.close();
    }

    public String randomWord() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, words.length);
        return "\"" + words[randomNum] + "\"";
    }

    public void printFullList(){
        for (String word : words) {
            System.out.print("\"");
            System.out.print(word);
            System.out.println("\"");
        }

    }
}