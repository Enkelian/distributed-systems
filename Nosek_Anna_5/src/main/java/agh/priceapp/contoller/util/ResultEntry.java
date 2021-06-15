package agh.priceapp.contoller.util;

import agh.priceapp.model.Game;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ResultEntry {

    public enum SingleEntryType {
        BEST, BETTER, WORSE
    }

    private String gameName;
    private final Map<SingleEntryType, SingleEntry> entries;

    public ResultEntry(String gameName) {
        this.gameName = gameName;
        this.entries = new HashMap<>();
    }

    public String getGameName(){
        return gameName;
    }

    public ResultEntry addEntry(SingleEntryType type, SingleEntry entry){
        entries.put(type, entry);
        return this;
    }

    public SingleEntry getSingleEntry(SingleEntryType type){
        return entries.get(type);
    }

    public SingleEntry getBestEntry(){
        return entries.get(SingleEntryType.BEST);
    }

    public SingleEntry getBetterEntry(){
        return entries.get(SingleEntryType.BETTER);
    }

    public String getBetterStoreName(){
        if(getBetterEntry() == null) return "No offers";
        return getBetterEntry().getStore().getStoreName();
    }

    public String getBestStoreName(){
        return getBestEntry().getStore().getStoreName();
    }

    public String getDifference(){
        if(entries.get(SingleEntryType.WORSE) == null){
            return "No competition";
        }
        return
            String.format("%.2f", entries.get(SingleEntryType.WORSE).getDealInfo().getPrice() -
                    entries.get(SingleEntryType.BETTER).getDealInfo().getPrice());
    }

    public double getBestPrice(){
        return getBestEntry().getDealInfo().getPrice();
    }

    public String getBetterPrice(){
        if(getBetterEntry() == null) return "No price";
        return Double.toString(entries.get(SingleEntryType.BETTER).getDealInfo().getPrice());
    }

    public Map<SingleEntryType, SingleEntry> getEntries(){
        return entries;
    }

}
