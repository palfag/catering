package businesslogic.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Preparation extends Procedure{
	private static Map<Integer, Preparation> all = new HashMap<>();
	private String name;
	private int id;

	public Preparation(String name) {
		id = 0;
		this.name = name;
	}

	//carico tutte le preparazioni con lo stesso sistema usato dalle prof, un'hashmap andrà a salvare le preparazioni in base all'id
	public static ObservableList<Preparation> loadAllPreparations() {
		String query = "SELECT * FROM preparations";
		PersistenceManager.executeQuery(query, new ResultHandler() {
			@Override
			public void handle(ResultSet rs) throws SQLException {
				int id = rs.getInt("id");
				if (all.containsKey(id)) {
					Preparation rec = all.get(id);
					rec.name = rs.getString("name");
				} else {
					Preparation prep = new Preparation(rs.getString("name"));
					prep.id = id;
					all.put(prep.id, prep);
				}
			}
		});
		ObservableList<Preparation> prepList =  FXCollections.observableArrayList(all.values());
		Collections.sort(prepList, new Comparator<Preparation>() {
			@Override
			public int compare(Preparation o1, Preparation o2) {
				return (o1.getName().compareTo(o2.getName()));
			}
		});
		return prepList;
	}

    public static ObservableList<Preparation> getAllPreparations() {
		return FXCollections.observableArrayList(all.values());

	}

    @Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public int getId() {
		return id;
	}
}
