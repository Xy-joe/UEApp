package ng.schooln.ueapp.models;

/**
 * Created by xyjoe on 9/16/18.
 */

public class LocationModel {
    String id;
    double lat, lon;

    public LocationModel() {
    }

    public LocationModel(String id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
