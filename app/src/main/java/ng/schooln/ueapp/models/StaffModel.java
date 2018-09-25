package ng.schooln.ueapp.models;

import java.io.Serializable;

/**
 * Created by xyjoe on 9/8/18.
 */

public class StaffModel implements Serializable {
    private String id, nam, dept, img, schl, office, email;

    public String getSchl() {
        return schl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSchl(String schl) {
        this.schl = schl;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public StaffModel(String id, String nam, String dept,  String img, String schl, String office, String email) {
        this.nam = nam;
        this.email = email;
        this.office = office;
        this.dept = dept;
        this.schl = schl;
        this.id = id;
        this.img = img;
    }

    public StaffModel() {
    }

    public String getNam() {
        return nam;
    }

    public void setNam(String nam) {
        this.nam = nam;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
