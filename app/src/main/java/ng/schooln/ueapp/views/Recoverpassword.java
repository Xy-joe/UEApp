package ng.schooln.ueapp.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.controllers.Controls;
import ng.schooln.ueapp.utils.TextWatcherAdapter;

public class Recoverpassword extends AppCompatActivity {

    private Button recover;
    private AutoCompleteTextView mEmailView;
    private   FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoverpassword);
        init();
    }

    private  void init(){
        recover = findViewById(R.id.recover);
        mEmailView = findViewById(R.id.email);
        auth = FirebaseAuth.getInstance();

        mEmailView.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (new Controls(auth).isValidEmail(s.toString())){
                    recover.setVisibility(View.VISIBLE);
                }else {
                    recover.setVisibility(View.GONE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (new Controls(auth).isValidEmail(s.toString())){
                    recover.setVisibility(View.VISIBLE);
                }else {
                    recover.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (new Controls(auth).isValidEmail(s.toString())){
                    recover.setVisibility(View.VISIBLE);
                }else {
                    recover.setVisibility(View.GONE);
                }
            }
        });
    }
    public void rcoverPass(View v){

        if (TextUtils.isEmpty(mEmailView.getText().toString())){
            mEmailView.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus();
            return;
        }
        if (! new Controls(auth).isValidEmail(mEmailView.getText().toString())){
            mEmailView.setError(getString(R.string.error_invalid_email));
            mEmailView.requestFocus();
            return;
        }
        if (auth != null){

            new Controls(auth).recoverPpassword(mEmailView.getText().toString(), this);
        }else {
            Toast.makeText(this, "Account does not exist", Toast.LENGTH_LONG).show();
        }
    }
}
