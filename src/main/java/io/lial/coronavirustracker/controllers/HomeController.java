package io.lial.coronavirustracker.controllers;

import io.lial.coronavirustracker.models.LocationStats;
import io.lial.coronavirustracker.service.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    private List<LocationStats> allStats = new ArrayList<>();
    private List<LocationStats> indiaStats = new ArrayList<>();

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStats> stats = coronaVirusDataService.getLocationStats();
        int totalReportedCases = stats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int diffReportedCases = stats.stream().mapToInt(stat -> stat.getDiffFromPrevious()).sum();
        model.addAttribute("locationStats", stats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("diffReportedCases", diffReportedCases);
        this.allStats = stats;
        return "home";
    }

    @GetMapping("/india")
    public String indiaHome(Model model) {
        this.indiaStats = allStats.stream().filter(stat->stat.getCountry().equalsIgnoreCase("india")).collect(Collectors.toList());
        int totalReportedCases = indiaStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int diffReportedCases = indiaStats.stream().mapToInt(stat -> stat.getDiffFromPrevious()).sum();
        model.addAttribute("locationStats", indiaStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("diffReportedCases", diffReportedCases);
        return "india";
    }
}
