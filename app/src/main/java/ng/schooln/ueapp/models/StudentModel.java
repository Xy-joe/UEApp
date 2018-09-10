package ng.schooln.ueapp.models;

import java.io.Serializable;

/**
 * Created by xyjoe on 9/8/18.
 */

public class StudentModel implements Serializable {
    private String id,nam, dept, lvl, img, schl;

    public String getSchl() {
        return schl;
    }

    public void setSchl(String schl) {
        this.schl = schl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StudentModel(String id, String nam, String dept, String lvl, String img, String schl) {
        this.nam = nam;
        this.dept = dept;
        this.schl = schl;
        this.lvl = lvl;
        this.id = id;
        this.img = img;
    }

    public StudentModel() {
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

    public String getLvl() {
        return lvl;
    }

    public void setLvl(String lvl) {
        this.lvl = lvl;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
