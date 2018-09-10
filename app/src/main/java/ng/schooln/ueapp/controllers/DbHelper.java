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

    public DatabaseReference staffref(String uid){
        return FirebaseDatabase.getInstance().getReference().child(variables.Appname).child(variables.Users).child(variables.Student).child(uid);
    }

    public StorageReference profilephoto(String id){
        return FirebaseStorage.getInstance().getReferenceFromUrl("gs://test-projects-5eebd.appspot.com").child("UEApp profile photo/"+ id);
    }

}
