package data;

import retrofit.mime.TypedByteArray;

/**
 * Created by iran on 2015-09-20.
 */
public class PoinilaTypedByteArray extends TypedByteArray{
    /**
     * Constructs a new typed byte array.  Sets mimeType to {@code application/unknown} if absent.
     *
     * @param mimeType
     * @param bytes
     * @throws NullPointerException if bytes are null
     */
    public PoinilaTypedByteArray(String mimeType, byte[] bytes) {
        super(mimeType, bytes);
    }

    @Override
    public String fileName() {
        return "temp";
    }
}
