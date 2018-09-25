package ng.schooln.ueapp.views;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.controllers.DbHelper;
import ng.schooln.ueapp.models.StaffModel;
import ng.schooln.ueapp.models.StudentModel;
import ng.schooln.ueapp.utils.Variables;

public class Userprofile extends AppCompatActivity {

    private ImageView userimage;
    private TextView name, email, dept, other, oth;
    private DbHelper dbHelper;
    private FirebaseAuth auth;
    Variables variables;
    private String usertype, myId;
    private StaffModel staffModel;
    private StudentModel studentModel;
    private FloatingActionButton fab;
    private Menu mainmenu;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialise();
    }

    private void initialise() {
        variables = new Variables();
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Lato-Black.ttf");
        auth = FirebaseAuth.getInstance();
        dbHelper = new DbHelper(auth, this);
        name = findViewById(R.id.name);
        userimage = findViewById(R.id.userimg);
        email = findViewById(R.id.emal);
        oth = findViewById(R.id.oth);
        other = findViewById(R.id.other);
        dept = findViewById(R.id.dept);
        fab = findViewById(R.id.editp);
        usertype = getIntent().getStringExtra(variables.Users);
        name.setTypeface(custom_font);
        myId = getIntent().getStringExtra(variables.ID);
        if (!myId.equals(auth.getCurrentUser().getUid())){
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Userprofile.this, EditProfile.class);
                intent.putExtra("usertype", usertype);
                startActivity(intent);
            }
        });

        setUptheViews();
    }

    private void setUptheViews() {
        dbHelper.userref().child(usertype).child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null ){
                    if (usertype.equals(variables.Staffs)){
                        oth.setText(getString(R.string.office));
                        staffModel = dataSnapshot.getValue(StaffModel.class);
                        name.setText(staffModel.getNam());
                        other.setText(staffModel.getOffice());
                        dept.setText(staffModel.getDept());
                        email.setText(staffModel.getEmail());
                        Glide.with(Userprofile.this).load(staffModel.getImg()).into(userimage);

                    }else {
                        oth.setText(getString(R.string.level));
                        studentModel = dataSnapshot.getValue(StudentModel.class);
                        other.setText(studentModel.getLvl());
                        name.setText(studentModel.getNam());
                        dept.setText(studentModel.getDept());
                        email.setText(studentModel.getEmail());
                        Glide.with(Userprofile.this).load(studentModel.getImg()).into(userimage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        mainmenu = menu;
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save){
            logoutnow();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutnow() {
        auth.signOut();
        Intent intent = new Intent(Userprofile.this, SignUpOptions.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
