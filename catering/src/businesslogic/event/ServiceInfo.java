package businesslogic.event;

import businesslogic.menu.Menu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

public class ServiceInfo implements EventItemInfo {
    private int id;
    private String name;
    private Date date;
    private Time timeStart;
    private Time timeEnd;
    private int participants;
    private Menu menu;

    public ServiceInfo(String name) {
        this.name = name;
    }


    public String toString() {
        return name + ":\n\t" +
                date + "\n\t"+
                "inizio: "+String.format("%02d", timeStart.getHours())+":"+String.format("%02d", timeStart.getMinutes())+"\n\t"
                + "fine: " + String.format("%02d", timeEnd.getHours())+":"+String.format("%02d", timeEnd.getMinutes()) +"\n\t"
                +participants + " persone";
    }

    // STATIC METHODS FOR PERSISTENCE

    public static ObservableList<ServiceInfo> loadServiceInfoForEvent(int event_id) {
        ObservableList<ServiceInfo> result = FXCollections.observableArrayList();
        String query = "SELECT id, name, service_date, time_start, time_end, expected_participants, approved_menu_id " +
                "FROM Services WHERE event_id = " + event_id + " AND approved_menu_id != 0";
        PersistenceManager.executeQuery(query, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String s = rs.getString("name");
                ServiceInfo serv = new ServiceInfo(s);
                serv.id = rs.getInt("id");
                serv.date = rs.getDate("service_date");
                serv.timeStart = normalizeTime(rs.getTime("time_start"));
                serv.timeEnd = normalizeTime(rs.getTime("time_end"));
                serv.participants = rs.getInt("expected_participants");
                int menu = rs.getInt("approved_menu_id");
                serv.menu = Menu.loadMenuById(menu);
                result.add(serv);
            }
        });

        return result;
    }
    private static Time normalizeTime(Time t1)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(t1);
        cal.add(Calendar.HOUR, -1);
        return new Time(cal.getTime().getTime());
    }

	public Menu getMenu() {
        return menu;
	}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
