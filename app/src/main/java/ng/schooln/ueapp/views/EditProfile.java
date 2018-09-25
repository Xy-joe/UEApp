package ng.schooln.ueapp.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.controllers.Controls;
import ng.schooln.ueapp.controllers.DbHelper;
import ng.schooln.ueapp.models.StaffModel;
import ng.schooln.ueapp.models.StudentModel;
import ng.schooln.ueapp.utils.TextWatcherAdapter;
import ng.schooln.ueapp.utils.Variables;

public class EditProfile extends AppCompatActivity {

    private EditText name;
    private TextView imgtxt;
    private ImageView image, mgbg;
    private FirebaseAuth auth;
    private DbHelper dbHelper;
    private Variables variables;
    private StaffModel staffModel;
    private StudentModel studentModel;
    private  String usertye;
    private ProgressDialog progressDialog;
    private int REQUEST_CODE_ASK_PERMISSIONS = 5483;
    int RESULT_LOAD_IMAGE = 954;
    private RelativeLayout relativeLayout;
    Bitmap bitmap;
    private  Menu menu;
    private MenuItem saveitem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initViews();
    }


    private void initViews() {
        variables = new Variables();
        auth = FirebaseAuth.getInstance();
        dbHelper = new DbHelper(auth, this);
        progressDialog = new ProgressDialog(this);
        name = findViewById(R.id.name);
        image = findViewById(R.id.img);
        imgtxt = findViewById(R.id.imgtxt);
        relativeLayout = findViewById(R.id.ggo);
        usertye = getIntent().getStringExtra("usertype");
        seytUpViews();
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkuserpermission();
            }
        });

    }

    private void checkuserpermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        LoadImage();
    }

    private void LoadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = this.getContentResolver().query(selectedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String picturepath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            bitmap =  new Controls(auth).decodeSampledBitmapFromResource(picturepath, 400,300);
            image.setImageBitmap(bitmap);
            relativeLayout.setVisibility(View.GONE);
            checkimage();
        }
    }

    private void seytUpViews() {
        dbHelper.userref().child(usertye).child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    if (usertye.equals(variables.Staffs)){
                        staffModel = dataSnapshot.getValue(StaffModel.class);
                        name.setText(staffModel.getNam());
                        Picasso.with(EditProfile.this).load(staffModel.getImg()).into(image);

                    }else {
                        studentModel = dataSnapshot.getValue(StudentModel.class);
                        if (studentModel != null){
                            name.setText(studentModel.getNam());
                            Picasso.with(EditProfile.this).load(studentModel.getImg()).into(image);
                        }

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
        getMenuInflater().inflate(R.menu.editprofilemenu, menu);
        this.menu = menu;
        editCheck();
        return true;

    }

    private void editCheck() {
        if (!usertye.equals(variables.Staffs)){
            name.addTextChangedListener(textWatcherAdapter(name, studentModel.getNam()));
        }else {
            name.addTextChangedListener(textWatcherAdapter(name, staffModel.getNam()));
        }
        checkimage();
    }

    private TextWatcherAdapter textWatcherAdapter (final EditText editText, final String option){
        return new TextWatcherAdapter(){
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    if (menu != null) {
                        MenuItem item = menu.findItem(R.id.save);
                        if (!editText.getText().toString().equals(option) && editText.getText().length() != 0 && !editText.getText().toString().equals("")){
                            item.setVisible(true);
                        }
                        else {
                            item.setVisible(false);
                        }
                    }
                }
            }
        };
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save){
            performSaveAction();
        }
        return false;
    }

    private void performSaveAction() {
        progressDialog.setMessage(getString(R.string.pleasewait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (TextUtils.isEmpty(name.getText().toString())){
            name.setError(getString(R.string.error_field_required));
            name.requestFocus();
            return;
        }
        if (bitmap != null){
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
                        public void onSuccess(final Uri uri) {
                            dbHelper.userref().child(usertye).child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null){
                                        if (!usertye.equals(variables.Staffs)){

                                            dbHelper.userref().child(usertye).child(auth.getCurrentUser().getUid()).child("nam").setValue(name.getText().toString());
                                            dbHelper.userref().child(usertye).child(auth.getCurrentUser().getUid()).child("img").setValue(String.valueOf(uri));
                                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri).setDisplayName(name.getText().toString()).build();
                                            auth.getCurrentUser().updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            });

        }else {

            dbHelper.userref().child(usertye).child(auth.getCurrentUser().getUid()).child("nam").setValue(name.getText().toString());
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();
            auth.getCurrentUser().updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (isFinishing()){
                        progressDialog.dismiss();
                    }
                    if (isDestroyed()){
                        progressDialog.dismiss();
                    }

                }
            });
        }

    }

    private void checkimage(){
        final MenuItem item = menu.findItem(R.id.save);
        image.buildDrawingCache();
        Bitmap vv = image.getDrawingCache();
        if (bitmap != null){
            if (bitmap != vv){
                item.setVisible(true);
            }else {
                item.setVisible(false);
            }
        }
    }
}
