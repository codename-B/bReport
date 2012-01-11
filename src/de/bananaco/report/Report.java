package de.bananaco.report;
/**
 * This class stores the relevant data
 * for reports.
 * 
 * .hashCode() and .toString() are custom
 */
public class Report {
	
	private final String reporter;
	private final String report;
	private final String id;
	private final String location;
	
	private boolean resolved;
	
	protected Report(String reporter, String report, String id, String location) {
		this(reporter, report, false, id, location);
	}
	
	protected Report(String reporter, String report, boolean resolved, String id, String location) {
	this.reporter = reporter;
	this.report = report;
	this.id = id;
	this.resolved = resolved;
	this.location = location;
	}
	
	/**
	 * Gets the person who created the report's name
	 * @return String
	 */
	public String getReporter() {
		return reporter;
	}
	
	/**
	 * Gets the message of the report
	 * @return String
	 */
	public String getReport() {
		return report;
	}
	
	/**
	 * Gets the id of the report
	 * @return String
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Returns the location (as a String) of where the report was made
	 * @return String (Location)
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * Use to mark a report as resolved or unresolved manually
	 * @param resolved
	 */
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
	
	/**
	 * Gets the resolved state of a report
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
	public String toString() {
		return reporter+":"+report;
	}

}
