import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by akateiva on 14/04/16.
 */
public class TextureManager{
    Map<String, Integer> textures;
    TextureManager() {
        textures = new HashMap<>();
    }

    /**
     * Create a new texture
     * @param name The name this texture will be keep
     * @param image ByteBuffer with the image ( @see Util )
     */
    public void createTexture(String name, String shaderName, ByteBuffer image){

    }

}
