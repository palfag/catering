package persistence;

import businesslogic.kitchenTask.KitchenTaskEventReceiver;
import businesslogic.kitchenTask.SummarySheet;
import businesslogic.kitchenTask.Task;

public class KitchenTaskPersistence implements KitchenTaskEventReceiver {
	@Override
	public void updateSheetCreated(SummarySheet currentSheet) {
		SummarySheet.saveNewSummarySheet(currentSheet);
	}

	@Override
	public void updateSheetOpened(SummarySheet currentSheet) {
		//NIENTE?
	}
	@Override
	public void updateSheetDeleted(SummarySheet currentSheet) {
		SummarySheet.deleteSummarySheet(currentSheet);
	}

	@Override
	public void updateTaskCreated(SummarySheet currentSheet, Task task) {
		Task.saveNewTask(currentSheet, task);
	}

	@Override
	public void updateTaskRemoved(SummarySheet currentSheet, Task task) {
		Task.removeTask(currentSheet, task);
	}

	@Override
	public void updateTaskRearranged(SummarySheet currentSheet) {
		SummarySheet.saveTasksOrder(currentSheet);
	}

	@Override
	public void updateTaskModified(SummarySheet currentSheet, Task task) {
		task.updateTask(currentSheet, task);
	}

	@Override
	public void updateTimeAdded(SummarySheet currentSheet, Task selectedTask) {
		selectedTask.updateTask(currentSheet, selectedTask);
	}

	@Override
	public void updateQuantityAdded(SummarySheet currentSheet, Task selectedTask) {
		selectedTask.updateTask(currentSheet, selectedTask);
	}

	@Override
	public void updateTaskAssigned(SummarySheet currentSheet, Task selectedTask) {
		selectedTask.updateTask(currentSheet, selectedTask);
	}

	@Override
	public void updateAssignmentNullified(SummarySheet currentSheet, Task selectedTask) {
		selectedTask.nullifyAssigment(currentSheet, selectedTask);
	}


}
