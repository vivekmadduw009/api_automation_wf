package hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import org.slf4j.Logger;
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
}