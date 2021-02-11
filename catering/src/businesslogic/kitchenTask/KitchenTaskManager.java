package businesslogic.kitchenTask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.ServiceInfo;
import businesslogic.recipe.Procedure;
import businesslogic.user.User;
import businesslogic.workShift.WorkShift;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import ui.kitchenTask.KitchenTaskManagement;

import java.util.ArrayList;

public class KitchenTaskManager {
	private SummarySheet currentSheet;
	private ArrayList<KitchenTaskEventReceiver> eventReceivers;

	public KitchenTaskManager() {
		this.eventReceivers = new ArrayList<>();
	}

	public SummarySheet getCurrentSheet() {
		return currentSheet;
	}

	public void setCurrentSheet(SummarySheet currentSheet) {
		this.currentSheet = currentSheet;
	}

	public SummarySheet loadSummarySheet(ServiceInfo service) {
		return SummarySheet.loadSummarySheet(service);
	}

	public void createSheet(ServiceInfo service) throws UseCaseLogicException, KitchenTaskException {
		User u = CatERing.getInstance().getUserManager().getCurrentUser();
		if (u.isChef() && u.equals(CatERing.getInstance().getEventManager().getChef())) //equals e non == perchè possono essere oggetti diversi
		{
			if (CatERing.getInstance().getEventManager().getServices().contains(service)) {
				SummarySheet sheet = new SummarySheet(service);
				setCurrentSheet(sheet);
				notifySheetCreated();
			} else
				throw new KitchenTaskException();
		} else
			throw new UseCaseLogicException();
	}

	public void openSheet(ServiceInfo service) throws KitchenTaskException {
		User u = CatERing.getInstance().getUserManager().getCurrentUser();
		if (u.isChef() && u.equals(CatERing.getInstance().getEventManager().getChef())) //equals e non == perchè possono essere oggetti diversi
		{
			SummarySheet sheet = CatERing.getInstance().getKitchenMgr().loadSummarySheet(service);
			setCurrentSheet(sheet);
			notifySheetOpened();
		} else
			throw new KitchenTaskException();
	}


	public void restoreSheet(ServiceInfo newServiceInfo) throws KitchenTaskException, UseCaseLogicException {
		User u = CatERing.getInstance().getUserManager().getCurrentUser();
		if (u.isChef() && u.equals(CatERing.getInstance().getEventManager().getChef())) //equals e non == perchè possono essere oggetti diversi
		{
			if (CatERing.getInstance().getEventManager().getServices().contains(newServiceInfo))//&& foglio associato a servizio non credo si debba ,ettere perchè è scontato, lo faccio via software
			{
				SummarySheet toDelete = CatERing.getInstance().getKitchenMgr().loadSummarySheet(newServiceInfo);
				setCurrentSheet(toDelete);
				notifySheetDeleted();
				SummarySheet sheet = new SummarySheet(newServiceInfo);
				setCurrentSheet(sheet);
				notifySheetCreated();
			} else
				throw new KitchenTaskException();
		}

	}

	//NOTA BENE: inserire una procedura vuol dire andare a creare un COMPITO (task) in base a una procedura
	public void insertProcedure(Procedure newProcedure) throws UseCaseLogicException {
		Task task = null;
		if (currentSheet != null) {
			task = new Task(newProcedure);
			currentSheet.getTasks().add(task);//aggiungo il task alla lista dei task del foglio
			notifyTaskCreated(task);
		} else
			throw new UseCaseLogicException();
	}

	public void removeTask(Task task) throws KitchenTaskException, UseCaseLogicException {
		if (currentSheet != null && currentSheet.hasTask(task)) {
			if (!task.isCompleted()) {
				currentSheet.removeTask(task);
				notifyTaskRemoved(task);
			} else throw new KitchenTaskException();
		} else throw new UseCaseLogicException();
	}

	public void moveTask(Task task, int newpos) throws UseCaseLogicException {
		if (currentSheet != null && currentSheet.hasTask(task)) {
			if (newpos >= 0 || newpos < currentSheet.getTasksCount()) {
				currentSheet.moveTask(task, newpos);
				notifyTaskRearranged();
			}
		} else
			throw new UseCaseLogicException();
	}

	public void taskIsReady(Task task) throws UseCaseLogicException {
		if (currentSheet != null && currentSheet.hasTask(task))
			notifyTaskModified(task);
		else
			throw new UseCaseLogicException();
	}

	public void providesTime(Task selectedTask, int time) throws UseCaseLogicException {
		if (currentSheet != null && currentSheet.hasTask(selectedTask)) {
			selectedTask.setEstimateTime(time);
			notifyTimeAdded(selectedTask);
		} else
			throw new UseCaseLogicException();
	}

	public void providesQuantity(Task selectedTask, String quantity) throws UseCaseLogicException {
		if (currentSheet != null && currentSheet.hasTask(selectedTask)) {
			selectedTask.setQuantity(quantity);
			notifyQuantityAdded(selectedTask);
		} else
			throw new UseCaseLogicException();
	}

