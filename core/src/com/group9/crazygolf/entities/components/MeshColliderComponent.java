package com.group9.crazygolf.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by akateiva on 08/05/16.
 */
public class MeshColliderComponent implements Component {
    //Store the vertex positions and normals for every triangle
    public Vector3[] vertPosition;
    public Vector3[] vertNormal;
}
