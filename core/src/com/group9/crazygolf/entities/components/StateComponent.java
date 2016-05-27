package com.group9.crazygolf.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * StateComponent
 * <p>
 * This component stores the position of the entity, the quaternion (rotation data) and the scale of the entity.
 */
public class StateComponent implements Component {
    //Primary state
    public Vector3 position = new Vector3();                ///< the position of the cube center of mass in world coordinates (meters).
    public Vector3 momentum = new Vector3();                ///< the momentum of the cube in kilogram meters per second.
    public Quaternion orientation = new Quaternion();         ///< the orientation of the cube represented by a unit quaternion.
    //Secondary state
    public Vector3 velocity = new Vector3();                ///< velocity in meters per second (calculated from momentum).

    //Constant state
    public float mass;                     ///< mass of the cube in kilograms.
    public float inverseMass;              ///< inverse of the mass used to convert momentum to velocity.
    public Vector3 scale = new Vector3(1, 1, 1);

    //Model transformation
    public Matrix4 transform = new Matrix4();
    public boolean autoTransformUpdate = true;

    public void update() {
        velocity.set(momentum).scl(inverseMass);
        if (autoTransformUpdate) {
            transform.idt().rotate(orientation).scale(scale.x, scale.y, scale.z).translate(position);
        }
        //orientation.normalize(); TODO: Fix
        //spin = 0.5 * nQuaternion(0, angularVelocity.x, angularVelocity.y, angularVelocity.z) * orientation;
        //Transform matrix pls
    }
}
