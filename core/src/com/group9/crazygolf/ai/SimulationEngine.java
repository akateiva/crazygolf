package com.group9.crazygolf.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.group9.crazygolf.entities.components.StateComponent;
import com.group9.crazygolf.entities.systems.HoleSystem;
import com.group9.crazygolf.entities.systems.PhysicsSystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by akateiva on 6/16/16.s
 */
public class SimulationEngine {

    PhysicsSystem physicsSystem;
    HoleSystem holeSystem;

    LinkedList<SimulationRequest> requests;

    public boolean hasRequests(){
        return requests.size() > 0;
    }

    public SimulationEngine(PhysicsSystem physicsSystem, HoleSystem holeSystem) {
        this.physicsSystem = physicsSystem;
        this.holeSystem = holeSystem;
        requests = new LinkedList<SimulationRequest>();
    }

    public void addRequest(SimulationRequest request){
        requests.add(request);
    }

    /**
     * Simulates one shot in one simulation request.
     * @param deltaTime probably unused
     */
    public void update(float deltaTime) {

        //No requests to handle, stop
        if (requests.size() < 1)
            return;


        //Get the first simulation request from the queue, but don't remove it
        SimulationRequest cur = requests.peek();

        //If all shots in the simulation have been processed, we can call the listener and discard this simulation
        if (!cur.shotIterator.hasNext()) {
            cur.listener.finished(cur.bestShot);
            requests.remove();
            return;
        }

        //Simulate one shot
        //Save the states of all components ( we'll be reverting back to this after the simulation to not alter anything in the actual game)
        physicsSystem.saveStates();

        Shot shot = cur.shotIterator.next();

        //Apply the shot
        cur.entity.getComponent(StateComponent.class).momentum.mulAdd(shot.direction, shot.power);
        physicsSystem.update(4); // magic number 4: 4 second simulation time-out

        //Measure the distance from the hole after the simulation
        float dst2 = holeSystem.dst2ClosestHole(cur.entity);
        if(dst2 < cur.bestShotHeuristic){
            cur.bestShotHeuristic = dst2;
            cur.bestShot = shot;
        }
        physicsSystem.restoreStates();
    }

    public interface SimulationListener{
        void finished(Shot bestShot);
    }
}

