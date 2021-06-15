package agh.priceapp;
import agh.priceapp.contoller.util.ResultEntry;
import agh.priceapp.contoller.util.SingleEntry;

import agh.priceapp.model.Deal;
import agh.priceapp.model.DealDetails;
import agh.priceapp.model.Game;
import agh.priceapp.model.Store;
import agh.priceapp.service.DealService;
import agh.priceapp.service.GameService;
import agh.priceapp.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class DataRetriever {

    private final String EPIC = "epic games store";
    private final String STEAM = "steam";

    private final DealService dealService;
    private final StoreService storeService;
    private final GameService gameService;


    @Autowired
    public DataRetriever(DealService dealService, StoreService storeService, GameService gameService){
        this.dealService = dealService;
        this.storeService = storeService;
        this.gameService = gameService;
    }


    public List<ResultEntry> createResultEntry(String name) throws IllegalArgumentException {


        List<Game> allGames = gameService.getGame(name);
        if(allGames.isEmpty()) throw new IllegalArgumentException("No such game");
        final List<Game> games = allGames.subList(0, Math.min(allGames.size(), 3));

        List<Store> allStores = storeService.getAllStores();

        List<DealDetails> bestDeals = games.stream()
                .map(game -> dealService.getDealDetails(game.getCheapestDealID()))
                .collect(Collectors.toList());

        List <Store> bestStores = bestDeals.stream()
                .map(deal -> storeService.getStore(deal.getInfo().getStoreID(), allStores))
                .collect(Collectors.toList());

        Store steamStore = storeService.getStore(STEAM, allStores);
        Store epicStore = storeService.getStore(EPIC, allStores);

        Executor executor = Executors.newFixedThreadPool(2);
        CompletionService<Map<String, Deal>> completionService =
                new ExecutorCompletionService<>(executor);

        Future<Map<String, Deal>> steamFuture = completionService.submit(() -> dealService.getDeals(steamStore, games));
        Future<Map<String, Deal>> epicFuture = completionService.submit(() -> dealService.getDeals(epicStore, games));

        Map<String, Deal> steamDeals = new HashMap<>();
        Map<String, Deal> epicDeals = new HashMap<>();

        try {
            steamDeals = steamFuture.get();
            epicDeals = epicFuture.get();
        }
        catch (Exception e){
            e.printStackTrace();
        }


        List<ResultEntry> results = new ArrayList<>();

        for(int i = 0; i < bestDeals.size(); i++){

            DealDetails bestDeal = bestDeals.get(i);
            ResultEntry result = new ResultEntry(bestDeal.getName());

            result.addEntry(ResultEntry.SingleEntryType.BEST, new SingleEntry(bestDeal, bestStores.get(i)));

            Deal steamDeal = steamDeals.get(bestDeal.getName());
            Deal epicDeal = epicDeals.get(bestDeal.getName());

            SingleEntry steamEntry = new SingleEntry(steamDeal, steamStore);
            SingleEntry epicEntry = new SingleEntry(epicDeal, epicStore);

            if(steamDeal == null && epicDeal == null){
                results.add(result);
                continue;
            }

            if(steamDeal == null){
                result.addEntry(ResultEntry.SingleEntryType.BETTER, epicEntry);
            }
            else if(epicDeal == null){
                result.addEntry(ResultEntry.SingleEntryType.BETTER, steamEntry);
            }
            else{
                SingleEntry betterEntry = steamDeal.getPrice() < epicDeal.getPrice()
                        ? steamEntry : epicEntry;

                SingleEntry worseEntry = !(steamDeal.getPrice() < epicDeal.getPrice())
                        ? steamEntry : epicEntry;

                result.addEntry(ResultEntry.SingleEntryType.BETTER, betterEntry);
                result.addEntry(ResultEntry.SingleEntryType.WORSE, worseEntry);
            }
            results.add(result);

        }

        return results;

    }

}
