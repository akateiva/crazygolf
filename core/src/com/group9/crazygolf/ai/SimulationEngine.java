package com.group9.crazygolf.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.group9.crazygolf.entities.components.PlayerComponent;
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
    ArrayList<Vector3> pathVec;
    int index;

    LinkedList<SimulationRequest> requests;

    public boolean hasRequests(){
        return requests.size() > 0;
    }

    public SimulationEngine(PhysicsSystem physicsSystem, HoleSystem holeSystem, ArrayList<Vector3> pvc) {
        this.physicsSystem = physicsSystem;
        this.holeSystem = holeSystem;
        pathVec = pvc;
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
        final int timeout = 4;
        for(int i = 0; i < timeout/physicsSystem.getStepSize(); i++){
            physicsSystem.update(physicsSystem.getStepSize());
            //ERIC'S HEURISTIC
            if(cur.entity.getComponent(PlayerComponent.class).astar) {
                float dst2 = holeSystem.dst2ClosestHole(cur.entity);
                float distance = getClosestVec(cur.entity.getComponent(StateComponent.class).position, cur);

                if (dst2 < cur.bestShotHeuristic) {
                    cur.bestShotHeuristic = dst2;
                    cur.bestShot = shot;
                } else if (cur.Index < index || distance < cur.disToCloseVec) {
                    cur.Index = index;
                    cur.disToCloseVec = distance;
                    //cur.bestShotHeuristic = distance;
                    cur.bestShot = shot;
                }
            }else {
                //ALEX AK-47 $$$$$ VIP HEURISTIC
                //Measure the distance from the hole after the simulation
                float dst2 = holeSystem.dst2ClosestHole(cur.entity);
                if(dst2 < cur.bestShotHeuristic){
                    cur.bestShotHeuristic = dst2;
                    cur.bestShot = shot;
                }
            }
        }

        physicsSystem.restoreStates();
    }

    public interface SimulationListener{
        void finished(Shot bestShot);
    }

    public float getClosestVec(Vector3 position, SimulationRequest cur){
        float distance = Float.MAX_VALUE;
        for(int i=index;i<pathVec.size();i++){
            if(pathVec.get(i).dst2(position)<distance){
                distance = pathVec.get(i).dst2(position);
                index = i;
            }
        }
        return distance;
    }
}

