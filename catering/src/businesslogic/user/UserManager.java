package businesslogic.user;

import businesslogic.workShift.WorkShift;
import javafx.collections.ObservableList;

import java.util.List;

public class UserManager {
    private User currentUser;

    public void fakeLogin(String username) //TODO: bisogna implementare il login vero!
    {
        this.currentUser = User.loadUser(username);
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public ObservableList<User> getCooks(WorkShift selectedItem) {
        return User.getCooks(selectedItem);
    }

    public ObservableList<User> getAllCooks() {
        return User.getAllCooks();
    }
}
