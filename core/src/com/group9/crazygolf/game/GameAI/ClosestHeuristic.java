package com.group9.crazygolf.game.GameAI;

import com.badlogic.ashley.core.Entity;

/**
 * Created by Florian on 9-6-2016.
 */
public class ClosestHeuristic implements AStarHeuristic {
    /**
     * @see AStarHeuristic#getCost(NavMesh, Entity, int, int, int, int, int, int)
     */
    public float getCost(NavMesh map, Entity ball, int x, int y, int z, int tx, int ty, int tz) {
        float dx = tx - x;
        float dy = ty - y;
        float dz = tz - z;

        float result = (float) (Math.sqrt((dx*dx)+(dy*dy)+(dz*dz)));

        return result;
    }
}
