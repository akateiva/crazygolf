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
    public Vector3 position;                ///< the position of the cube center of mass in world coordinates (meters).
    public Vector3 momentum = new Vector3();                ///< the momentum of the cube in kilogram meters per second.
    public Quaternion orientation = new Quaternion();         ///< the orientation of the cube represented by a unit quaternion.
    public Vector3 angularMomentum = new Vector3();         ///< angular momentum vector.

    //Secondary state
    public Vector3 velocity = new Vector3();                ///< velocity in meters per second (calculated from momentum).
    public Quaternion spin = new Quaternion();                ///< quaternion rate of change in orientation.
    public Vector3 angularVelocity = new Vector3();         ///< angular velocity (calculated from angularMomentum).

    //Constant state
    public float mass;                     ///< mass of the cube in kilograms.
    public float inverseMass;              ///< inverse of the mass used to convert momentum to velocity.
    public float inertiaTensor;            ///< inertia tensor of the cube (i have simplified it to a single value due to the mass properties a cube).
    public float inverseInertiaTensor;     ///< inverse inertia tensor used to convert angular momentum to angular velocity.
    public Vector3 scale = new Vector3(1, 1, 1);

    //Model transformation
    public Matrix4 transform = new Matrix4();

    public void update() {
        velocity.set(momentum).scl(inverseMass);
        angularVelocity.set(angularMomentum).scl(inverseInertiaTensor);
        transform.idt().rotate(orientation).scale(scale.x, scale.y, scale.z).translate(position);
        //orientation.normalize(); TODO: Fix
        //spin = 0.5 * nQuaternion(0, angularVelocity.x, angularVelocity.y, angularVelocity.z) * orientation;
        //Transform matrix pls
    }
}
