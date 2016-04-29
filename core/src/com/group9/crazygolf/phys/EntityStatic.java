package com.group9.crazygolf.phys;


import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
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
    private int VERTEX_SIZE;
    private int TRIANGLE_COUNT;
    private int NORMAL_OFFSET;

    private Mesh mesh;

    /**
     * @param modelInstance the model which will be attached to this Entity
     */
    public EntityStatic(ModelInstance modelInstance){
        super(modelInstance);

        mesh = modelInstance.model.meshes.first();

        //The size of each vertex is returned in bytes
        //We can assume that all vertex data is passed as a float
        //Therefore we divide the size in bytes by 4, because float takes up 4 bytes of memory
        VERTEX_SIZE = mesh.getVertexSize() / 4;

        //3 indices are required to construct a triangle
        TRIANGLE_COUNT = mesh.getNumIndices() / 3;

        NORMAL_OFFSET = mesh.getVertexAttribute(VertexAttributes.Usage.Normal).offset / 4;

        vertices = new float[mesh.getNumVertices()*VERTEX_SIZE];
        mesh.getVertices(vertices);

        indices = new short[mesh.getNumIndices()];
        mesh.getIndices(indices);
    }

    /**
     * Check whether a ray intersects a triangle of this entity.
     *
     * @param ray          the ray which will be casted
     * @param triangle     the index of the triangle of which to check against
     * @param intersection the closest vector of the intersection
     * @return true if ray is obstructed by triangle, false if not
     */
    public boolean intersectRayTriangle(Ray ray, int triangle, Vector3 intersection) {
        return Intersector.intersectRayTriangle(ray,
                getVertexPosition(triangle * 3),
                getVertexPosition(triangle * 3 + 1),
                getVertexPosition(triangle * 3 + 2),
                intersection
        );
    }

    /**
     * @return the number of triangles in this Entity's mesh
     */
    public int getTriangleCount() {
        return TRIANGLE_COUNT;
    }

    /**
     * Get a vertex normal from this Entity's mesh
     *
     * @param index the index of the vertex
     * @return the normal of the vertex
     */
    public Vector3 getVertexNormal(int index) {
        return new Vector3(vertices[indices[index] * VERTEX_SIZE + NORMAL_OFFSET],
                vertices[indices[index] * VERTEX_SIZE + NORMAL_OFFSET + 1],
                vertices[indices[index] * VERTEX_SIZE + NORMAL_OFFSET + 2]);
    }

    /**
     * Get a vertex position from this Entity's mesh
     *
     * @param index the index of the vertex
     * @return the position of the vertex
     */
    public Vector3 getVertexPosition(int index) {
        return new Vector3(vertices[indices[index] * VERTEX_SIZE],
                vertices[indices[index] * VERTEX_SIZE + 1],
                vertices[indices[index] * VERTEX_SIZE + 2]).add(getPosition());
    }


}
