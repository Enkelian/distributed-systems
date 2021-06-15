package agh.priceapp.service;

import agh.priceapp.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private final RestTemplate restTemplate;

    @Autowired
    public StoreService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<Store> getAllStores(){
        return Arrays.asList(
                        Optional.ofNullable(
                            restTemplate.getForObject("https://www.cheapshark.com/api/1.0/stores", Store[].class)
                        ).orElse(new Store[0])
                );
    }

    public Store getStore(String name, List<Store> stores){
        return stores.stream()
                .filter(store -> store.getStoreName().equalsIgnoreCase(name))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No such store"));

    }

    public Store getStore(int storeId, List<Store> stores){
        return stores.stream()
                .filter(store -> store.getStoreID() == storeId)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No such store"));
    }


}
