package hooks;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import org.slf4j.Logger;
import utils.ConfigReader;
import utils.DatabaseUtil;
import utils.LoggerUtil;

public class Hooks {

    private static final Logger logger = LoggerUtil.getLogger(Hooks.class);

    @Before
    public void beforeScenario() {
        logger.info("===== Scenario Started =====");
    }

    @After
    public void afterScenario() {
        logger.info("===== Scenario Finished =====");
    }

    @BeforeAll
    public static void beforeAll() {
        logger.info("===== Test Suite Started =====");
        ConfigReader.loadProperties();

        DatabaseUtil.connect(
                ConfigReader.get("db.host"),
                Integer.parseInt(ConfigReader.get("db.port")),
                ConfigReader.get("db.name"),
                ConfigReader.get("db.user"),
                ConfigReader.get("db.password")
        );
    }

    @AfterAll
    public static void afterAll() {
        DatabaseUtil.close();
        logger.info("===== Test Suite Finished =====");
    }
}