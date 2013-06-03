package de.bananaco.report.report;

import org.bukkit.Location;

/**
 * This class stores the relevant data for reports.
 *
 * .hashCode() and .toString() are custom
 */
public class Report {

    private final String reporter;
    private final String report;
    private final String id;
    private final Location location;
    private boolean resolved;

    protected Report(String reporter, String report, String id, Location location) {
        this(reporter, report, false, id, location);
    }

    protected Report(String reporter, String report, boolean resolved, String id, Location location) {
        this.reporter = reporter;
        this.report = report;
        this.id = id;
        this.resolved = resolved;
        this.location = location;
    }

    /**
     * Gets the person who created the report's name
     *
     * @return String
     */
    public String getReporter() {
        return reporter;
    }

    /**
     * Gets the message of the report
     *
     * @return String
     */
    public String getReport() {
        return report;
    }

    /**
     * Gets the id of the report
     *
     * @return String
     */
    public String getID() {
        return id;
    }

    /**
     * Returns the location (as a String) of where the report was made
     *
     * @return String (Location)
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Use to mark a report as resolved or unresolved manually
     *
     * @param resolved
     */
    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    /**
     * Gets the resolved state of a report
     *
     * @return resolved
     */
    public boolean getResolved() {
        return resolved;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Report other = (Report) obj;
        if ((this.reporter == null) ? (other.getReporter() != null) : !this.reporter.equals(other.getReporter())) {
            return false;
        }
        if ((this.report == null) ? (other.getReport() != null) : !this.report.equals(other.getReport())) {
            return false;
        }
        if ((this.id == null) ? (other.getID() != null) : !this.id.equals(other.getID())) {
            return false;
        }
        if (this.location != other.getLocation() && (this.location == null || !this.location.equals(other.getLocation()))) {
            return false;
        }
        if (this.resolved != other.getResolved()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return reporter + ":" + report;
    }
}
