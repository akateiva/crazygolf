import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by akateiva on 15/03/16.
 */
public class Mesh {
    /**
     * Create a Mesh object from an OBJ file
     * @param objSource
     */

    //obj_vertices stores the unique vertices, and vertices is the vertex buffer with repeating vertices (3 vertices for a triangle)
    ArrayList<Vector3f> obj_vertices;
    ArrayList<Vector3f> vertices;
    Mesh(String objSource){
        obj_vertices = new ArrayList<>();
        vertices = new ArrayList<>();
        Scanner objScanner = new Scanner(objSource);
        //Iterate through the input until no lines are left
        while(objScanner.hasNextLine()){
            String curLine = objScanner.nextLine();
            String parts[] = curLine.split("\\s+|\\/");
            if(parts[0].trim().equals("v") && parts.length == 4){
                obj_vertices.add(new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
            }
            if(parts[0].trim().equals("f") && parts.length == 10){
                for(int i = 1; i < 9; i+=3){
                    vertices.add(obj_vertices.get(Integer.parseInt(parts[i]) - 1));
                }
            }

        }
        System.out.println("Loaded " + obj_vertices.size() + " unique vertices.");
        System.out.println("Loaded " + vertices.size() / 3 + " unique faces.");
    }

    public FloatBuffer getFloatBuffer(){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.size()*3);
        for(int i = 0; i < vertices.size(); i++){
            buffer.put(vertices.get(i).x);
            buffer.put(vertices.get(i).y);
            buffer.put(vertices.get(i).z);
        }
        return buffer;
    }
}
