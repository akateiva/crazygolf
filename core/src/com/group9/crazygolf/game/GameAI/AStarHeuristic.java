package com.group9.crazygolf.game.GameAI;

import com.badlogic.ashley.core.Entity;

/**
 * crazygolf
 * 2016
 *
 * Aleksas Kateiva
 * Eric Chang
 * Adeline Mekic
 * Florian Kok
 * Roger Sijben
 */
public interface AStarHeuristic {
    /**
     * Get the additional heuristic cost of the given tile. This controls the
     * order in which tiles are searched while attempting to find a path to the
     * target location. The lower the cost the more likely the tile will
     * be searched.
     *
     * @param map The map on which the path is being found
     * @param ball The entity that is moving along the path
     * @param x The x coordinate of the tile being evaluated
     * @param y The y coordinate of the tile being evaluated
     * @param z The z coordinate of the tile being evaluated
     * @param tx The x coordinate of the target location
     * @param ty The y coordinate of the target location
     * @param tz The z coordinate of the target location
     * @return The cost associated with the given tile
     */
    float getCost(NavMesh map, Entity ball, int x, int y, int z, int tx, int ty, int tz);
}
