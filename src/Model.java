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
    private ArrayList<Float> vboData;
    private int triangleCount;

    private FloatBuffer vboBuffer;
    /**
     * Create a model object from an OBJ file (source, not path)
     * @param modelSource
     */
    Model(String modelSource){
        Scanner scanner = new Scanner(modelSource);

        vertices = new ArrayList<>();
        normals = new ArrayList<>();
        uvCoords = new ArrayList<>();
        vboData = new ArrayList<>();

        triangleCount = 0;

        while(scanner.hasNextLine()) {
            String curLine = scanner.nextLine();
            String parts[] = curLine.split("\\s+|\\/");

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

                        vboData.add(vertices.get(vertexIndex*3 + 0));
                        vboData.add(vertices.get(vertexIndex*3 + 1));
                        vboData.add(vertices.get(vertexIndex*3 + 2));

                        int normalIndex = Integer.parseInt(parts[i+2]) - 1;

                        vboData.add(normals.get(normalIndex*3 + 0));
                        vboData.add(normals.get(normalIndex*3 + 1));
                        vboData.add(normals.get(normalIndex*3 + 2));


                        if(parts[i+1].isEmpty()){
                            //In case the OBJ file does not have UV mapping
                            vboData.add(0.f);
                            vboData.add(0.f);
                        }else {
                            int uvIndex = Integer.parseInt(parts[i + 1]) - 1;

                            vboData.add(uvCoords.get(uvIndex * 2 + 0));
                            vboData.add(uvCoords.get(uvIndex * 2 + 1));
                        }

                        triangleCount++;
                    }
                    break;

            }
        }
        vboBuffer = BufferUtils.createFloatBuffer(vboData.size());

        for(Float f : vboData){
            vboBuffer.put(f);
        }

        vboBuffer.flip();
    }

    public FloatBuffer getVboBuffer() {
        return vboBuffer;
    }

    public int getTriangleCount() {
        return triangleCount;
    }
}
