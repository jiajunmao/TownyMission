package world.naturecraft.townymission.core.components;

/**
 * The type Data holder.
 *
 * @param <T> the type parameter
 */
public class DataHolder<T> {

    private T data;

    /**
     * Instantiates a new Data holder.
     *
     * @param data the data
     */
    public DataHolder(T data) {
        this.data = data;
    }

    /**
     * Instantiates a new Data holder.
     */
    public DataHolder() {}

    /**
     * Gets data.
     *
     * @return the data
     */
    public T getData() {
        return this.data;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(T data) {
        this.data = data;
    }
}
