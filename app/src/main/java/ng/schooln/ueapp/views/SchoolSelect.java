package ng.schooln.ueapp.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import ng.schooln.ueapp.R;

/**
 * Created by xyjoe on 9/8/18.
 */

public class SchoolSelect extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner spinner, deptspinner;
    Button schoolnext, cameranext;
    private TextView selectdept, clicktxt;
    private LinearLayout cameralay, deptlay;
    private ImageView camicon, userimage;
    int RESULT_LOAD_IMAGE=320;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 322;
    String picturepath;
    FirebaseAuth auth;
    Bitmap bitmap;
    ArrayAdapter<String> deptarray;
    private String[] deptarr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_select);
        init();
    }

    // This method is used to initialise all variables
    private void init(){
        auth = FirebaseAuth.getInstance();
        spinner = findViewById(R.id.spinner);
        deptspinner = findViewById(R.id.deptspinner);
        selectdept  = findViewById(R.id.selectdept);
        schoolnext = findViewById(R.id.next2);
        cameranext = findViewById(R.id.next);
        cameralay = findViewById(R.id.cameralay);
        deptlay = findViewById(R.id.deptlay);
        clicktxt = findViewById(R.id.text);
        camicon = findViewById(R.id.camicon);
        userimage = findViewById(R.id.image);
        spinner.setOnItemSelectedListener(this);
        deptspinner.setOnItemSelectedListener(this);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            if (user.getPhotoUrl() != null){
                cameralay.setVisibility(View.GONE);
                deptlay.setVisibility(View.VISIBLE);
            }else {
                cameralay.setVisibility(View.VISIBLE);
                deptlay.setVisibility(View.GONE);
            }
        }
    }

    public void openusertype(View v){
        Intent intent = new Intent(SchoolSelect.this, MainActivity.class);
        intent.putExtra("faculty", spinner.getSelectedItem().toString());
        intent.putExtra("dept", deptspinner.getSelectedItem().toString());
        intent.putExtra("path", picturepath);
        startActivity(intent);
    }

    public void SelectDept(View v){
        deptlay.setVisibility(View.VISIBLE);
        cameralay.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == spinner) {
            if (spinner.getSelectedItemPosition() != 0) {
                deptarr = null;
                if (spinner.getSelectedItemPosition() == 1) {
                    deptarr = getResources().getStringArray(R.array.Saat);
                } else if (spinner.getSelectedItemPosition() == 4) {
                    deptarr = getResources().getStringArray(R.array.Sops);
                }else {
                    deptarr = null;
                }
                if (deptarr != null){
                    deptarray = new ArrayAdapter<String>(SchoolSelect.this, android.R.layout.simple_spinner_dropdown_item, deptarr);
                    deptspinner.setAdapter(deptarray);
                    deptspinner.setVisibility(View.VISIBLE);
                    selectdept.setVisibility(View.VISIBLE);
                }

            } else {
                deptspinner.setVisibility(View.GONE);
                schoolnext.setVisibility(View.GONE);
                selectdept.setVisibility(View.GONE);
            }
        } else if (adapterView == deptspinner){
            schoolnext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void openGallery(View v){
        CheckUserPermsions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadImage();
                } else {
                    // Permission Denied
                    Toast.makeText(SchoolSelect.this, "Permission denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    // Checking if the application has permissions to read phone gallery
    void CheckUserPermsions() {
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
        Intent  intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
            cursor.moveToFirst();
            picturepath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(picturepath, options);
            setImage(bitmap);
        }

    }

    private void setImage(Bitmap bitmap) {
        camicon.setVisibility(View.GONE);
        clicktxt.setVisibility(View.GONE);
        userimage.setImageBitmap(bitmap);
        cameranext.setVisibility(View.VISIBLE);
    }



}
