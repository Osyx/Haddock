package controller;

import model.ServerException;
import model.WordHandler;

public class Controller {
    private WordHandler wordHandler;

    public String getWord() throws ServerException {
        if(wordHandler == null)
            wordHandler = new WordHandler();
        return wordHandler.randomWord();
    }

}
