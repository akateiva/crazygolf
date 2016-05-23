package com.group9.crazygolf.entities.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by akateiva on 08/05/16.
 */
public class PhysicsComponent implements Component {
    public float restitution = 0.10f;
    public float friction = 0.5f;
}
