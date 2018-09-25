package ng.schooln.ueapp.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.models.Alert;
import ng.schooln.ueapp.models.StaffModel;
import ng.schooln.ueapp.models.StudentModel;
import ng.schooln.ueapp.utils.Connectivity;
import ng.schooln.ueapp.utils.Variables;
import ng.schooln.ueapp.views.Homepage;
import ng.schooln.ueapp.views.LoginActivity;
import ng.schooln.ueapp.views.SchoolSelect;
import ng.schooln.ueapp.views.dialog.EmergencyText;

/**
 * Created by xyjoe on 9/8/18.
 */

public class Controls {
    private FirebaseAuth auth;
    private Variables variables = new Variables();
    private DbHelper dbHelper;
    private ProgressDialog progressDialog;
    private  String LoggedIn_User_ID;

    public Controls(FirebaseAuth auth) {
        this.auth = auth;
    }

    public void Login(final Context context, String email, String password){
        dbHelper = new DbHelper(auth, context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.pleasewait));
        if (Connectivity.isConnected(context)){
            if (auth != null){
                progressDialog.show();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            dbHelper.studentref(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null){
                                        progressDialog.dismiss();
                                        gotohomepage(context, null, variables.Student);
                                    }else {
                                        dbHelper.VerifiedStaffs(auth.getCurrentUser().getUid()).getRef().addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() != null){
                                                    progressDialog.dismiss();
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                    builder.setCancelable(false);
                                                    builder.setTitle("Verified Account");
                                                    builder.setMessage(context.getString(R.string.sucessregistext));
                                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            finaliseStaff(context, progressDialog);
                                                        }
                                                    });
                                                    AlertDialog alert = builder.create();
                                                    alert.show();
                                                }else {
                                                    dbHelper.UnverifiedStaffs(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.getValue() != null){
                                                                progressDialog.dismiss();
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                builder.setCancelable(false);
                                                                builder.setTitle("Unverified Account");
                                                                builder.setMessage(context.getString(R.string.unconfirmedaccount));
                                                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        Intent intent = new Intent(context, LoginActivity.class);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        context.startActivity(intent);
                                                                    }
                                                                });
                                                                AlertDialog alert = builder.create();
                                                                alert.show();

                                                            }else {
                                                                dbHelper.staffref(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.getValue() != null){
                                                                            progressDialog.dismiss();
                                                                            gotohomepage(context,variables.Staffs, null);
                                                                        }else {
                                                                            progressDialog.dismiss();
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
                        progressDialog.dismiss();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(context, "A user with this email already exists", Toast.LENGTH_LONG).show();
                        } else if ( e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(context, "The email or password you entered is incorrect", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_LONG).show();
                        }
                        e.printStackTrace();
                    }
                });
            }
        }else {
            Toast.makeText(context, "Internet Connection failed", Toast.LENGTH_LONG).show();

        }

    }

    //Complete the creation on verified staff Account
    private void finaliseStaff(final Context context, final ProgressDialog progressDialog){
        progressDialog.show();
        dbHelper = new DbHelper(auth,context);
       dbHelper.UnverifiedStaffs(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
               if (dataSnapshot.getValue() != null){
                   final StaffModel staffModel = dataSnapshot.getValue(StaffModel.class);
                   dbHelper.staffref(auth.getCurrentUser().getUid()).setValue(staffModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               dbHelper.staffdepartmentref(staffModel.getDept()).child(auth.getCurrentUser().getUid()).child(variables.ID).setValue(auth.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       dbHelper.UnverifiedStaffs(auth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               if (dataSnapshot.getValue() != null){
                                                   dbHelper.VerifiedStaffs(auth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                       @Override
                                                       public void onSuccess(Void aVoid) {
                                                           if ((progressDialog != null) && progressDialog.isShowing()) {
                                                               progressDialog.dismiss();
                                                           }
                                                           gotohomepage(context,variables.Staffs, null);
                                                       }
                                                   });
                                               }
                                           }
                                       });
                                   }
                               });

                           }else {
                               if ((progressDialog != null) && progressDialog.isShowing()) {
                                   progressDialog.dismiss();
                               }
                               Toast.makeText(context, "Internet Connection failed, could not create your account", Toast.LENGTH_LONG).show();
                           }
                       }
                   });
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

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

    public void createAccount(final Context context, final String office, String picturepath, final String dept, final String faculty, final String level){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.pleasewait));
        dbHelper = new DbHelper(auth, context);
        if (Connectivity.isConnected(context)){
            progressDialog.show();
            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    if (userId == null ){
                        OneSignal.startInit(context)
                                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                                .unsubscribeWhenNotificationsAreDisabled(true)
                                .init();

                    }else {
                        LoggedIn_User_ID = auth.getCurrentUser().getUid();
                        OneSignal.sendTag(variables.ID, LoggedIn_User_ID);
                        OneSignal.syncHashedEmail(auth.getCurrentUser().getEmail());

                    }

                }
            });
            if (picturepath != null){
                Bitmap bitmap = decodeSampledBitmapFromResource(picturepath, 500, 600);
                Locale locale = new Locale("yyMMddHHmmss");

                DateFormat df = new SimpleDateFormat("yyMMddHHmmss", locale);
                Date dateobj = new Date();
                final String mydownloadUrl = df.format(dateobj) + "_" + auth.getCurrentUser().getUid() + ".jpg";

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = dbHelper.profilephoto(mydownloadUrl).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        if ((progressDialog != null) && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        exception.printStackTrace();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        dbHelper.profilephoto(mydownloadUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final Uri imageurl =  uri;

                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(imageurl).build();
                                auth.getCurrentUser().updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        proceed(office,context,dept,faculty,level);
                                    }
                                });
                            }
                        });

                    }
                });

            }else {
                proceed(office,context,dept,faculty,level);
            }
        }else {
            Toast.makeText(context, "Internet Connection failed", Toast.LENGTH_LONG).show();
        }
    }

    private void showConfirmationNotice(final Context context, String uname) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Registration Notice!");
        builder.setMessage(uname+", "+context.getString(R.string.call_later));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void proceed(String office, final Context context, final String dept, String faculty, String level){
        if (office == null){
            final StudentModel studentModel = new StudentModel(auth.getCurrentUser().getUid(),auth.getCurrentUser().getDisplayName(), dept, level, String.valueOf(auth.getCurrentUser().getPhotoUrl()), faculty, auth.getCurrentUser().getEmail());
            dbHelper.studentref(auth.getCurrentUser().getUid()).setValue(studentModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        dbHelper.studentdepartmentref(dept).child(auth.getCurrentUser().getUid()).child(variables.ID).setValue(auth.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if ((progressDialog != null) && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                                gotohomepage(context,null,variables.Student);
                            }
                        });

                    }else {
                        if ((progressDialog != null) && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(context, "Internet Connection failed, could not create your account", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            StaffModel staffModel = new StaffModel(auth.getCurrentUser().getUid(), auth.getCurrentUser().getDisplayName(), dept,String.valueOf(auth.getCurrentUser().getPhotoUrl()), faculty, office, auth.getCurrentUser().getEmail());
            dbHelper.UnverifiedStaffs(auth.getCurrentUser().getUid()).setValue(staffModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        if ((progressDialog != null) && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        showConfirmationNotice(context, auth.getCurrentUser().getDisplayName());
                    }
                }
            });

        }
    }

    public void recoverPpassword(String email, final Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.pleasewait));
        builder.setCancelable(false);
        progressDialog.show();
        Toast.makeText(context, email, Toast.LENGTH_LONG).show();
        if (Connectivity.isConnected(context)){
            auth.sendPasswordResetEmail(email).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(context, "A user with this email already exists", Toast.LENGTH_LONG).show();
                    } else if ( e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "The email you entered does not exist", Toast.LENGTH_LONG).show();
                    }else if (e instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(context, "There is no account for the email you entered ", Toast.LENGTH_LONG).show();
                    }
                      else     {
                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    e.printStackTrace();
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        builder.setTitle("Done");
                        builder.setMessage("Recovery link sent Successfully");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else {
                        Toast.makeText(context, "Incorrect email or password", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            progressDialog.dismiss();
            Toast.makeText(context, "Internet Connection failed", Toast.LENGTH_LONG).show();


        }

    }

    public boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathname,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathname, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathname,options);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void sendNotificationandSave(final String text, final String etype, String usertype, final EmergencyText emergencyText){
        dbHelper = new DbHelper(auth, emergencyText.getActivity());
        Locale locale = new Locale("yyMMddHHmmss");
        DateFormat df = new SimpleDateFormat("yyMMddHHmmss", locale);
        Date dateobj = new Date();
        final ProgressDialog progressDialog = new ProgressDialog(emergencyText.getActivity());
        progressDialog.setMessage(emergencyText.getActivity().getString(R.string.pleasewait));
        if (Connectivity.isConnected(emergencyText.getActivity())) {
            progressDialog.show();
            final String id = df.format(dateobj) + auth.getCurrentUser().getUid();
            if (usertype != null) {
                if (usertype.equals(variables.Staffs)) {
                    dbHelper.staffref(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                final StaffModel staffModel = dataSnapshot.getValue(StaffModel.class);
                                final Alert alert = new Alert(variables.Staffs, System.currentTimeMillis(), text, etype, auth.getCurrentUser().getUid(), 0.0, 0.0);
                                dbHelper.Depthistorystudentref(staffModel.getDept()).child(id).setValue(alert).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dbHelper.Tagref().child(etype).child(id).setValue(alert).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                notifyA(auth.getCurrentUser().getUid());
                                                progressDialog.dismiss();
                                                emergencyText.dismiss();
                                            }
                                        });

                                    }
                                });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    dbHelper.studentref(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                final StudentModel studentModel = dataSnapshot.getValue(StudentModel.class);
                                final Alert alert = new Alert(variables.Student, System.currentTimeMillis(), text, etype, auth.getCurrentUser().getUid(), 0.0, 0.0);
                                dbHelper.Depthistorystudentref(studentModel.getDept()).child(id).setValue(alert).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dbHelper.Tagref().child(etype).child(id).setValue(alert).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                notifyA(auth.getCurrentUser().getUid());
                                                progressDialog.dismiss();
                                                emergencyText.dismiss();
                                            }
                                        });


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
        }else {
            Toast.makeText(emergencyText.getActivity(), "No Internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void notifyA(final String userId){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic YWE4ZWNiM2QtODQzZS00YjI4LTg5OWUtNGMwMmIwMjg0OTE1");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"26e448b4-ea29-4c95-80ee-3efed2649c11\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"id\", \"relation\": \"=\", \"value\": \"" + userId + "\"}],"

                                + "\"data\": {\"activityToBeOpened\": \"Staffemergency\"},"
                                + "\"contents\": {\"en\": \"AlERT!!! New Emergency Alert\"}"
                                + "}";

                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }

        });
    }
}
