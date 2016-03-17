import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Util {
    /**
     * Load a file from disk into memory
     * @param path
     * @return the contents of a file in a ByteBuffer
     */
    public static ByteBuffer resourceToByteBuffer(String path) {
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();

            ByteBuffer buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);

            while (fc.read(buffer) != -1) ;

            fis.close();
            fc.close();
            return buffer;
        } catch (IOException e) {
            System.out.println("Can't load resource " + path);
            return null;
        }
    }

    /**
     * Load a file from disk into memory
     * @param path
     * @return the contents of a file in a String
     */
    public static String resourceToString(String path) {
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            return new String(data);
        } catch (IOException e) {
            System.out.println("Can't load resource " + path);
            return null;
        }
    }

}
