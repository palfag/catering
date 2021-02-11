package businesslogic.workShift;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Calendarr {
    private ObservableList<WorkShift> workshifts;
    private Calendarr() {
        workshifts = WorkShift.loadAllWorkShifts();
    }

    public ObservableList<WorkShift> getWorkshifts() {
        //return workshifts;//non funziona
        return WorkShift.getAllWorkShifts();
    }
    private static Calendarr singleInstance;

    public static Calendarr getInstance() {
        if (singleInstance == null) {
            singleInstance = new Calendarr();
        }
        return singleInstance;
    }

    public ObservableList<WorkShift> getKitchenShifts() {
        ObservableList<WorkShift> all = getWorkshifts();
        ObservableList<WorkShift> kitchenSHift = FXCollections.observableArrayList();
        for (WorkShift s: all) {//voglio selezionare solo i turni di cucina
            if (s.getType()==WorkshiftType.kitchenShift)
                kitchenSHift.add(s);
        }
        return kitchenSHift;
    }
}
