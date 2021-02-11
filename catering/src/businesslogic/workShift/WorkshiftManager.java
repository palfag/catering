package businesslogic.workShift;

import persistence.WorkShiftPersistence;

import java.util.ArrayList;

public class WorkshiftManager {
    private ArrayList<WorkshiftEventReceiver> eventReceivers;
    private Calendarr calendarr;
    public WorkshiftManager(){
        eventReceivers = new ArrayList<>();
        calendarr = Calendarr.getInstance();
    }

    public Calendarr getCalendarr() {
        return calendarr;
    }

//    public ObservableList<WorkShift> checkBillboard() {
//        return FXCollections.unmodifiableObservableList(WorkShift.getAllWorkShifts());
//    }

    public void setComplete(WorkShift selectedItem) {
        selectedItem.setComplete(!selectedItem.isComplete());
        notifyShiftCompleted(selectedItem);
    }

    private void notifyShiftCompleted(WorkShift selectedItem) {
        for (WorkshiftEventReceiver receiver : eventReceivers)
            receiver.updateShiftCompleted(selectedItem);
    }

    public void addEventReceiver(WorkShiftPersistence workShiftPersistence) {
        this.eventReceivers.add(workShiftPersistence);
    }
}
