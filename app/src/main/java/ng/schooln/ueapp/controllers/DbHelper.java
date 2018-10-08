package ng.schooln.ueapp.controllers;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ng.schooln.ueapp.utils.Variables;

/**
 * Created by xyjoe on 9/8/18.
 */

public class DbHelper {
    private FirebaseAuth auth;
    private Context context;
    Variables variables = new Variables();

    public DbHelper(FirebaseAuth auth, Context context) {
        this.auth = auth;
        this.context = context;
    }

    public DatabaseReference studentref(String uid){
     return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Users).child(variables.Student).child(uid);
    }

    public DatabaseReference userref(){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Users);
    }

    public DatabaseReference staffref(String uid){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Users).child(variables.Staffs).child(uid);
    }

    public StorageReference profilephoto(String id){
        return FirebaseStorage.getInstance().getReferenceFromUrl("gs://test-projects-5eebd.appspot.com").child("UEApp profile photo/"+ id);
    }

    public DatabaseReference UnverifiedStaffs(String uid){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Users).child(variables.Unverifier).child(uid);
    }

    public DatabaseReference VerifiedStaffs(String uid){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Users).child(variables.Verified).child(uid);
    }

    public DatabaseReference stuentEmer(){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Studentemergencies);
    }

    public DatabaseReference stafemer(){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.StaffEmergencies);
    }

    public DatabaseReference staffdepartmentref(String dept){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Dept).child(dept).child("sta");
    }

    public DatabaseReference UsersCurrentLocation(){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Locations);
    }

    public DatabaseReference studentdepartmentref(String dept){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Dept).child(dept).child("stu");
    }

    public DatabaseReference Tagref (){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Tags);
    }


    public DatabaseReference Depthistorystaffref(String dept){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Dept).child(dept).child(variables.History).child(variables.Staffs);
    }

    public DatabaseReference Depthistorystudentref(String dept){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Dept).child(dept).child(variables.History).child(variables.Student);
    }

}
