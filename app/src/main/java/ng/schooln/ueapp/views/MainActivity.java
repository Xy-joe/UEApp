package ng.schooln.ueapp.views;

import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.controllers.Controls;
import ng.schooln.ueapp.models.StudentModel;
import ng.schooln.ueapp.utils.Variables;

public class MainActivity extends AppCompatActivity {
Variables variables;
private FirebaseAuth auth;
private  String picturepath, dept, faculty;
private RelativeLayout officelay, levellay, usertypelay;
private EditText office;
private Spinner levelspinner;
private Button levelbtn, officebtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        variables = new Variables();
        auth =  FirebaseAuth.getInstance();
        picturepath = getIntent().getStringExtra("path");
    }

    private void init(){
        office = findViewById(R.id.office);
        officebtn = findViewById(R.id.officebtn);
        officelay = findViewById(R.id.officelay);
        levelspinner = findViewById(R.id.deptspinner);
        levellay = findViewById(R.id.levellay);
        dept = getIntent().getStringExtra("dept");
        faculty = getIntent().getStringExtra("faculty");
        usertypelay = findViewById(R.id.main1);
        levelspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemPosition() != 0){

                    levelbtn.setVisibility(View.VISIBLE);
                }else {
                    levelbtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void Student(View v){
       usertypelay.setVisibility(View.GONE);
       levellay.setVisibility(View.VISIBLE);
       officelay.setVisibility(View.GONE);

    }

    public void Staff(View v){
        Intent intent = new Intent(this, SchoolSelect.class);
        intent.putExtra(variables.Staffs, variables.Staffs);
        if (picturepath != null){
            intent.putExtra("path", picturepath);
        }

        startActivity(intent);
    }
    public void createStudent(View v){
        if (auth.getCurrentUser() != null){
            new Controls(auth).createAccount(this,variables.Student,null,picturepath,dept,faculty,levelspinner.getSelectedItem().toString());
        }

    }

    public void CreateStaff(View v){

    }
}
