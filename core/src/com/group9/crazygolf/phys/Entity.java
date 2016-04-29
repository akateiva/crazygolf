package com.group9.crazygolf.phys;


import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by akateiva on 18/04/16.
 */
public class Entity {

    private Vector3 position = new Vector3();   //Position vector (m)
    private ModelInstance modelInstance;
    private PhysMaterial physMaterial;

    public Entity( ModelInstance modelInstance){
        this(modelInstance, new PhysMaterial());
    }

    public Entity(ModelInstance modelInstance, PhysMaterial physMaterial) {
        this.modelInstance = modelInstance;
        this.physMaterial = physMaterial;
    }
    /**
     * Apply updates on the entity
     * @param dt
     */
    public void update(float dt){

    }

    /**
     * @return modelInstance the model instance attached to this entity
     */
    public ModelInstance getModelInstance() {
        return modelInstance;
    }


    /**
     * @return the position of the entity
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * @param position the position of the entity
     */
    public void setPosition(Vector3 position) {
        this.position = position;
        this.modelInstance.transform.set(position, new Quaternion());
    }

    public PhysMaterial getPhysMaterial() {
        return physMaterial;
    }

    public void setPhysMaterial(PhysMaterial physMaterial) {
        this.physMaterial = physMaterial;
    }
}
