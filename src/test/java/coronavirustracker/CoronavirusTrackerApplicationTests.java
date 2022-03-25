package coronavirustracker;

import coronavirustracker.services.CoronaVirusDataService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class CoronavirusTrackerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testDataService() throws IOException, InterruptedException {
		CoronaVirusDataService covidData = new CoronaVirusDataService();
		covidData.fetchVirusData();
	}

}
