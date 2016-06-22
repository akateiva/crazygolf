package com.group9.crazygolf.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Iterator;

public class SimulationRequest{
    Entity entity;
    ArrayList<Shot> shots;
    SimulationEngine.SimulationListener listener;

    Iterator<Shot> shotIterator;
    Shot bestShot;
    float bestShotHeuristic;
    int Index;
    float disToCloseVec;

    public SimulationRequest(SimulationEngine.SimulationListener listener, ArrayList<Shot> shots, Entity entity) {
        this.listener = listener;
        this.shots = shots;
        this.entity = entity;

        bestShot = null;
        bestShotHeuristic = Float.MAX_VALUE;
        shotIterator = shots.iterator();
        Index = 0;
        disToCloseVec =  Float.MAX_VALUE;
    }
}

