import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by akateiva on 13/04/16.
 */
public class Model {
    private ArrayList<Float> vertices;
    private ArrayList<Float> normals;
    private ArrayList<Float> uvCoords;
    private ArrayList<Float> vbo_data;

    private FloatBuffer vbo_buffer;
    /**
     * Create a model object from an OBJ file (source, not path)
     * @param modelSource
     */
    Model(String modelSource){
        Scanner scanner = new Scanner(modelSource);

        vertices = new ArrayList<>();
        normals = new ArrayList<>();
        uvCoords = new ArrayList<>();
        vbo_data = new ArrayList<>();



        while(scanner.hasNextLine()) {
            String curLine = scanner.nextLine();
            String parts[] = curLine.split("\\s+|\\/");

            //This line of the OBJ file describes a vertex and its position
            if(parts[0].trim().equals("v") && parts.length == 4){
                vertices.add(Float.parseFloat(parts[1]));
            }
            switch(parts[0].trim().toLowerCase()){
                //Vertex position
                case "v":
                    vertices.add(Float.parseFloat(parts[1]));
                    vertices.add(Float.parseFloat(parts[2]));
                    vertices.add(Float.parseFloat(parts[3]));
                    break;
                //Vertex normal
                case "vn":
                    normals.add(Float.parseFloat(parts[1]));
                    normals.add(Float.parseFloat(parts[2]));
                    normals.add(Float.parseFloat(parts[3]));
                    break;
                //UV coordinates
                case "vt":
                    uvCoords.add(Float.parseFloat(parts[1]));
                    uvCoords.add(Float.parseFloat(parts[2]));
                    break;
                //Faces
                case "f":
                    //The elements of f are as follows:
                    //1 : vertex index
                    //2 : texture coordinate index
                    //3 : normal index
                    for(int i = 1; i < 9; i+=3){
                        int vertexIndex = Integer.parseInt(parts[i]) - 1;
                        int uvIndex = Integer.parseInt(parts[i] + 1) - 1;
                        int normalIndex = Integer.parseInt(parts[i] + 2) - 1;

                        vbo_data.add(vertices.get(vertexIndex*3 + 0));
                        vbo_data.add(vertices.get(vertexIndex*3 + 1));
                        vbo_data.add(vertices.get(vertexIndex*3 + 2));

                        vbo_data.add(uvCoords.get(uvIndex*2 + 0));
                        vbo_data.add(uvCoords.get(uvIndex*2 + 1));

                        vbo_data.add(normals.get(normalIndex*3 + 0));
                        vbo_data.add(normals.get(normalIndex*3 + 2));
                        vbo_data.add(normals.get(normalIndex*3 + 2));
                    }
                    break;

            }
        }
        vbo_buffer = BufferUtils.createFloatBuffer(vbo_data.size());

        for(Float f : vbo_data){
            vbo_buffer.put(f);
        }

        vbo_buffer.flip();
    }

    public FloatBuffer getVbo_buffer() {
        return vbo_buffer;
    }
}
