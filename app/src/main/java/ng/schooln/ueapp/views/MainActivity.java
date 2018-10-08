package ng.schooln.ueapp.views;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
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
import ng.schooln.ueapp.utils.TextWatcherAdapter;
import ng.schooln.ueapp.utils.Variables;

public class MainActivity extends AppCompatActivity {
Variables variables;
private FirebaseAuth auth;
private  String picturepath, dept, faculty;
private RelativeLayout officelay, levellay, usertypelay;
private EditText office;
private Spinner levelspinner, officespin;
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
        Typeface custom = Typeface.createFromAsset(getAssets(),  "fonts/Lato-Regular.ttf");
        office = findViewById(R.id.office);
        officebtn = findViewById(R.id.officebtn);
        officelay = findViewById(R.id.officelay);
        levelspinner = findViewById(R.id.deptspinner);
        officespin = findViewById(R.id.officespin);
        levellay = findViewById(R.id.levellay);
        levelbtn = findViewById(R.id.levelbtn);
        dept = getIntent().getStringExtra("dept");
        faculty = getIntent().getStringExtra("faculty");
        usertypelay = findViewById(R.id.main1);
        office.setTypeface(custom);
        levelbtn.setTypeface(custom);
        officespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemPosition() != 0){
                    officebtn.setVisibility(View.VISIBLE);
                    office.setVisibility(View.GONE);
                }
                if (adapterView.getSelectedItemPosition() == 4){
                    office.setVisibility(View.VISIBLE);
                    if (office.getText().equals("")){
                        officebtn.setVisibility(View.GONE);
                    }else {
                        officebtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        office.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0){
                    officebtn.setVisibility(View.VISIBLE);
                }else {
                    officebtn.setVisibility(View.GONE);
                }
            }
        });

    }

    public void Student(View v){
       usertypelay.setVisibility(View.GONE);
       levellay.setVisibility(View.VISIBLE);
       officelay.setVisibility(View.GONE);

    }

    public void Staff(View v){
        usertypelay.setVisibility(View.GONE);
        levellay.setVisibility(View.GONE);
        officelay.setVisibility(View.VISIBLE);
    }
    public void createStudent(View v){
        if (auth.getCurrentUser() != null){
            new Controls(auth).createAccount(this,null,picturepath,dept,faculty,levelspinner.getSelectedItem().toString());
        }

    }

    public void CreateStaff(View v){
        if (auth.getCurrentUser() != null){
            if (officespin.getSelectedItemPosition() != 0 && !office.getText().toString().equals("")){
                new Controls(auth).createAccount(this,office.getText().toString(),picturepath,dept,faculty,levelspinner.getSelectedItem().toString());
            }else if (officespin.getSelectedItemPosition() != 0){
                new Controls(auth).createAccount(this,officespin.getSelectedItem().toString(),picturepath,dept,faculty,levelspinner.getSelectedItem().toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (officelay.getVisibility() == View.VISIBLE){
            usertypelay.setVisibility(View.VISIBLE);
            levellay.setVisibility(View.GONE);
            officelay.setVisibility(View.GONE);
        }
        if (levellay.getVisibility() == View.VISIBLE){
            usertypelay.setVisibility(View.VISIBLE);
            levellay.setVisibility(View.GONE);
            officelay.setVisibility(View.GONE);
        }

    }
}
