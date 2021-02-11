package businesslogic.event;

import businesslogic.user.User;
import javafx.collections.ObservableList;

public class EventManager {
    private EventInfo currentEvent;

    public ObservableList<EventInfo> getEventInfo() {
        return EventInfo.loadAllEventInfo();
    }

    public ObservableList<EventInfo> getEventFromUser(User currentUser) {
        return EventInfo.loadEventFromUser(currentUser);
    }

    public void setCurrentEvent(EventInfo newEventInfo) {
        this.currentEvent = newEventInfo;
    }

    public User getChef() {
        return currentEvent.getChef();
    }

    public ObservableList<ServiceInfo> getServices() {
        return currentEvent.getServices();
    }
}
