package agh.priceapp.service;

import agh.priceapp.model.Deal;
import agh.priceapp.model.DealDetails;
import agh.priceapp.model.Game;
import agh.priceapp.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

@Service
public class DealService {

    private final RestTemplate restTemplate;

    @Autowired
    public DealService(RestTemplate restTemplate){

        this.restTemplate = restTemplate;
    }

    public List<Deal> getDeals(Store store, Game game){
        return Arrays.asList(
                Optional.ofNullable(
                        restTemplate.getForObject("https://www.cheapshark.com/api/1.0/deals?storeID=" + store.getStoreID() +"&title=" + game.getExternal() + "&exact=1", Deal[].class)
                ).orElse(new Deal[0])
        );
    }
    public Deal getDeal(Store store, Game game){
        return getDeals(store, game).stream()
                .findFirst().orElse(null);
    }

    public DealDetails getDealDetails(String dealID) {
        URI uri = URI.create("https://www.cheapshark.com/api/1.0/deals?id=" + dealID);

        return restTemplate.getForObject(uri, DealDetails.class);
    }

    public DealDetails getDealDetails(Store store, Game game) {
        return getDealDetails(getDeal(store, game).getDealID());
    }

    public Map<String, Deal> getDeals(Store store, List<Game> games){

        Executor executor = Executors.newFixedThreadPool(games.size());
        CompletionService<Deal> completionService =
                new ExecutorCompletionService<>(executor);

        for(Game game : games){
            completionService.submit(() -> getDeal(store, game));
        }

        Map<String, Deal> deals = new HashMap<>();

        int received = 0;
        boolean errors = false;

        while(received < games.size() && !errors) {
            try {
                Future<Deal> resultFuture = completionService.take(); //blocks if none available
                Deal deal = resultFuture.get();
                if(deal != null){
                    deals.put(deal.getName(), deal);
                }
                received++;
            }
            catch(Exception e) {
                //log
                errors = true;
            }
        }


        return deals;
    }
}
