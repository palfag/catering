package persistence;


import businesslogic.workShift.WorkShift;
import businesslogic.workShift.WorkshiftEventReceiver;

public class WorkShiftPersistence implements WorkshiftEventReceiver {
    @Override
    public void updateShiftCompleted(WorkShift selectedItem) {
        WorkShift.updateShift(selectedItem);
    }
}
