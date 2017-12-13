package controller;

import model.ServerException;
import model.WordHandler;

public class Controller {
    private WordHandler wordHandler;
    private String NO_CONN_ERROR = "\"getWord\" called though no connection has been set up...";

    public String newConnection() throws ServerException {
        wordHandler = new WordHandler();
        return wordHandler.randomWord();
    }

    public String getWord() throws ServerException {
        if(wordHandler == null)
            throw new ServerException(NO_CONN_ERROR);
        return "";
    }

}
