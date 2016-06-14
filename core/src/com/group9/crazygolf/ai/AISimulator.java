package com.group9.crazygolf.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.entities.components.StateComponent;
import com.group9.crazygolf.entities.systems.HoleSystem;
import com.group9.crazygolf.entities.systems.PhysicsSystem;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by akateiva on 6/12/16.
 */
public class AISimulator {
    private PhysicsSystem physicsSystem;
    private HoleSystem holeSystem;

    public AISimulator(PhysicsSystem physicsSystem, HoleSystem holeSystem) {
        this.physicsSystem = physicsSystem;
        this.holeSystem = holeSystem;
    }

    /**
     * Will simulate a shot
     * @param shot the shot parameters
     * @param ply the player entity
     * @param timeout the timeout of the simulation in seconds
     * @return float heuristic value of the shot result
     */
    public float simulate(Shot shot, Entity ply, float timeout){
        physicsSystem.saveStates();
        applyShot(shot, ply);

        //hacky and wacky way to bypass the time accumulator on the physics engine, so we can call the physics and hole systems
        //sequentially ( what the fuck is this? )


        boolean holed = false;
        for(int i = 0; i < timeout/physicsSystem.getStepSize(); i++){
            physicsSystem.update(physicsSystem.getStepSize());
        }


        float dst2 = holeSystem.dst2ClosestHole(ply);
        physicsSystem.restoreStates();

        return dst2;
    }


    public Shot tryRandomShots(int attempts, Entity ply, float timeout){
        Shot bestShot = null;
        float bestHeuristic = Float.MAX_VALUE;
        for(int i = 0; i < attempts; i++){
            Random rand = new Random();
            Shot shot = new Shot();
            shot.direction = new Vector3(rand.nextFloat()-0.5f, 0f, rand.nextFloat()-0.5f).nor();
            shot.power = 2;
            float heuristic = simulate(shot, ply, timeout);
            if(heuristic < bestHeuristic){
                bestHeuristic = heuristic;
                bestShot = shot;
            }
        }
        return bestShot;
    }

    public void applyShot(Shot shot, Entity ply){
        ply.getComponent(StateComponent.class).momentum.mulAdd(shot.direction, shot.power);
    }
}

class Shot{
    Vector3 direction;
    float power;
}