package com.group9.crazygolf;

import java.util.ArrayList;

/**
 * Created by akateiva on 17/04/16.
 */


public class PlayersHandler {
    enum PlayerType{
        HUMAN, AI_EASY, AI_NORMAL, AI_HARD
    }

    private ArrayList<PlayerType> players;

    PlayersHandler(){
        players = new ArrayList<PlayerType>();
    }

    public void addPlayer(PlayerType type){
        players.add(type);
    }


}
