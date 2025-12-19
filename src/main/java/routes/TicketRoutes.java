package routes;

public class TicketRoutes {

    public static String getTickets(){
        return "/api/version1/tickets";
    }

    public static String getTicketById(String ticketId){
        return "/api/version1/tickets/" + ticketId;
    }

    public static String createTicket(){
        return "/api/version1/tickets";
    }

    public static String updateTicket(String ticketId){
        return "/api/version1/tickets/" + ticketId;
    }

    public static String deleteTicket(String ticketId){
        return "/api/version1/tickets/" + ticketId;
    }

    public static String dashboardSummary(){
        return "/api/version1/dashboard/summary";
    }

    public static String dashboardCharts(){
        return "/api/version1/dashboard/charts";
    }

    public static String addAttachment(String ticketId){
        return "/api/version1/tickets/"+ ticketId +"/attachment";
    }

    public static String getAttachment(String ticketId){
        return "/api/version1/tickets/"+ ticketId +"/attachment";
    }

    public static String deleteAttachment(String ticketId){
        return "/api/version1/tickets/"+ ticketId +"/attachment/";
    }

    public static String addComment(String ticketId){
        return "/api/version1/tickets/"+ ticketId +"/comments";
    }

    public static String getComments(String ticketId){
        return "/api/version1/tickets/"+ ticketId +"/comments";
    }

    public static String deleteComment(String ticketId){
        return "/api/version1/tickets/"+ ticketId +"/comments/";
    }

    public static String addWatchers(String ticketId){
        return "/api/version1/tickets/"+ ticketId +"/watch";
    }

    public static String deleteWatchers(String ticketId){
        return "/api/version1/tickets/"+ ticketId +"/watch";
    }

    public static String getNotifications(){
        return "/api/version1/notifications";
    }

    public static String markReadNotification()
    {
        return "/api/version1/notifications/mark_read";
    }
}