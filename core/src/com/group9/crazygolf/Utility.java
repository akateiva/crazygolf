package com.group9.crazygolf;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.group9.crazygolf.entities.components.MeshColliderComponent;

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

public class Utility {

    public static MeshColliderComponent createMeshColliderComponent(Mesh mesh, Matrix4 transform) {
        MeshColliderComponent meshColliderComponent = new MeshColliderComponent();
        meshColliderComponent.vertPosition = new Vector3[mesh.getNumIndices()];
        meshColliderComponent.vertNormal = new Vector3[mesh.getNumIndices()];


        int POSITION_OFFSET = mesh.getVertexAttribute(VertexAttributes.Usage.Position).offset / 4;
        int NORMAL_OFFSET = mesh.getVertexAttribute(VertexAttributes.Usage.Normal).offset / 4;
        int VERTEX_SIZE = mesh.getVertexSize() / 4;

        float[] vertices = new float[mesh.getNumVertices() * VERTEX_SIZE];
        short[] indices = new short[mesh.getNumIndices()];

        mesh.getVertices(vertices);
        mesh.getIndices(indices);


        for (int i = 0; i < meshColliderComponent.vertPosition.length; i++) {
            meshColliderComponent.vertPosition[i] = new Vector3(
                    vertices[indices[i] * VERTEX_SIZE + POSITION_OFFSET],
                    vertices[indices[i] * VERTEX_SIZE + POSITION_OFFSET + 1],
                    vertices[indices[i] * VERTEX_SIZE + POSITION_OFFSET + 2]
            );
            meshColliderComponent.vertNormal[i] = new Vector3(
                    vertices[indices[i] * VERTEX_SIZE + NORMAL_OFFSET],
                    vertices[indices[i] * VERTEX_SIZE + NORMAL_OFFSET + 1],
                    vertices[indices[i] * VERTEX_SIZE + NORMAL_OFFSET + 2]
            );
        }

        meshColliderComponent.trainvtransform = transform.cpy().inv().tra();

        return meshColliderComponent;
    }
}
