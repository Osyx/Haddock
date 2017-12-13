package com.homework5.haddock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class WordHandler {
    private String[] words;


    public WordHandler(InputStream file) throws IOException {
        createList(file);
    }

    private void createList(InputStream file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file));
        String line;
        ArrayList<String> tempList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            tempList.add(line);
        }
        words = tempList.toArray(new String[0]);
        bufferedReader.close();
    }

    String randomWord() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, words.length);
        return "\"" + words[randomNum] + "!\"";
    }

    public void printFullList(){
        for (String word : words) {
            System.out.print("\"");
            System.out.print(word);
            System.out.println("\"");
        }

    }
}