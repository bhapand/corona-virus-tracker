package io.lial.coronavirustracker.service;

import io.lial.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private static final String VIRUS_DATA_URL =
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private List<LocationStats> locationStats = new ArrayList<>();
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        StringReader csvResponseBodyReader = new StringReader(httpResponse.body());

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvResponseBodyReader);
        for (CSVRecord record : records) {
            LocationStats stat = new LocationStats();
            stat.setCountry(record.get("Country/Region"));
            String state = record.get("Province/State");
            stat.setState((state.isEmpty() || state.length()!=0) ? state : "State/Province information not available");
            int latestTotalCases = Integer.parseInt(record.get(record.size() -1));
            int previousTotalCases = Integer.parseInt(record.get(record.size() -2));
            stat.setLatestTotalCases(latestTotalCases);
            stat.setDiffFromPrevious(latestTotalCases - previousTotalCases);
            locationStats.add(stat);
        }

    }

    public List<LocationStats> getLocationStats() {
        return locationStats;
    }
}
