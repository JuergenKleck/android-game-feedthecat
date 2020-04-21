package info.simplyapps.game.feedthecat;

import info.simplyapps.game.feedthecat.storage.StoreData;
import info.simplyapps.game.feedthecat.storage.dto.CurrentGame;
import info.simplyapps.game.feedthecat.storage.dto.Inventory;

/**
 * @author simplyapps.info
 */
public class SystemHelper extends info.simplyapps.appengine.SystemHelper {

    public synchronized static final Inventory getInventory() {
        return StoreData.getInstance().inventory;
    }

    public synchronized static final CurrentGame getCurrentGame() {
        return StoreData.getInstance().currentGame;
    }

}
