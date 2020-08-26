package me.reykench.database;

import me.reykench.BungeeUtils;
import me.reykench.Text;
import me.reykench.commands.BugReport;
import me.reykench.commands.Mail;
import me.reykench.commands.Report;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class SQLManager {

    private final ConnectionPoolManager pool;

    public static final String mailDataTable = "mail_data";
    public static final String reportDataTable = "report_data";
    public static final String bugDataTable = "bug_data";

    public SQLManager(ConnectionPoolManager pool) {
        this.pool = pool;

        makeTable(mailDataTable, reportDataTable, bugDataTable);
    }

    private String getStatement(String table) {

        if (table.equals(mailDataTable))
            return "CREATE TABLE IF NOT EXISTS `" + mailDataTable + "` (uuid TEXT, username TEXT, mail TEXT)";

        if (table.equals(bugDataTable))
            return "CREATE TABLE IF NOT EXISTS `" + bugDataTable + "` (id INT, reporter TEXT, reporterName TEXT, bug TEXT)";

        if (table.equals(reportDataTable))
            return "CREATE TABLE IF NOT EXISTS `" + reportDataTable + "` (id INT, reporter TEXT, reporterName TEXT, reported TEXT, reportedName TEXT, reason TEXT)";

        return "CREATE TABLE IF NOT EXISTS " + table;
    }

    private void makeTable(String... tables) {

        try {

            for (String table : tables) {
                String statement = getStatement(table);

                Connection x = pool.getConnection();
                PreparedStatement y = x.prepareStatement(statement);
                y.executeUpdate();

                pool.close(x, y, null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void onDisable() {
        pool.closePool();
    }

    public boolean playerExists(String table, UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String stmt = "SELECT * FROM " + table + " WHERE uuid=?";
            ps = conn.prepareStatement(stmt);

            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();

            if(rs.next()) return true;
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }

        return false;
    }

    private void createPlayerMail(UUID uuid, String name) {
        if (playerExists(mailDataTable, uuid)) return;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String stmt = "INSERT INTO " + mailDataTable + "(uuid,username,mail) VALUE(?,?,?)";
            ps = conn.prepareStatement(stmt);
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setString(3, new JSONArray().toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public ArrayList<Mail.MailElement> getMail(ProxiedPlayer player) {
        createPlayerMail(player.getUniqueId(), player.getName());

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String stmt = "SELECT * FROM " + mailDataTable + " WHERE uuid=?";
            ps = conn.prepareStatement(stmt);

            ps.setString(1, player.getUniqueId().toString());
            rs = ps.executeQuery();

            while(rs.next()) {

                if(rs.getString("mail") == null) return new ArrayList<>();

                JSONArray array = new JSONArray(rs.getString("mail"));

                ArrayList<Mail.MailElement> mail = new ArrayList<>();
                for(int i = 0; i < array.length(); i++) {
                    JSONObject input = array.getJSONObject(i);

                    mail.add(new Mail.MailElement(
                            UUID.fromString(input.getString("sender")),
                            input.getString("text"),
                            input.getInt("read")
                    ));
                }

                return mail;
            }

        } catch (NullPointerException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }

        return new ArrayList<>();
    }

    public void checkUnreadMails(ProxiedPlayer pp) {
        int unreadCount = 0;
        for(Mail.MailElement mail : getMail(pp)) {
            if(mail.wasRead() == 0) {
                unreadCount++;
            }
        }
        if(unreadCount != 0)
            Text.sendMessage(pp, Text.MAIL_PREFIX + "You have " + unreadCount + " unread mails in your mailbox!");
    }

    public void deleteMail(ProxiedPlayer player) {
        setMail(player, new ArrayList<>());
    }

    public void sendMail(ProxiedPlayer sender, ProxiedPlayer player, String text) {
        ArrayList<Mail.MailElement> mail = getMail(player);
        mail.add(new Mail.MailElement(sender.getUniqueId(), text, 0));
        setMail(player, mail);
    }

    public void setMail(ProxiedPlayer player, ArrayList<Mail.MailElement> mail) {
        createPlayerMail(player.getUniqueId(), player.getName());

        JSONArray toUpdate = new JSONArray();
        for (Mail.MailElement me : mail) {
            toUpdate.put(new JSONObject().put("sender", me.getSender()).put("text", me.getText()).put("read", me.wasRead()));
        }

        setPlayerData(mailDataTable, player.getUniqueId(), "mail", toUpdate.toString());
    }

    public ArrayList<BugReport.BugReportElement> getBugReports() {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String stmt = "SELECT * FROM " + bugDataTable;
            ps = conn.prepareStatement(stmt);
            rs = ps.executeQuery();

            ArrayList<BugReport.BugReportElement> bugreports = new ArrayList<>();

            while(rs.next()) {

                if(rs.getString("bug") == null) return new ArrayList<>();

                bugreports.add(new BugReport.BugReportElement(rs.getInt("id"), UUID.fromString(rs.getString("reporter")), rs.getString("reporterName"), rs.getString("bug")));

            }

            return bugreports;

        } catch (NullPointerException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }

        return new ArrayList<>();
    }

    public void deleteBug(int id) {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String stmt = "DELETE FROM " + bugDataTable + " WHERE id=?";
            ps = conn.prepareStatement(stmt);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public void createBug(BugReport.BugReportElement bug) {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String stmt = "INSERT INTO " + bugDataTable + "(id,reporter,reporterName,bug) VALUE(?,?,?,?)";
            ps = conn.prepareStatement(stmt);
            ps.setInt(1, bug.getId());
            ps.setString(2, bug.getPlayer().toString());
            ps.setString(3, bug.getPlayerName());
            ps.setString(4, bug.getBug());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public ArrayList<Report.PlayerReportElement> getReports() {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String stmt = "SELECT * FROM " + reportDataTable;
            ps = conn.prepareStatement(stmt);
            rs = ps.executeQuery();

            ArrayList<Report.PlayerReportElement> reports = new ArrayList<>();

            while(rs.next()) {

                if(rs.getString("reason") == null) return new ArrayList<>();

                reports.add(new Report.PlayerReportElement(rs.getInt("id"), UUID.fromString(rs.getString("reporter")), rs.getString("reporterName"),
                        UUID.fromString(rs.getString("reported")), rs.getString("reportedName"), rs.getString("reason")));

            }

            return reports;

        } catch (NullPointerException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }

        return new ArrayList<>();
    }

    public void deleteReport(int id) {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String stmt = "DELETE FROM " + reportDataTable + " WHERE id=?";
            ps = conn.prepareStatement(stmt);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public void createReport(Report.PlayerReportElement report) {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String stmt = "INSERT INTO " + reportDataTable + "(id,reporter,reporterName,reported,reportedName,reason) VALUE(?,?,?,?,?,?)";
            ps = conn.prepareStatement(stmt);
            ps.setInt(1, report.getId());
            ps.setString(2, report.getReporter().toString());
            ps.setString(3, report.getReporterName());
            ps.setString(4, report.getReportedPlayer().toString());
            ps.setString(5, report.getReportedName());
            ps.setString(6, report.getReason());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, rs);
        }
    }

    public void setPlayerData(String table, UUID uuid, String column, Object data) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = pool.getConnection();

            String stmt = "UPDATE " + table + " SET " + column + "=?  WHERE uuid=?";
            ps = conn.prepareStatement(stmt);

            ps.setObject(1, data);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

}
