package agh.priceapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Store {

    private int storeID;
    private String storeName;

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }


    @Override
    public String toString() {
        return "Store{" +
                "storeID=" + storeID +
                ", storeName='" + storeName + '\'' +
                '}';
    }
}
