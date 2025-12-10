package api;

public class ApiManager {


    private final TicketsApi ticketsApi;
    private final DashboardApi dashboardApi;
    private final AttachmentsApi attachmentsApi;
    private final CommentsApi commentsApi;

    public ApiManager() {
        this.ticketsApi = new TicketsApi(this);
        this.dashboardApi = new DashboardApi(this);
        this.attachmentsApi = new AttachmentsApi(this);
        this.commentsApi = new CommentsApi(this);
    }

    public TicketsApi tickets() {
        return ticketsApi;
    }

    public DashboardApi dashboard() {
        return dashboardApi;
    }

    public AttachmentsApi attachments() {
        return attachmentsApi;
    }

    public CommentsApi comments() {
        return commentsApi;
    }
}