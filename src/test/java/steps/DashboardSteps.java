package steps;

import api.ApiManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DashboardSteps {

    private final ApiManager api = new ApiManager();

    @When("I fetch the dashboard summary")
    public void getDashboardSummary() {
        api.dashboard().getDashboardSummary();
    }

    @Then("the dashboard summary API response status code should be {int}")
    public void validateDashboardSummaryStatusCode(int expectedStatusCode) {
        api.dashboard().validateDashboardSummaryStatusCode(expectedStatusCode);
    }

    @Then("the dashboard charts API response status code should be {int}")
    public void validateDashboardChartsStatusCode(int expectedStatusCode) {
        api.dashboard().validateDashboardChartsStatusCode(expectedStatusCode);
    }

    @Then("verify the dashboard summary response is valid from the tickets data")
    public void validateDashboardSummary() {
        api.dashboard().validateDashboardSummaryResponse();
    }

    @When("I fetch the dashboard charts")
    public void getDashboardCharts() {
        api.dashboard().getDashboardCharts();
    }

    @Then("verify the dashboard charts response is valid from the tickets data")
    public void validateDashboardCharts() {
        api.dashboard().validateDashboardChartsResponse();
    }

    @When("I execute a query to fetch ticket data")
    public void fetchTicketData() {
        api.dashboard().fetchTicketDataFromDatabase();
    }

    @Then("the ticket data should be retrieved successfully")
    public void validateTicketDataRetrieval() {
        api.dashboard().validateTicketDataRetrieval();
    }
}
