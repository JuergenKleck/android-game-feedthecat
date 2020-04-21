package info.simplyapps.game.feedthecat.storage.dto;

import java.io.Serializable;

import info.simplyapps.appengine.storage.dto.BasicTable;

public class Inventory extends BasicTable implements Serializable {

    /**
     * serial id
     */
    private static final long serialVersionUID = -7101789009229738387L;

    public long points;
    public long coins;

    public Inventory() {
        coins = 0L;
        points = 0L;
    }

}
