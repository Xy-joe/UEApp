package ng.schooln.ueapp.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.controllers.DbHelper;
import ng.schooln.ueapp.models.StaffModel;
import ng.schooln.ueapp.models.StudentModel;
import ng.schooln.ueapp.utils.Variables;
import ng.schooln.ueapp.views.Homepage;
import ng.schooln.ueapp.views.dialog.EmergencyText;

/**
 * Created by xyjoe on 1/18/18.
 */

public class EtypeAdapter extends RecyclerView.Adapter<EtypeAdapter.ViewHold> {
   private Context context;
    private Variables variables = new Variables();
    private FirebaseUser firebaseUser;
   private StudentModel studentModel;
   private StaffModel staffModel;
  private DbHelper dbHelper;
    private ArrayList<StaffModel> data = new ArrayList<>();
    private ArrayList<StudentModel> studata = new ArrayList<>();
    private FirebaseAuth auth;
    protected LatLng location;
    List<Address> addresses;




    public EtypeAdapter(Context applicationContext, ArrayList<StudentModel> studata, ArrayList<StaffModel> data, FirebaseAuth auth, StaffModel staffModel,
                        StudentModel studentModel, LatLng location, List<Address> addresses) {
        this.context = applicationContext;
        this.data = data;
        this.location = location;
        this.studata = studata;
        this.staffModel = staffModel;
        this.studentModel = studentModel;
        this.addresses = addresses;
        this.auth = auth;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.e_type_content,parent, false);
        return new ViewHold(v);
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {

        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Lato-Regular.ttf");
        holder.text.setTypeface(custom_font);
        if (staffModel != null){
            staffModel = data.get(position);
            holder.text.setText(staffModel.getId());
        }else {
            studentModel = studata.get(position);
            holder.text.setText(studentModel.getId());
        }

         /* end check */
    }


    @Override
    public int getItemCount() {
        if (studentModel != null){
            return studata.size();
        }else {
            return data.size();
        }

    }

    public  class ViewHold extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView text;

       private ViewHold(View view) {
           super(view);
           view.setOnClickListener(this);
          text = view.findViewById(R.id.etext);

       }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String val;
            if (studentModel != null){
                studentModel = studata.get(position);
                val= studentModel.getId();
            }else {
                staffModel = data.get(position);
                val = staffModel.getId();
            }
            EmergencyText emergencyText = new EmergencyText();
            Homepage homepage = (Homepage) context;
            Bundle bundle = new Bundle();
            bundle.putString("etype", val );
            if (addresses != null){
                bundle.putString("address",addresses.get(0).getAddressLine(0));
            }
            if (location != null){
                bundle.putDouble("latitude", location.latitude);
                bundle.putDouble("longitude", location.longitude);
            }

            if (staffModel != null){
                bundle.putString("usertype", variables.Staffs);
            }else {
                bundle.putString("usertype", variables.Student);
            }

            emergencyText.setArguments(bundle);
            emergencyText.show(homepage.getFragmentManager(), null);

        }


    }

}