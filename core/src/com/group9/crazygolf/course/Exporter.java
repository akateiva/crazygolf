package com.group9.crazygolf.course;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by akateiva on 25/05/16.
 */
public class Exporter {
    Vector3 startPosition;
    Vector3 endPosition;
    Mesh terrain;

    public Exporter() {
    }

    public void setStartPosition(Vector3 startPosition) {
        this.startPosition = startPosition;
    }

    public void setEndPosition(Vector3 endPosition) {
        this.endPosition = endPosition;
    }

    public void setTerrainMesh(Mesh terrain) {
        this.terrain = terrain;
    }

    public void export() {
        Gdx.files.local("courses/course").writeString("shit, fuck", false);
    }
}
