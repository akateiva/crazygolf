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
public interface NavMesh {

    /**
     * Get the width of the tile map. The slightly odd name is used
     * to distinguish this method from commonly used names in game maps.
     *
     * @return The number of tiles across the map
     */
    int getWidthInTiles();

    /**
     * Get the height of the tile map. The slightly odd name is used
     * to distinguish this method from commonly used names in game maps.
     *
     * @return The number of tiles down the map
     */
    int getHeightInTiles();

    /**
     * Get the depth of the tile map. The slightly odd name is used
     * to distinguish this method from commonly used names in game maps.
     *
     * @return The number of tiles deep the maps is
     */
    int getDepthInTiles();

    /**
     * Notification that the path finder visited a given tile. This is
     * used for debugging new heuristics.
     *
     * @param x The x coordinate of the tile that was visited
     * @param y The y coordinate of the tile that was visited
     * @param z The z coordinate of the tile that was visited
     */
    void pathFinderVisited(int x, int y, int z);

    /**
     * Check if the given location is blocked, i.e. blocks movement of
     * the supplied mover.
     *
     * @param ball The entity that is potentially moving through the specified
     * tile.
     * @param x The x coordinate of the tile to check
     * @param y The y coordinate of the tile to check
     * @param z The z coordinate of the tile to check
     * @return True if the location is blocked
     */
    boolean blocked(Entity ball, int x, int y, int z);

    /**
     * Get the cost of moving through the given tile. This can be used to
     * make certain areas more desirable. A simple and valid implementation
     * of this method would be to return 1 in all cases.
     *
     * @param ball The entity that is trying to move across the tile
     * @param sx The x coordinate of the tile we're moving from
     * @param sy The y coordinate of the tile we're moving from
     * @param sz The z coordinate of the tile we're moving from
     * @param tx The x coordinate of the tile we're moving to
     * @param ty The y coordinate of the tile we're moving to
     * @param tz The z coordinate of the tile we're moving to
     * @return The relative cost of moving across the given tile
     */
    float getCost(Entity ball, int sx, int sy, int sz, int tx, int ty, int tz);

}
