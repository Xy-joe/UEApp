package ng.schooln.ueapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.controllers.DbHelper;
import ng.schooln.ueapp.models.Alert;
import ng.schooln.ueapp.models.StaffModel;
import ng.schooln.ueapp.models.StudentModel;
import ng.schooln.ueapp.utils.Variables;
import ng.schooln.ueapp.views.Tags;
import ng.schooln.ueapp.views.Userprofile;

/**
 * Created by xyjoe on 1/18/18.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHold> {
   private Context context;
    private Variables variables = new Variables();
    private FirebaseUser firebaseUser;
   private StudentModel studentModel;
   private StaffModel staffModel;
  private DbHelper dbHelper;
    private ArrayList<Alert> data = new ArrayList<>();
    private FirebaseAuth auth;
    private  Alert alert;




    public HistoryAdapter(Context applicationContext, ArrayList<Alert> data, FirebaseAuth auth, StaffModel staffModel, StudentModel studentModel) {
        this.context = applicationContext;
        this.data = data;
        this.staffModel = staffModel;
        this.studentModel = studentModel;
        this.auth = auth;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.history_content,parent, false);
        return new ViewHold(v);
    }

    @Override
    public void onBindViewHolder(final ViewHold holder, int position) {
        dbHelper = new DbHelper(auth, context);
        alert = data.get(position);
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Lato-Bold.ttf");
        holder.name.setTypeface(custom_font);
        holder.time.setText(DateUtils.getRelativeTimeSpanString(alert.getTime()));
        holder.text.setText(alert.getDecrp());
        holder.tag.setText(alert.getTag());
        dbHelper.userref().child(alert.getUsertype()).child(alert.getUserid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    StaffModel staffModel = dataSnapshot.getValue(StaffModel.class);
                    Glide.with(context).load(staffModel.getImg()).into(holder.userimg);
                    holder.name.setText(staffModel.getNam());
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
         /* end check */
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public  class ViewHold extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView text, tag, name, time;
        ImageView userimg;

       private ViewHold(View view) {
           super(view);
           view.setOnClickListener(this);
          text = view.findViewById(R.id.text);
          userimg = view.findViewById(R.id.userimg);
          userimg.setOnClickListener(this);
          time = view.findViewById(R.id.time);
          tag = view.findViewById(R.id.tag);
          tag.setOnClickListener(this);
          name = view.findViewById(R.id.uname);

       }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            alert = data.get(position);
            if (view == tag){
                if (staffModel != null){
                    Intent intent = new Intent(context, Tags.class);
                    intent.putExtra(variables.ID, alert.getUserid());
                    intent.putExtra(variables.Tags, alert.getTag());
                    intent.putExtra(variables.Staffs, staffModel);
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, Tags.class);
                    intent.putExtra(variables.ID, alert.getUserid());
                    intent.putExtra(variables.Tags, alert.getTag());
                    intent.putExtra(variables.Student, studentModel);
                    context.startActivity(intent);
                }
            }
            if (view == userimg){
                if (staffModel != null){
                    Intent intent = new Intent(context, Userprofile.class);
                    intent.putExtra(variables.ID, alert.getUserid());
                    intent.putExtra(variables.Users, variables.Staffs);
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, Userprofile.class);
                    intent.putExtra(variables.ID, alert.getUserid());
                    intent.putExtra(variables.Users, variables.Student);
                    context.startActivity(intent);
                }
            }

        }
    }

}