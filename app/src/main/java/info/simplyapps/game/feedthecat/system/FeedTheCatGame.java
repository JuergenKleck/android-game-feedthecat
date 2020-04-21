package info.simplyapps.game.feedthecat.system;

import info.simplyapps.gameengine.system.Game;
import info.simplyapps.gameengine.system.GameRound;

public class FeedTheCatGame extends Game {

    public boolean complete;
    public long points;
    public long coins;
    public long totalCoins;
    public boolean jumped;
    public boolean credited;
    public int difficulty;

    public FeedTheCatGame(GameRound[] rounds) {
        super(rounds);
    }

    public void reset() {
        super.reset();
        life = 0.0f;
    }

}
