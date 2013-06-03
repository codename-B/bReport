package de.bananaco.report.msg;

import java.util.Locale;

/**
 *
 * @author Brandon Barker
 */
public enum Msg {

    USAGE("&4Usage: %s"),
    INGAMECOMMAND("&4Only players can use the %s command!"),
    PERMISSION("&4You do not have permission to use %s"),
    REPORT_LENGTH("&4Reports must be at least %s words long."),
    TICKET_CLONE("&4You already have a ticket about that."),
    REPORT_FILED_YOU("&7A report has been filed for you - ID:&b %s"),
    REPORT_FILED("&7A report has been filed by&b %s &7 - ID:&b %s"),
    REPORT_READ("&b %s &7 - ID:&b %s"),
    SHOWING_REPORT("&7Showing&b %s" + "/" +"%s &7unread reports"),
    NO_REPORTS("&4** NOTHING TO REPORT **"),
    UNSOLVED_REPORTS("&4No report with that id, use %s to see all unresolved reports."),
    READ_REPORTER("&7Reporter:&b %s &7ID:&b %s"),
    READ_STATUS("&7Statues:&b %s"),
    READ_REPORT("&7** %s"),
    REPORT_WAS_RESOLVED("&4That report is already resolved!"),
    REPORT_IS_RESOLVED("&7Report Resolved"),
    REPORT_RESOLVED("&7A report you filed has been resolved by &b%s"),
    REPORT_IS_UNRESOLVED("&4That report is not yet resolved!"),
    REPORT_UNRESOLVED("&7Report unresolved."),
    REPORT_TOTAL("&7There are&b %s&7 unread reports"),
    ON_JOIN("&7There are&b %s&7 unread reports"),
    REPORT_TELEPORT("&7Teleported to the location of the report - ID:&b %s");
    

    private Msg() {
    }

    private String message;

    Msg(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.toString().toUpperCase(Locale.getDefault());
    }

    /**
     *
     * @return
     */
    public String getMsg() {
        return this.message;
    }
}
