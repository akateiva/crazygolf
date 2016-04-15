import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
/**
 * Created by akateiva on 11/04/16.
 */
public class TextRenderer {
    class TextData {
        private String string;
        Vector2f position;


        /**
         * Create a new on-screen text object
         * @param string
         * @param position
         */
        TextData(String string, Vector2f position){
            this.string = string;
            this.position = position;
        }

        /**
         * In order to avoid re-creating the text meshes every time, only call this when the string of Text has changed
         */
        public void invalidate(){
            generateTextMesh();
        }
    }
    private int fontTexture;

    TextRenderer(){
        //Load the font

        //The bitmap texture size of the font that will be generated ( must be power of 2 )
        final int  BITMAP_W = 512, BITMAP_H = 512;

        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

        //Load the font
        ByteBuffer ttf = Util.resourceToByteBuffer("res/Arial.ttf");

        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W*BITMAP_H);
        //Create a texture based on the TTF file
        stbtt_BakeFontBitmap(ttf, 12, bitmap,BITMAP_W, BITMAP_H, 32, cdata);

        //Transfer the texture to video memory
        glBindTexture(GL_TEXTURE_2D, fontTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

    }

    /**
     * Generates mesh
     */
    private void generateTextMesh(){

    }

    public void update(long dt){

    }

    public void draw(){

    }
}
