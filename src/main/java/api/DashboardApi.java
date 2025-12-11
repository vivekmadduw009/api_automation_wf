package api;

import io.restassured.response.Response;
import org.slf4j.Logger;
import routes.TicketRoutes;
import utils.DatabaseUtil;
import utils.LoggerUtil;
import utils.RequestBuilder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class DashboardApi {

    private final ApiManager apiManager;

    public DashboardApi(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    private static final Logger logger = LoggerUtil.getLogger(DashboardApi.class);
    private Response dashboardSummaryResponse;
    private Response dashboardChartsResponse;

    public void getDashboardSummary() {
        logger.info("Fetching dashboard summary");
        dashboardSummaryResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .get(TicketRoutes.dashboardSummary());
        logger.debug("Get dashboard summary resp: {}", dashboardSummaryResponse.asPrettyString());
    }

    public void validateDashboardSummaryStatusCode(int expected) {
        if (dashboardSummaryResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = dashboardSummaryResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated dashboard summary status code: actual = {}, expected = {}", actual, expected);
    }

    public void validateDashboardChartsStatusCode(int expected) {
        if (dashboardChartsResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = dashboardChartsResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated dashboard charts status code: actual = {}, expected = {}", actual, expected);
    }

    private int getMetricCount(List<Map<String, Object>> metrics, String metricTitle) {
        return metrics.stream()
                .filter(m -> metricTitle.equalsIgnoreCase(String.valueOf(m.get("title"))))
                .map(m -> Integer.parseInt(String.valueOf(m.get("count"))))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Metric not found: " + metricTitle));
    }

    public void validateDashboardSummaryResponse() {
        if (dashboardSummaryResponse == null) {
            throw new IllegalStateException("Dashboard summary response is null. Fetch the summary first.");
        }

        Response ticketsResponse = apiManager.tickets().getTickets();
        logger.info("Validating dashboard summary response against tickets data");

        List<Map<String, Object>> metrics = dashboardSummaryResponse.jsonPath().getList("metrics");

        int openSummaryCount = getMetricCount(metrics, "Open tickets");
        int unassignedSummaryCount = getMetricCount(metrics, "Unassigned Tickets");
        int onHoldSummaryCount = getMetricCount(metrics, "Tickets On Hold");

        List<Map<String, Object>> tickets = ticketsResponse.jsonPath().getList("tickets");

        long openTicketsCount = tickets.stream()
                .filter(t -> {
                    String status = String.valueOf(t.get("status")).toLowerCase();
                    return status.equals("open") || status.equals("in_progress");
                }).count();

        long unassignedTicketsCount = tickets.stream()
                .filter(t -> {
                    Object a = t.get("assign_to");
                    return a == null || String.valueOf(a).trim().isEmpty();
                })
                .count();

        long onHoldTicketsCount = tickets.stream()
                .filter(t -> {
                    String status = String.valueOf(t.get("status")).toLowerCase();
                    return status.equals("on_hold");
                }).count();

        logger.info("Dashboard summary open tickets count: {}", openSummaryCount);
        logger.info("Tickets API open tickets count: {}", openTicketsCount);
        logger.info("Dashboard summary unassigned tickets count: {}", unassignedSummaryCount);
        logger.info("Tickets API unassigned tickets count: {}", unassignedTicketsCount);
        logger.info("Dashboard summary on-hold tickets count: {}", onHoldSummaryCount);
        logger.info("Tickets API on-hold tickets count: {}", onHoldTicketsCount);

        if (openSummaryCount != openTicketsCount) {
            throw new AssertionError("Mismatch in open tickets count: Dashboard summary has "
                    + openSummaryCount + " but Tickets API has " + openTicketsCount);
        }
        if (unassignedSummaryCount != unassignedTicketsCount) {
            throw new AssertionError("Mismatch in unassigned tickets count: Dashboard summary has "
                    + unassignedSummaryCount + " but Tickets API has " + unassignedTicketsCount);
        }
        if (onHoldSummaryCount != onHoldTicketsCount) {
            throw new AssertionError("Mismatch in on-hold tickets count: Dashboard summary has "
                    + onHoldSummaryCount + " but Tickets API has " + onHoldTicketsCount);
        }
        logger.info("Dashboard summary response is valid.");
    }

    public void getDashboardCharts() {
        dashboardChartsResponse = null;
        logger.info("Fetching dashboard charts");
        dashboardChartsResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .get( TicketRoutes.dashboardCharts());
        logger.debug("Get dashboard charts resp: {}", dashboardChartsResponse.asPrettyString());
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase().replace("_", "").replace(" ", "");
    }

    public void validateDashboardChartsResponse() {
        if (dashboardChartsResponse == null) {
            throw new IllegalStateException("Dashboard charts response is null. Fetch the charts first.");
        }

        Response ticketsResponse = apiManager.tickets().getTickets();
        List<Map<String, Object>> tickets = ticketsResponse.jsonPath().getList("tickets");
        List<Map<String, Object>> charts = dashboardChartsResponse.jsonPath().getList("charts");

        Map<String, Long> expectedPriorityCounts = tickets.stream()
                .collect(Collectors.groupingBy(
                        t -> normalize(String.valueOf(t.get("priority"))),
                        Collectors.counting()
                ));

        validatePieChart(charts, "priority", expectedPriorityCounts, "priority");

        Map<String, Long> expectedStatusCounts = tickets.stream()
                .collect(Collectors.groupingBy(
                        t -> normalize(String.valueOf(t.get("status"))),
                        Collectors.counting()
                ));

        validatePieChart(charts, "status", expectedStatusCounts, "status");

        Map<String, Long> expectedOpenCounts = tickets.stream()
                .collect(Collectors.groupingBy(
                        t -> normalize(String.valueOf(t.get("priority"))),
                        Collectors.counting()
                ));

        validatePieChart(charts, "open", expectedOpenCounts, "open");

        logger.info("Dashboard charts validation passed successfully.");
    }

    @SuppressWarnings("unchecked")
    private void validatePieChart(List<Map<String, Object>> charts,
                                  String chartId,
                                  Map<String, Long> expectedCounts,
                                  String fieldName) {

        logger.info("--------------------------------------------------");
        logger.info("Validating Chart: '{}'", chartId);
        logger.info("Expected counts computed from tickets API: {}", expectedCounts);
        logger.info("--------------------------------------------------");

        Map<String, Object> chart = charts.stream()
                .filter(c -> chartId.equalsIgnoreCase((String) c.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Chart not found: " + chartId));

        List<Map<String, Object>> chartData = (List<Map<String, Object>>) chart.get("data");

        logger.info("Chart '{}' data received from API: {}", chartId, chartData);

        for (Map.Entry<String, Long> entry : expectedCounts.entrySet()) {
            String key = entry.getKey();
            long expectedValue = entry.getValue();

            long actualValue = chartData.stream()
                    .filter(d -> key.equalsIgnoreCase(normalize(String.valueOf(d.get("label"))).toLowerCase()))
                    .map(d -> Long.parseLong(String.valueOf(d.get("value"))))
                    .findFirst()
                    .orElse(0L);

            logger.info("Validating Label '{}' | Expected = {} | Actual = {}", key, expectedValue, actualValue);

            if (expectedValue != actualValue) {
                logger.error("MISMATCH in chart '{}' for {} = {} | expected {} but found {}",
                        chartId, fieldName, key, expectedValue, actualValue);

                throw new AssertionError("Mismatch in chart '" + chartId +
                        "' for " + fieldName + " = " + key +
                        " | expected: " + expectedValue +
                        " but found: " + actualValue);
            }

            logger.info("Label '{}' validated successfully for chart '{}'", key, chartId);
        }

        logger.info("--------------------------------------------------");
        logger.info("Chart '{}' validated SUCCESSFULLY", chartId);
        logger.info("--------------------------------------------------");
    }

    public void fetchTicketDataFromDatabase() {
        logger.info("Fetching ticket data from database");

        String sql = "SELECT * FROM tickets LIMIT 1";
        try (ResultSet rs = DatabaseUtil.executeQuery(sql)) {
            if (rs == null) {
                throw new RuntimeException("Failed to fetch ticket data: ResultSet is null");
            }

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (rs.next()) {
                StringBuilder rowData = new StringBuilder("Fetched row: {");

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    if (i > 1) {
                        rowData.append(columnName).append(" = ").append(columnValue).append(" || ");
                    }
                }
                rowData.append("}");

                logger.info("Ticket data Fetched successfully");
                logger.info("Result content :{}", rowData);
            } else {
                logger.warn("ResultSet is empty, no ticket data found");
            }
        } catch (SQLException s) {
            logger.error("SQL Exception while fetching ticket data: {}", s.getMessage(), s);
            throw new RuntimeException("SQL Exception: " + s.getMessage(), s);
        }
    }

    public void validateTicketDataRetrieval() {
        logger.info("Validating ticket data retrieval from database");
        try {
            ResultSet rs = DatabaseUtil.executeQuery(
                    "SELECT * FROM tickets LIMIT 1"
            );
            if (rs.next()) {
                logger.info("Ticket data retrieved successfully: Ticket ID = {}", rs.getString("ticket_id"));
            } else {
                throw new AssertionError("No ticket data found in the database.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate ticket data retrieval: " + e.getMessage(), e);
        }
    }
}
