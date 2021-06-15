package agh.priceapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    private int gameID;
    private String cheapestDealID;
    private String external;

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getCheapestDealID() {
        return cheapestDealID;
    }

    public void setCheapestDealID(String cheapestDealID) {
        this.cheapestDealID = cheapestDealID;
    }

    public String getExternal() {
        return external;
    }

    public void setExternal(String external) {
        this.external = external;
    }



}
