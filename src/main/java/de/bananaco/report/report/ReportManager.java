package de.bananaco.report.report;

import de.bananaco.report.ReportPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Use ReportManager.getInstance() rather than creating a new instance
 */
public class ReportManager {

    private static final Comparator<Report> comparReport = new Comparator<Report>() {
        public int compare(Report o0, Report o1) {
            int i0 = Integer.parseInt(o0.getID());
            int i1 = Integer.parseInt(o1.getID());
            if (i0 > i1) {
                return -1;
            }
            if (i0 < i1) {
                return 1;
            } else {
                return 0;
            }
        }
    };
    private ReportPlugin rp;
    private Map<String, Report> reports = new HashMap<String, Report>();
    private File file = new File("plugins/bReport/reports.yml");
    private final YamlConfiguration config = new YamlConfiguration();

    public ReportManager(ReportPlugin rp) {
        this.rp = rp;
    }

    /**
     * Intended to be used to create a report directly from onCommand
     *
     * @param reporter
     * @param report
     * @param location
     * @return Report created
     */
    public Report createReport(String reporter, String[] report, Location location) {
        String id = String.valueOf(reports.size());
        StringBuilder sb = new StringBuilder();
        for (String r : report) {
            sb.append(r).append(" ");
        }
        reports.put(id, new Report(reporter, sb.toString(), id, location));
        return reports.get(id);
    }

    /**
     * For use externally printing an args[]
     *
     * @param report
     * @return String
     */
    public String getString(String[] report) {
        StringBuilder sb = new StringBuilder();
        for (String r : report) {
            sb.append(r).append(" ");
        }
        return sb.toString();
    }

    /**
     * For use externally printing an args[]
     *
     * @param report
     * @param skip 
     * @return String
     */
    public String getString(String[] report, Integer... skip) {
        StringBuilder sb = new StringBuilder();
        List<Integer> skipList = new ArrayList<Integer>();
        skipList.addAll(Arrays.asList(skip));
        Integer count = 0;
        for (String r : report) {
            if (!(skipList.contains(count))) {
                sb.append(r).append(" ");
            }
            count++;
        }
        return sb.toString();
    }

    /**
     * Returns a List<Report> of unresolved reports
     *
     * @return List<Report> unresolved
     */
    public List<Report> getUnresolvedReports() {
        List<Report> rList = new ArrayList<Report>();
        for (String report : reports.keySet()) {
            Report r = reports.get(report);
            if (!r.getResolved()) {
                rList.add(r);
            }
        }
        // Sort the reports!
        Collections.sort(rList, comparReport);
        return rList;
    }

    /**
     * Gets a report Report by its id
     *
     * @param id
     * @return Report
     */
    public Report getReport(String id) {
        return reports.get(id);
    }

    /**
     * Loads the reports.yml
     */
    public void load() {
        try {
            file = rp.getDirectory("reports.yml");
            fileCheck();
            config.load(file);
            Set<String> keys = config.getKeys(false);
            if (keys != null && keys.size() > 0) {
                // Reference
                String reporter;
                String report;
                boolean resolved;
                String id;
                Location location;
                // And load
                for (String key : keys) {
                    reporter = config.getString(key + ".reporter");
                    report = config.getString(key + ".report");
                    resolved = config.getBoolean(key + ".resolved");
                    location = getLocation(config.getString(key + ".location", "world,0,70,0.0.0"));
                    id = key;
                    Report r = new Report(reporter, report, resolved, id, location);
                    for (String s : config.getStringList(key + ".comments")) {
                        r.getComments().add(s);
                    }
                    reports.put(id, r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the reports.yml
     */
    public void save() {
        Set<String> keys = reports.keySet();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                Report report = reports.get(key);
                config.set(report.getID() + ".reporter", report.getReporter());
                config.set(report.getID() + ".report", report.getReport());
                config.set(report.getID() + ".resolved", report.getResolved());
                config.set(report.getID() + ".location", getLocation(report.getLocation()));
                config.set(report.getID() + ".comments", report.getComments().toArray());
            }
        }
        try {
            // Actually save the edited config
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Internal method, creates the file if it doesn't exist
     *
     * @return
     * @throws Exception
     */
    protected boolean fileCheck() throws Exception {
        boolean r = true;
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            r = file.createNewFile();
        }
        return r;
    }

    /**
     * Deserialises a Location
     *
     * @param input
     * @return Location
     */
    protected Location getLocation(String input) {
        String[] data = input.split(",");
        World world = Bukkit.getWorld(data[0]);
        int x = Integer.parseInt(data[1]);
        int y = Integer.parseInt(data[2]);
        int z = Integer.parseInt(data[3]);
        Location loc = new Location(world, x, y, z);
        loc.setYaw(Float.valueOf(data[4]));
        loc.setPitch(Float.valueOf(data[5]));
        return loc;
    }

    /**
     * Serialises a Location
     *
     * @param input
     * @return String
     */
    protected String getLocation(Location input) {
        return input.getWorld().getName() + "," + input.getBlockX() + "," + input.getBlockY() + "," + input.getBlockZ() + "," + input.getYaw() + "," + input.getPitch();
    }
}
