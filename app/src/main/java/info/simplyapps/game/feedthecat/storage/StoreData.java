package info.simplyapps.game.feedthecat.storage;

import info.simplyapps.appengine.AppEngineConstants;
import info.simplyapps.appengine.storage.dto.Configuration;
import info.simplyapps.game.feedthecat.SystemHelper;
import info.simplyapps.game.feedthecat.storage.dto.CurrentGame;
import info.simplyapps.game.feedthecat.storage.dto.Inventory;

public class StoreData extends info.simplyapps.appengine.storage.StoreData {

    private static final long serialVersionUID = 5696810296031292822L;

    public Inventory inventory;
    public CurrentGame currentGame;

    public static StoreData getInstance() {
        return (StoreData) info.simplyapps.appengine.storage.StoreData.getInstance();
    }

    /**
     * Update to the latest release
     */
    public boolean update() {
        boolean persist = false;

        // Release 9 - 1.3
        if (migration < 9) {
            inventory = new Inventory();
            currentGame = new CurrentGame();
            SystemHelper.setConfiguration(new Configuration(AppEngineConstants.CONFIG_ON_SERVER, AppEngineConstants.DEFAULT_CONFIG_ON_SERVER));
            SystemHelper.setConfiguration(new Configuration(AppEngineConstants.CONFIG_FORCE_UPDATE, AppEngineConstants.DEFAULT_CONFIG_FORCE_UPDATE));
            SystemHelper.setConfiguration(new Configuration(AppEngineConstants.CONFIG_LAST_CHECK, AppEngineConstants.DEFAULT_CONFIG_LAST_CHECK));
            persist = true;
        }

        // Release 10 - 2.0
        if (migration < 10) {
//			persist = true;
        }

        // Release 11 - 2.0.1
        if (migration < 11) {

        }

        // Release 12 - 2.1.0
        if (migration < 12) {

        }

        migration = 12;
        return persist;
    }

}
