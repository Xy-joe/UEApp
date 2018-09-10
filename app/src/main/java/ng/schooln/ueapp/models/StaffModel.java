package ng.schooln.ueapp.models;

import java.io.Serializable;

/**
 * Created by xyjoe on 9/8/18.
 */

public class StudentModel implements Serializable {
    private String nam, dept, lvl, img, schl;

    public StudentModel(String nam, String dept, String lvl, String img, String schl) {
        this.nam = nam;
        this.dept = dept;
        this.schl = schl;
        this.lvl = lvl;
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
