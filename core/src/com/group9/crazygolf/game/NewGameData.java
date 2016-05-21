package com.group9.crazygolf.game;

import java.util.ArrayList;

/**
 * Created by akateiva on 21/05/16.
 */
public class NewGameData {
    String coursePath = "courses/don't yet exit lol";
    int playerCounter = 1;
    private ArrayList<Player> players;

    public NewGameData() {
        players = new ArrayList<Player>();
    }

    public ArrayList<Player> getPlayerList() {
        return players;
    }

    public void add() {
        players.add(new Player("Player " + playerCounter++, false, 0));
    }

    public class Player {
        public String name;
        public boolean AI;
        public int difficulty;

        private Player(String name, boolean AI, int difficulty) {
            this.name = name;
            this.AI = AI;
            this.difficulty = difficulty;
        }
    }
}