	public void assignTask(Task selectedTask, WorkShift kitchenShift) throws UseCaseLogicException, KitchenTaskException {
		if (currentSheet != null && currentSheet.hasTask(selectedTask)) {
			if (!selectedTask.isCompleted()) {
				if (!kitchenShift.isPast()) {
					selectedTask.setKitchenShift(kitchenShift);
				}else throw new KitchenTaskException();
				notifyTaskAssigned(selectedTask);
			} else
				throw new KitchenTaskException();

		} else
			throw new UseCaseLogicException();
	}

	public void assignTask(Task selectedTask, WorkShift kitchenShift, User cook) throws UseCaseLogicException, KitchenTaskException {
		if (currentSheet != null && currentSheet.hasTask(selectedTask)) {
			if (!selectedTask.isCompleted()) {
				if (!kitchenShift.isPast()) {
					selectedTask.setKitchenShift(kitchenShift);
				}else throw new KitchenTaskException();
				if (kitchenShift.isAvailable(cook, kitchenShift)){
					selectedTask.setCook(cook);
				}else throw new KitchenTaskException();
				notifyTaskAssigned(selectedTask);
			} else
				throw new KitchenTaskException();

		} else
			throw new UseCaseLogicException();
	}

	//modifico solo procedura
	public void modifyAssignment(Task selectedTask, Procedure procedure) throws KitchenTaskException, UseCaseLogicException {
		if (currentSheet != null && currentSheet.hasTask(selectedTask)) {
			if (!selectedTask.isCompleted()) {
				selectedTask.setProcedure(procedure);

				notifyTaskModified(selectedTask);
			} else
				throw new KitchenTaskException();

		} else
			throw new UseCaseLogicException();
	}
	//modifico solo il TURNO
	public void modifyAssignment(Task selectedTask, WorkShift kitchenShift) throws KitchenTaskException, UseCaseLogicException {
		if (currentSheet != null && currentSheet.hasTask(selectedTask)) {
			if (!selectedTask.isCompleted()) {
				if(!kitchenShift.isPast()){
					selectedTask.setKitchenShift(kitchenShift);
					if (!kitchenShift.isAvailable(selectedTask.getCook(), kitchenShift)){
						selectedTask.setCook(null);
					}
				}else throw new KitchenTaskException();
				notifyTaskModified(selectedTask);
			} else
				throw new KitchenTaskException();

		} else
			throw new UseCaseLogicException();
	}
	//modifico solo il CUOCO
	public void modifyAssignment(Task selectedTask, User cook) throws KitchenTaskException, UseCaseLogicException {
		if (currentSheet != null && currentSheet.hasTask(selectedTask)) {
			if (!selectedTask.isCompleted()) {
				if(selectedTask.getKitchenShift().isAvailable(cook, selectedTask.getKitchenShift()))
					selectedTask.setCook(cook);

				notifyTaskModified(selectedTask);
			} else
				throw new KitchenTaskException();

		} else
			throw new UseCaseLogicException();
	}
	public void nullifyAssignment(Task selectedTask) throws KitchenTaskException, UseCaseLogicException {
		if (currentSheet != null && currentSheet.hasTask(selectedTask)) {
			if (!selectedTask.isCompleted()) {
				selectedTask.setCook(null);
				selectedTask.setKitchenShift(null);
				notifyNullifiedAssignment(selectedTask);
			}
			else
				throw new KitchenTaskException();
		} else
			throw new UseCaseLogicException();
	}
	public ObservableList<Task> getTasks(WorkShift newShift) {
		return Task.getTasks(newShift);
	}




	private void notifyTaskAssigned(Task selectedTask) {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateTaskAssigned(currentSheet, selectedTask);
	}

	private void notifyQuantityAdded(Task selectedTask) {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateQuantityAdded(currentSheet, selectedTask);
	}

	private void notifyTimeAdded(Task selectedTask) {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateTimeAdded(currentSheet, selectedTask);
	}

	private void notifyTaskModified(Task task) {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateTaskModified(currentSheet, task);
	}


	private void notifyTaskRemoved(Task task) {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateTaskRemoved(currentSheet, task);
	}


	public void addEventReceiver(KitchenTaskEventReceiver rec) {
		this.eventReceivers.add(rec);
	}

	private void notifySheetCreated() {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateSheetCreated(currentSheet);
	}

	private void notifySheetOpened() {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateSheetOpened(currentSheet);
	}

	private void notifySheetDeleted() {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateSheetDeleted(currentSheet);
	}

	private void notifyTaskCreated(Task task) {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateTaskCreated(currentSheet, task);
	}

	private void notifyTaskRearranged() {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateTaskRearranged(currentSheet);
	}

	private void notifyNullifiedAssignment(Task selectedTask) {
		for (KitchenTaskEventReceiver receiver : eventReceivers)
			receiver.updateAssignmentNullified(currentSheet, selectedTask);
	}



}
