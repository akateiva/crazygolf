package com.group9.crazygolf.entities.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by akateiva on 08/05/16.
 */
public class PhysicsComponent implements Component {
    //An Entity is dynamic if it moves (i.e. ball)
    public float restitution = 0.80f;
    public float friction = 0.5f;
}
