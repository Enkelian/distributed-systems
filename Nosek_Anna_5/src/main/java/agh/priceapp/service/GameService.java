package agh.priceapp.service;

import agh.priceapp.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final RestTemplate restTemplate;

    @Autowired
    public GameService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<Game> getGame(String title){
        return Arrays.asList(
                Optional.ofNullable(
                        restTemplate.getForObject("https://www.cheapshark.com/api/1.0/games?title=" + title, Game[].class)
                ).orElse(new Game[0])
        );


    }

}
