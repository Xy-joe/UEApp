package ng.schooln.ueapp.models;

/**
 * Created by xyjoe on 9/16/18.
 */

public class Alert {
    String usertype, decrp, tag, userid;
    double latitude, longitude;
    long time;

    public Alert(String usertype, long time, String decrp, String tag, String userid, double latitude, double longitude) {
        this.usertype = usertype;
        this.time = time;
        this.decrp = decrp;
        this.tag = tag;
        this.userid = userid;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Alert() {
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDecrp() {
        return decrp;
    }

    public void setDecrp(String decrp) {
        this.decrp = decrp;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
