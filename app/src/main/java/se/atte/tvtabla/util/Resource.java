package se.atte.tvtabla.util;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import static se.atte.tvtabla.util.Resource.Status.SUCCESS;
import static se.atte.tvtabla.util.Resource.Status.ERROR;
import static se.atte.tvtabla.util.Resource.Status.LOADING;


public class Resource<T> {
    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;
    @Nullable
    public final Exception exception;

    /**
     * Status of a resource that is provided to the UI.
     * {@code LiveData<Resource<T>>} to pass back the latest data to the UI with its fetch status.
     */
    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message, @Nullable Exception exception) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.exception = exception;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(SUCCESS, data, null, null);
    }

    public static <T> Resource<T> success() {
        return new Resource<>(SUCCESS, null, null, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(ERROR, data, msg, null);
    }

    public static <T> Resource<T> error(Exception exception, String msg, @Nullable T data) {
        return new Resource<>(ERROR, data, msg, exception);
    }

    public static <T> Resource<T> loading(String msg, @Nullable T data) {
        return new Resource<>(LOADING, data, msg, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Resource)) {
            return false;
        }
        Resource<?> resource = (Resource<?>) o;
        return status == resource.status &&
                Objects.equals(data, resource.data) &&
                Objects.equals(message, resource.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, data, message);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", exception=" + exception +
                '}';
    }
}
