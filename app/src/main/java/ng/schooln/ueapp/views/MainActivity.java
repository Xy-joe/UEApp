package ng.schooln.ueapp.views;

import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.controllers.Controls;
import ng.schooln.ueapp.utils.Variables;

public class MainActivity extends AppCompatActivity {
Variables variables;
private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        variables = new Variables();
        auth =  FirebaseAuth.getInstance();
    }

    public void Student(View v){
        new Controls(auth).gotohomepage(this,null, variables.Student);
    }

    public void Staff(View v){
        Intent intent = new Intent(this, SchoolSelect.class);
        intent.putExtra(variables.Staffs, variables.Staffs);
        startActivity(intent);
    }
}
