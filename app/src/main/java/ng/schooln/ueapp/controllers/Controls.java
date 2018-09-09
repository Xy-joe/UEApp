package ng.schooln.ueapp.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ng.schooln.ueapp.models.StudentModel;
import ng.schooln.ueapp.utils.Variables;
import ng.schooln.ueapp.views.Homepage;
import ng.schooln.ueapp.views.MainActivity;
import ng.schooln.ueapp.views.SchoolSelect;

/**
 * Created by xyjoe on 9/8/18.
 */

public class Controls {
    private FirebaseAuth auth;
    private Variables variables = new Variables();
    private DbHelper dbHelper;

    public Controls(FirebaseAuth auth) {
        this.auth = auth;
    }

    public void Login(final Context context, String email, String password){
        if (auth != null){
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        dbHelper = new DbHelper(auth, context);
                        dbHelper.studentref(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null){
                                    gotohomepage(context, null, variables.Student);
                                }else {
                                    dbHelper.staffref(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null){
                                                gotohomepage(context,variables.Staffs, null);
                                            }else {
                                                context.startActivity(new Intent(context, SchoolSelect.class));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(context, "A user with this email already exists", Toast.LENGTH_LONG).show();
                    } else if ( e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "The email you entered does not exist", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(context, "Invalid email or password", Toast.LENGTH_LONG).show();
                    }
                    e.printStackTrace();
                }
            });
        }
    }

    public void SignUp(final Context context, final String uname, String email, String password){
        if (auth != null){
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(uname)
                                        .build();

                                auth.getCurrentUser().updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        context.startActivity(new Intent(context, SchoolSelect.class));
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (e instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(context, "A user with this email already exists", Toast.LENGTH_LONG).show();
                                } else if ( e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(context, "The email you entered does not exist", Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(context, "Something went wrong, could not verify your email address", Toast.LENGTH_LONG).show();
                                }
                                e.printStackTrace();
                            }
                        });





                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(context, "A user with this email already exists", Toast.LENGTH_LONG).show();
                    } else if ( e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "The email you entered does not exist", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(context, "Internet Connection failed, could not create your account", Toast.LENGTH_LONG).show();
                    }
                    e.printStackTrace();
                }
            });
        }
    }

    public void gotohomepage(Context context, String staff, String student){
        Intent intent = new Intent(context, Homepage.class);
        intent.putExtra(variables.Staffs, staff);
        intent.putExtra(variables.Student, student);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void createAccount(Context context, String student, String staff, Bitmap bitmap, String dept, String faculty, String level){
        dbHelper = new DbHelper(auth, context);
        if (student != null){
            StudentModel studentModel = new StudentModel(auth.getCurrentUser().getDisplayName(), dept, level, auth.getCurrentUser().getPhotoUrl().toString(), faculty);
            dbHelper.studentref(auth.getCurrentUser().getUid()).setValue(studentModel);
        }
    }
}
