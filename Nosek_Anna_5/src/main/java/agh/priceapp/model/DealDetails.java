package agh.priceapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealDetails implements DealInfo {

    @JsonProperty("gameInfo")
    private GameInfo gameInfo;

    public GameInfo getInfo() {
        return gameInfo;
    }

    public void setInfo(GameInfo info) {
        this.gameInfo = info;
    }

    public String getName(){ return getInfo().getName(); }

    public double getPrice(){ return getInfo().getRetailPrice(); }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GameInfo{

        private int storeID;
        private int gameID;
        private String name;
        private double salePrice;
        private double retailPrice;

        public int getStoreID() {
            return storeID;
        }

        public void setStoreID(int storeID) {
            this.storeID = storeID;
        }

        public int getGameID() {
            return gameID;
        }

        public void setGameID(int gameID) {
            this.gameID = gameID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getSalePrice() {
            return salePrice;
        }

        public void setSalePrice(double salePrice) {
            this.salePrice = salePrice;
        }

        public double getRetailPrice() {
            return retailPrice;
        }

        public void setRetailPrice(double retailPrice) {
            this.retailPrice = retailPrice;
        }

    }

}
