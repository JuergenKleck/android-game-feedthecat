package info.simplyapps.game.feedthecat.storage.dto;

import java.io.Serializable;

import info.simplyapps.appengine.storage.dto.BasicTable;

public class CurrentGame extends BasicTable implements Serializable {

    /**
     * serial id
     */
    private static final long serialVersionUID = -7101789009229738387L;

    // the round number
    public int round;
    // the level to play
    public int level;
    // the life
    public float life;
    // the points collected
    public long points;
    // the collected cat coins
    public long coins;
    public long totalCoins;
    // is in jump
    public boolean jumped;
    // time elapsed
    public long time;
    // the rage amount
    public float rage;

}
