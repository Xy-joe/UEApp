package ng.schooln.ueapp.views;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.adapters.HistoryAdapter;
import ng.schooln.ueapp.controllers.DbHelper;
import ng.schooln.ueapp.models.Alert;
import ng.schooln.ueapp.models.StaffModel;
import ng.schooln.ueapp.models.StudentModel;
import ng.schooln.ueapp.utils.Variables;

public class Tags extends AppCompatActivity {

    String tag;
    Variables variables;
    DbHelper dbHelper;
    FirebaseAuth auth;
    HistoryAdapter adapter;
    RecyclerView recyclerView;
    StaffModel staffModel;
    TextView noresult;
    StudentModel studentModel;
    ArrayList<Alert> data = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        setUpViews();
    }

    private void setUpViews() {
        variables = new Variables();
        auth = FirebaseAuth.getInstance();
        dbHelper = new DbHelper(auth, this);
        tag = getIntent().getStringExtra(variables.Tags);
        setTitle(tag);
        recyclerView = findViewById(R.id.recycler);
        noresult = findViewById(R.id.noresult);
        staffModel = (StaffModel) getIntent().getSerializableExtra(variables.Staffs);
        studentModel = (StudentModel) getIntent().getSerializableExtra(variables.Student);
        populateView();
    }

    private void populateView() {
        dbHelper.Tagref().child(tag).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    data.clear();
                    fetchData(dataSnapshot);
                }else {
                    noresult.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            Alert alert = ds.getValue(Alert.class);
            data.add(0,alert);

            adapter = new HistoryAdapter(Tags.this, data, auth, staffModel, studentModel);
            recyclerView.setLayoutManager(new LinearLayoutManager(Tags.this));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            noresult.setVisibility(View.GONE);

        }
    }
}

