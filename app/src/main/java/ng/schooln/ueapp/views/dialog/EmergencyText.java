package ng.schooln.ueapp.views.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.controllers.Controls;
import ng.schooln.ueapp.utils.Variables;

/**
 * Created by xyjoe on 2/5/18.
 */

public  class EmergencyText extends DialogFragment implements View.OnClickListener{
    FirebaseAuth auth;

    EditText editText;
    String  usertype;
    Button alertbtn;
    ProgressDialog pd;
    String alerttype;
    Variables variables;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.message_dialog,null, false);

        init(v);

        builder.setCancelable(false);
        builder.setView(v);
        return builder.create();
    }

    private void init(View myview){
        auth = FirebaseAuth.getInstance();
        variables = new Variables();
        editText = myview.findViewById(R.id.msg);
        alertbtn = myview.findViewById(R.id.alertbtn);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Lato-Regular.ttf");
        Typeface fontb = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Lato-Bold.ttf");
       alertbtn.setTypeface(font);
       editText.setTypeface(font);
        editText.addTextChangedListener(wachText());
        editText.requestFocus();
        usertype = getArguments().getString("usertype");
        editText.addTextChangedListener(wachText());
        alerttype = getArguments().getString("etype");
        alertbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertAll();
            }
        });

    }

    private TextWatcher wachText() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    alertbtn.setVisibility(View.VISIBLE);
                }else {
                    alertbtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    alertbtn.setVisibility(View.VISIBLE);
                }else {
                    alertbtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0){
                    alertbtn.setVisibility(View.VISIBLE);
                }else {
                    alertbtn.setVisibility(View.GONE);
                }
            }
        };
    }

    public void AlertAll (){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Confirm");
        builder.setMessage("Click the Ok button to proceed");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                sendNotification();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void sendNotification() {
        if (TextUtils.isEmpty(editText.getText().toString().trim())){
            editText.setError("This is required");
            return;
        }
        new Controls(auth).sendNotificationandSave(editText.getText().toString(), alerttype, usertype,this);

    }

    private void done() {
        if (pd != null){
            pd.dismiss();
        }
        dismiss();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog alert = builder.create();

        alert.show();
    }

    @Override
    public void onClick(View view) {


    }
}
