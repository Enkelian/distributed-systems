package agh.priceapp.contoller;

import agh.priceapp.DataRetriever;
import agh.priceapp.contoller.util.ResultEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FormController {

    private final DataRetriever dataRetriever;

    @Autowired
    public FormController(DataRetriever dataRetriever){
        this.dataRetriever = dataRetriever;

    }

    @GetMapping("/")
    public String getMainSite(Model model){
        return "form";
    }

    @PostMapping
    public String getResults(@RequestParam String gameName, Model model){
        try{
            List<ResultEntry> results = dataRetriever.createResultEntry(gameName);
            model.addAttribute("results", results);
        }
        catch (Exception e){
            return "error";
        }
        return "result";
    }
}
