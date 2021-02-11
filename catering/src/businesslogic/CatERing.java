package businesslogic;

import businesslogic.event.EventManager;
import businesslogic.kitchenTask.KitchenTaskManager;
import businesslogic.menu.Menu;
import businesslogic.menu.MenuManager;
import businesslogic.recipe.RecipeManager;
import businesslogic.user.UserManager;
import businesslogic.workShift.WorkshiftManager;
import persistence.KitchenTaskPersistence;
import persistence.MenuPersistence;
import persistence.PersistenceManager;
import persistence.WorkShiftPersistence;

public class CatERing {
    private static CatERing singleInstance;

    public static CatERing getInstance() {
        if (singleInstance == null) {
            singleInstance = new CatERing();
        }
        return singleInstance;
    }

    private MenuManager menuMgr;
    private RecipeManager recipeMgr;
    private UserManager userMgr;
    private EventManager eventMgr;
    private KitchenTaskManager kitchenMgr;
    private WorkshiftManager workshitMgr;

    private MenuPersistence menuPersistence;
    private KitchenTaskPersistence kitchenPersistence;
    private WorkShiftPersistence workShiftPersistence;

    private CatERing() {
        menuMgr = new MenuManager();
        recipeMgr = new RecipeManager();
        userMgr = new UserManager();
        eventMgr = new EventManager();
        kitchenMgr = new KitchenTaskManager();
        workshitMgr = new WorkshiftManager();
        menuPersistence = new MenuPersistence();
        menuMgr.addEventReceiver(menuPersistence);

        kitchenPersistence = new KitchenTaskPersistence();
        kitchenMgr.addEventReceiver(kitchenPersistence);

        workShiftPersistence = new WorkShiftPersistence();
        workshitMgr.addEventReceiver(workShiftPersistence);

    }


    public MenuManager getMenuManager() {
        return menuMgr;
    }

    public RecipeManager getRecipeManager() {
        return recipeMgr;
    }

    public UserManager getUserManager() {
        return userMgr;
    }

    public EventManager getEventManager() { return eventMgr; }

    public KitchenTaskManager getKitchenMgr() {
        return kitchenMgr;
    }

    public WorkshiftManager getWorkshitMgr() {
        return workshitMgr;
    }
}
