package coronavirustracker.services;

import coronavirustracker.models.LocationStats;
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

    private static String COVID_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    public List<LocationStats> allStats = new ArrayList<>();
    public int totalCases;

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request =HttpRequest.newBuilder()
                .uri(URI.create(COVID_DATA_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Commons CSV header auto detection implementation
        StringReader in = new StringReader(response.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

        List<LocationStats> newStats = new ArrayList<>();
        int sum = 0;
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            locationStat.setCountry(record.get("Country/Region"));
            locationStat.setState(record.get("Province/State"));
            // We need to fetch data from the last column
            int latestTotalCases = Integer.parseInt(record.get(record.size()-1));
            int prevDayCases = Integer.parseInt(record.get(record.size()-2));
            locationStat.setLatestTotalCases(latestTotalCases);
            locationStat.setDiffFromPrevDay(latestTotalCases - prevDayCases);
            sum += latestTotalCases;
            newStats.add(locationStat);
        }
        this.totalCases = sum;
        this.allStats = newStats;

    }

}
