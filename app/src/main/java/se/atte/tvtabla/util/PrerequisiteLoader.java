package se.atte.tvtabla.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for handling waiting for multiple {@link LiveData} to have fetched data.
 * The live data is generic except it must be a live data with a {@link Resource} object,
 * e.g. {@link LiveData<Resource<?>>}.
 */
public class PrerequisiteLoader {

    private Map<String, Object> liveDataResult = new HashMap<>();
    private List<Prerequisite>  prerequisites  = new ArrayList<>();

    /**
     * Adds a prerequisite to the list of prerequisites.
     *
     * @param tag      The tag associated with the live data.
     * @param liveData The live data required to have loaded data.
     */
    public void addPrerequisite(String tag, LiveData<Resource<?>> liveData) {
        liveDataResult.put(tag, null);
        prerequisites.add(new Prerequisite(tag, liveData));
    }

    /**
     * Initiates the loading of the prerequisites.
     */
    public LiveData<Resource<Void>> load() {
        MediatorLiveData<Resource<Void>> result = new MediatorLiveData<>();

        if (isDone()) {
            result.postValue(Resource.success());
        } else {
            for (Prerequisite prerequisite : prerequisites) {
                result.addSource(prerequisite.liveData, resource -> {
                    if (resource == null) {
                        return;
                    }

                    switch (resource.status) {
                        case LOADING:
                            result.postValue(Resource.loading(resource.message, null));
                            break;
                        case SUCCESS:
                            result.removeSource(prerequisite.liveData);
                            if (resource.data == null) {
                                result.postValue(Resource.error("data must not be null", null));
                            } else {
                                liveDataResult.put(prerequisite.tag, resource.data);
                            }
                            break;
                        case ERROR:
                            result.removeSource(prerequisite.liveData);
                            result.postValue(Resource.error(resource.exception, resource.message, null));
                            break;
                    }

                    if (isDone()) {
                        result.postValue(Resource.success());
                    }
                });
            }
        }

        return result;
    }

    /**
     * Returns the resource data value result from a prerequisite live data.
     *
     * @param tag The tag associated with the prerequisite. See {@link #addPrerequisite(String, LiveData)}.
     * @param <T> The value type.
     */
    public <T> T getLiveDataResult(String tag) {
        return (T) liveDataResult.get(tag);
    }

    /**
     * Returns <code>true</code> when all prerequisites have loaded.
     */
    private boolean isDone() {
        for (Object value : liveDataResult.values()) {
            if (value == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Internal class for holding info about a prerequisite.
     */
    private static class Prerequisite {
        String                tag;
        LiveData<Resource<?>> liveData;

        public Prerequisite(String tag, LiveData<Resource<?>> liveData) {
            this.tag = tag;
            this.liveData = liveData;
        }
    }
}

