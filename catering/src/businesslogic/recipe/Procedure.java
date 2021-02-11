package businesslogic.recipe;

import javafx.collections.ObservableMap;

public class Procedure {
	private String name,description,note;
	private ObservableMap<String, Boolean> tagMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public ObservableMap<String, Boolean> getTagMap() {
		return tagMap;
	}

	public void setTagMap(ObservableMap<String, Boolean> tagMap) {
		this.tagMap = tagMap;
	}

	@Override
	public String toString() {
		return name;
	}
}
