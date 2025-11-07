package maptools;

import android.location.Location;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Singleton repository that holds LiveData for current location and tracking state.
 * Service writes to it; Fragment observes it.
 */
public class LocationRepository {
    private static LocationRepository instance;
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isTrackingActive = new MutableLiveData<>(false);

    private LocationRepository() {}

    public static synchronized LocationRepository getInstance() {
        if (instance == null) instance = new LocationRepository();
        return instance;
    }

    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public LiveData<Boolean> getIsTrackingActive() {
        return isTrackingActive;
    }

    public void setCurrentLocation(Location location) {
        currentLocation.postValue(location);
    }

    public void setTrackingActive(boolean active) {
        isTrackingActive.postValue(active);
    }
}
