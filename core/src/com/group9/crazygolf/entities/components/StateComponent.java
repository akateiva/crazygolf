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

    private boolean original = true; //is this the original or the copy
    private StateComponent save = null;

    public StateComponent() {

    }

    private StateComponent(StateComponent o) {
        this.position = o.position.cpy();
        this.momentum = o.momentum.cpy();
        this.orientation = o.orientation.cpy();
        this.velocity = o.velocity.cpy();
        this.mass = o.mass;
        this.inverseMass = o.inverseMass;
        this.scale = o.scale.cpy();
        this.transform = o.transform.cpy();
        this.autoTransformUpdate = o.autoTransformUpdate;
        this.original = false;
    }

    public void save(){
        if(original){
            if(save == null){
                save = new StateComponent(this);
            }else{
                save.set(this);
            }
        }
    }

    public void restore(){
        if(original){
            if(save != null)
                set(save);
        }
    }

    private void set(StateComponent o){
        this.position.set(o.position);
        this.momentum.set(o.momentum);
        this.orientation.set(o.orientation);
        this.velocity.set(o.velocity);
        this.mass = o.mass;
        this.inverseMass = o.inverseMass;
        this.scale.set(o.scale);
        this.transform.set(o.transform);
        this.autoTransformUpdate = o.autoTransformUpdate;
    }

    public void update() {
        velocity.set(momentum).scl(inverseMass);
        if (autoTransformUpdate) {
            //rotate seems to freeze?
            //transform.idt().rotate(orientation).scale(scale.x, scale.y, scale.z).translate(position);
            transform.idt().translate(position);
        }
        //orientation.normalize(); TODO: Fix
        //spin = 0.5 * nQuaternion(0, angularVelocity.x, angularVelocity.y, angularVelocity.z) * orientation;
        //Transform matrix pls
    }
}
