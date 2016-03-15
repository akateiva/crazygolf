import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Created by akateiva on 14/03/16.
 */
public class EntityTerrain extends EntityPlainDrawable {
    EntityTerrain(){
        super(new Mesh(Util.resourceToString("terrain.obj")));
    }
}
