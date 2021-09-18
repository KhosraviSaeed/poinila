package data.event;

/**
 * Created by iran on 2015-07-07.
 */
public class CacheEvent<T> {
    private final T data;

    public CacheEvent(T data) {
        this.data = data;
    }

    public T getData(){
        return this.data;
    }
}
