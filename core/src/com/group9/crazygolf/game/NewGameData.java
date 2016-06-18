package com.group9.crazygolf.game;

import java.util.ArrayList;

/**
 * crazygolf
 * 2016
 *
 * Aleksas Kateiva
 * Eric Chang
 * Adeline Mekic
 * Florian Kok
 * Roger Sijben
 */
public class NewGameData {
    String coursePath = "courses/don't yet exit lol";
    int playerCounter = 1, botCounter = 1;

    private ArrayList<Player> players;
    private ArrayList<Bot> bots;

    public NewGameData() {
        players = new ArrayList<Player>();
        bots = new ArrayList<Bot>();
    }

    public ArrayList<Player> getPlayerList() {
        return players;
    }

    public ArrayList<Bot> getBotList() {
        return bots;
    }

    public void addPlayer() {
        players.add(new Player("Player " + playerCounter++));
    }

    public void addBot() {
        bots.add(new Bot("AI Bot " + botCounter++, false, 0));
    }

    public class Player {
        public String name;

        private Player(String name) {
            this.name = name;
        }
    }

    public class Bot {
        public String name;
        public boolean AI;
        public int difficulty;

        private Bot(String name, boolean AI, int difficulty) {
            this.name = name;
            this.AI = AI;
            this.difficulty = difficulty;
        }
    }
}
