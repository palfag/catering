package businesslogic.kitchenTask;

public interface KitchenTaskEventReceiver {
	void updateSheetCreated(SummarySheet currentSheet);

	void updateSheetOpened(SummarySheet currentSheet);


	void updateSheetDeleted(SummarySheet currentSheet);

    void updateTaskCreated(SummarySheet currentSheet, Task task);

    void updateTaskRemoved(SummarySheet currentSheet, Task task);

	void updateTaskRearranged(SummarySheet currentSheet);

    void updateTaskModified(SummarySheet currentSheet, Task task);

	void updateTimeAdded(SummarySheet currentSheet, Task selectedTask);

	void updateQuantityAdded(SummarySheet currentSheet, Task selectedTask);

    void updateTaskAssigned(SummarySheet currentSheet, Task selectedTask);

	void updateAssignmentNullified(SummarySheet currentSheet, Task selectedTask);
}
