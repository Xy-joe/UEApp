package ng.schooln.ueapp.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.controllers.Controls;
import ng.schooln.ueapp.controllers.DbHelper;
import ng.schooln.ueapp.utils.Variables;

public class SignUpOptions extends AppCompatActivity {

    private Variables variables;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mauthStateListener;
    private LinearLayout accountotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_options);

        variables = new Variables();
        accountotion = findViewById(R.id.accountoption);

         mauthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){

                    new DbHelper(firebaseAuth, SignUpOptions.this).studentref(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(checkstudentcredentials(firebaseAuth, mauthStateListener));
                }
            }
        };
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(mauthStateListener);


    }
    public void createaccount(View v){
        startActivity(new Intent(this, Register.class));
    }

    public void Login(View v){
        startActivity(new Intent(this, LoginActivity.class));
    }

    private ValueEventListener checkstudentcredentials(final FirebaseAuth auth, final FirebaseAuth.AuthStateListener authStateListener){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    new Controls(auth).gotohomepage(SignUpOptions.this, null, variables.Student);
                }else {
                    new DbHelper(auth, SignUpOptions.this).staffref(auth.getCurrentUser().getUid()).addValueEventListener(checkStaffCredentials(auth, authStateListener));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener checkStaffCredentials(final FirebaseAuth auth, final FirebaseAuth.AuthStateListener authStateListener){
        return  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    new Controls(auth).gotohomepage(SignUpOptions.this, variables.Staffs, null);
                }else {
                    auth.removeAuthStateListener(authStateListener);
                    accountotion.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
