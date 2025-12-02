package api;

public class ApiManager {


    private final TicketsApi ticketsApi;
    private final DashboardApi dashboardApi;

    public ApiManager() {
        this.ticketsApi = new TicketsApi(this);
        this.dashboardApi = new DashboardApi(this);
    }

    public TicketsApi tickets() {
        return ticketsApi;
    }

    public DashboardApi dashboard() {
        return dashboardApi;
    }
}