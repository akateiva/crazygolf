import org.joml.Vector2f;

/**
 * Created by akateiva on 14/04/16.
 */
public class Text {
    private String string;
    Vector2f position;


    /**
     * Create a new on-screen text object
     * @param string
     * @param position
     */
    Text(String string, Vector2f position){
        this.string = string;
        this.position = position;
    }

    /**
     * In order to avoid re-creating the text meshes every time, only call this when the string of Text has changed
     */
    public void invalidate(){

    }
}
