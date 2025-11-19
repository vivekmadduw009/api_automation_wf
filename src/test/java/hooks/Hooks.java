package hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import utils.ConfigReader;
import org.slf4j.Logger;
import utils.LoggerUtil;

public class Hooks {

    private static final Logger logger = LoggerUtil.getLogger(Hooks.class);

    @Before
    public void beforeScenario() {
        logger.info("===== Scenario Started =====");
        ConfigReader.loadProperties();
    }

    @After
    public void afterScenario() {
        logger.info("===== Scenario Finished =====");
    }
}