package world.naturecraft.townymission.core.components;

public class DataHolder<T> {

    private T data;

    public DataHolder(T data) {
        this.data = data;
    }

    public DataHolder() {}

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
