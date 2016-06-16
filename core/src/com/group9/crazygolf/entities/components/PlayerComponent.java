package com.group9.crazygolf.entities.components;

import com.badlogic.ashley.core.Component;

/**
 * Marks entities as player controlled.
 */
public class PlayerComponent implements Component {

    /**
     * The name of the player
     */
    public String name;

    public boolean ai;

    /**
     * Player has finished this course. ( his ball went into the hole
     */
    public boolean finished = false;
}
