package businesslogic.workShift;

import businesslogic.kitchenTask.Task;
import businesslogic.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static businesslogic.workShift.WorkshiftType.kitchenShift;
import static businesslogic.workShift.WorkshiftType.serviceShift;

public class WorkShift {
	private static Map<Integer, WorkShift> all = new HashMap<>();
	private WorkshiftType type;
	private Date date;
	private Time start,end;
	private int id;
	private boolean complete;

	public WorkShift(WorkshiftType type, Date date, Time start, Time end) {
		this.type = type;
		this.date = date;
		this.start = start;
		this.end = end;
	}



	public boolean isComplete() {
		return complete;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getStart() {
		return start;
	}

	public void setStart(Time start) {
		this.start = start;
	}

	public Time getEnd() {
		return end;
	}

	public void setEnd(Time end) {
		this.end = end;
	}

	public WorkshiftType getType() {
		return type;
	}

	public void setType(WorkshiftType type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public static ObservableList<WorkShift> loadAllWorkShifts() {
		String query = "SELECT * FROM Workshifts";
		PersistenceManager.executeQuery(query, new ResultHandler() {
			@Override
			public void handle(ResultSet rs) throws SQLException {
//				System.out.println(rs.getTime("start"));
				int id = rs.getInt("id");
				if (all.containsKey(id)) {

					WorkShift shift = all.get(id);

					String s = rs.getString("type");
					if(s.equals(kitchenShift.toString()))
						shift.type = kitchenShift;
					else
						shift.type = serviceShift;
					shift.date = rs.getDate("date");
					shift.start = normalizeTime(rs.getTime("start"));
					shift.end = normalizeTime(rs.getTime("end"));
					shift.complete = rs.getBoolean("completo");
				} else {
					WorkShift shift;
					String s = rs.getString("type");

					if(s.equals(kitchenShift.toString())) {
						shift = new WorkShift(kitchenShift,rs.getDate("date"), normalizeTime(rs.getTime("start")), normalizeTime(rs.getTime("end")));
						shift.id = id;
						all.put(shift.id, shift);
					}
					if(s.equals(serviceShift.toString())){
						shift = new WorkShift(serviceShift,rs.getDate("date"), normalizeTime(rs.getTime("start")), normalizeTime(rs.getTime("end")));
						shift.id = id;
						all.put(shift.id, shift);
					}


				}
			}
		});

		ObservableList<WorkShift> ws =  FXCollections.observableArrayList(all.values());
		Collections.sort(ws, new Comparator<WorkShift>() {
			@Override
			public int compare(WorkShift s1, WorkShift s2) {
				return (s1.getDate().compareTo(s2.getDate()));
			}
		});
		return ws;
	}

//	public static ObservableList<WorkShift> getAllWorkShifts() {
//		return FXCollections.observableArrayList(all.values());
//	}
	public static ObservableList<WorkShift> getAllWorkShifts() {
		String query = "SELECT * FROM Workshifts";
		ObservableList<WorkShift> workShifts = FXCollections.observableArrayList();
		PersistenceManager.executeQuery(query, new ResultHandler() {
			@Override
			public void handle(ResultSet rs) throws SQLException {
				String s = rs.getString("type");
				WorkshiftType type;
				if(s.equals(kitchenShift.toString()))
					type = kitchenShift;
				else
					type = serviceShift;
				Date date = rs.getDate("date");
				Time start = normalizeTime(rs.getTime("start"));
				Time end = normalizeTime(rs.getTime("end"));
				WorkShift shift = new WorkShift(type, date, start, end);
				shift.complete = rs.getBoolean("completo");
				shift.id = rs.getInt("id");
				workShifts.add(shift);
			}
		});
		return workShifts;
	}
	@Override
	public String toString() {
		return "Il " + date +
				" da: " + String.format("%02d", start.getHours())+":"+String.format("%02d", start.getMinutes()) +
				" a: " + String.format("%02d", end.getHours())+":"+String.format("%02d", end.getMinutes());
	}

	public boolean isPast() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		java.util.Date date = new java.util.Date();
		return this.date.before(date) || this.date.getDate()==date.getDate();
	}

	private static Time normalizeTime(Time t1)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(t1);
		cal.add(Calendar.HOUR, -1);
		return new Time(cal.getTime().getTime());
	}

    public boolean isAvailable(User cook, WorkShift kitchenShift) {
		String select = "SELECT * FROM catering.Availability WHERE user_id="+cook.getId()+" AND workshift_id=" + kitchenShift.getId();
		final boolean[] res = new boolean[1];
		PersistenceManager.executeQuery(select, new ResultHandler() {
			@Override
			public void handle(ResultSet rs) throws SQLException {
				int size =0;
				if (rs != null)
				{
					rs.last();    // moves cursor to the last row
					size = rs.getRow(); // get row id
				}
				res[0] = size!=0;
			}
		});
		return res[0];
    }



	public ObservableList<User> getAvailability() {
		String select = "SELECT * FROM catering.Users, catering.Availability WHERE users.id=availability.user_id AND workshift_id=" + this.getId();
		ObservableList<User> users = FXCollections.observableArrayList();
		PersistenceManager.executeQuery(select, new ResultHandler() {
			@Override
			public void handle(ResultSet rs) throws SQLException {
				do {
					User u = new User();
					u.setId(rs.getInt("id"));
					u.setUsername(rs.getString("username"));
					users.add(u);
				}while (rs.next());
			}
		});
		return users;
	}


	public void setComplete(boolean b) {
		this.complete= b;
	}

	public static void updateShift(WorkShift selectedItem) {
		String upd = "UPDATE catering.workshifts SET completo = "+selectedItem.isComplete()+" WHERE id="+selectedItem.getId();

		PersistenceManager.executeUpdate(upd);
	}
}
