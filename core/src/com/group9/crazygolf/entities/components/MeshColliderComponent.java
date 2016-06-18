package com.group9.crazygolf.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

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
public class MeshColliderComponent implements Component {
    //Store the vertex positions and normals for every triangle
    public Vector3[] vertPosition;
    public Vector3[] vertNormal;

    public Matrix4 trainvtransform; // transposed inverted transform for normal manipulation

}
