package businesslogic.user;

import businesslogic.workShift.WorkShift;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.PersistenceManager;
import persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class User {

    private static Map<Integer, User> loadedUsers = FXCollections.observableHashMap();



    public void setId(int id) {
        this.id=id;
    }

    public void setUsername(String username) {
        this.username=username;
    }


    public static enum Role {SERVIZIO, CUOCO, CHEF, ORGANIZZATORE};

    private int id;
    private String username;
    private Set<Role> roles;

    public User() {
        id = 0;
        username = "";
        this.roles = new HashSet<>();
    }


    public boolean isChef() {
        return roles.contains(Role.CHEF);
    }

    public String getUserName() {
        return username;
    }

    public int getId() {
        return this.id;
    }

    public String toString() {
        return username;
    }

    // STATIC METHODS FOR PERSISTENCE

    public static User loadUserById(int uid) {
        if (loadedUsers.containsKey(uid)) return loadedUsers.get(uid);

        User load = new User();
        String userQuery = "SELECT * FROM users WHERE id='"+uid+"'";
        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                load.id = rs.getInt("id");
                load.username = rs.getString("username");
            }
        });
        if (load.id > 0) {
            loadedUsers.put(load.id, load);
            String roleQuery = "SELECT * FROM userroles WHERE user_id=" + load.id;
            PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    String role = rs.getString("role_id");
                    switch (role.charAt(0)) {
                        case 'c':
                            load.roles.add(User.Role.CUOCO);
                            break;
                        case 'h':
                            load.roles.add(User.Role.CHEF);
                            break;
                        case 'o':
                            load.roles.add(User.Role.ORGANIZZATORE);
                            break;
                        case 's':
                            load.roles.add(User.Role.SERVIZIO);
                    }
                }
            });
        }
        return load;
    }

    public static User loadUser(String username) {
        User u = new User();
        String userQuery = "SELECT * FROM users WHERE username='"+username+"'";
        PersistenceManager.executeQuery(userQuery, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
            }
        });
        if (u.id > 0) {
            loadedUsers.put(u.id, u);
            String roleQuery = "SELECT * FROM userroles WHERE user_id=" + u.id;
            PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
                @Override
                public void handle(ResultSet rs) throws SQLException {
                    String role = rs.getString("role_id");
                    switch (role.charAt(0)) {
                        case 'c':
                            u.roles.add(User.Role.CUOCO);
                            break;
                        case 'h':
                            u.roles.add(User.Role.CHEF);
                            break;
                        case 'o':
                            u.roles.add(User.Role.ORGANIZZATORE);
                            break;
                        case 's':
                            u.roles.add(User.Role.SERVIZIO);
                    }
                }
            });
        }
        return u;
    }

    public static ObservableList<User> getCooks(WorkShift selectedItem) {
        String select = "SELECT * FROM users, userroles, availability WHERE users.id = userroles.user_id AND role_id = 'c' AND availability.user_id = users.id" +
                " AND availability.workshift_id="+selectedItem.getId()+" AND users.id  NOT IN (" +//non voglio i cuochi già presi in quel turno
                "SELECT cook_id FROM catering.tasks WHERE users.id =  cook_id AND kitchenShift_id = "+ selectedItem.getId()+")";
        ObservableList<User> cooks= FXCollections.observableArrayList();
        PersistenceManager.executeQuery(select, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                do{
                    User c = new User();
                    c.setUsername(rs.getString("username"));
                    c.setId(rs.getInt("id"));
                    cooks.add(c);
                }while (rs.next());
            }
        });
        return cooks;
    }

    public static ObservableList<User> getAllCooks() {
        String select = "SELECT * FROM users, userroles WHERE users.id = userroles.user_id AND role_id = 'c'";
        ObservableList<User> cooks= FXCollections.observableArrayList();
        PersistenceManager.executeQuery(select, new ResultHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                do{
                    User c = new User();
                    c.setUsername(rs.getString("username"));
                    c.setId(rs.getInt("id"));
                    cooks.add(c);
                }while (rs.next());
            }
        });
        return cooks;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.getId();
    }


}
