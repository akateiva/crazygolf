package com.group9.crazygolf.phys;


import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * EntityStatic represents all the non-moving ( not balls ) entities in the world such as the terrain or any obstacles
 * The collision bounds of EntityStatic are represented as a mesh
 */
public class EntityStatic extends Entity {
    private float[] vertices;
    private short[] indices;
    private int VERTEX_SIZE; // How many elements in each vertex
    private Mesh mesh;

    public EntityStatic(ModelInstance modelInstance){
        super(modelInstance);

        mesh = modelInstance.model.meshes.first();
        VERTEX_SIZE = mesh.getVertexSize();

        vertices = new float[mesh.getNumVertices()*VERTEX_SIZE];
        mesh.getVertices(vertices);

        indices = new short[mesh.getNumIndices()];
        mesh.getIndices(indices);

        for(int i = 0; i < VERTEX_SIZE; i++){
            System.out.println(vertices[i]);
        }
    }


    public boolean intersectRayTriangle(Ray ray, int triangle, Vector3 intersection){
        return Intersector.intersectRayTriangle(ray, getVertexPosition(triangle*3), getVertexPosition(triangle*3 + 1), getVertexPosition(triangle*3 + 2) ,intersection);
    }

    public boolean intersectRay(Ray ray, Vector3 intersection, Vector3 )
}
