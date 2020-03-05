package com.carldroid.taskmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    RecyclerView recyclerView;

    private EditText title_update;
    private EditText note_update;
    private Button delete_btn;
    private Button update_btn;

    //Variables
    private String title;
    private String note;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uid);
        mDatabase.keepSynced(true);
        recyclerView = findViewById(R.id.recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Task App");

        floatingActionButton = findViewById(R.id.fab_btn);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
                LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                View myView = inflater.inflate(R.layout.custominputfield, null);
                myDialog.setView(myView);

                final AlertDialog dialog = myDialog.create();

                final EditText title = myView.findViewById(R.id.edt_title);
                final EditText note = myView.findViewById(R.id.edt_note);
                Button mSave = myView.findViewById(R.id.btn_save);

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mTitle = title.getText().toString().trim();
                        String mNote = note.getText().toString().trim();

                        if (TextUtils.isEmpty(mTitle)) {
                            title.setError("Required Field");
                            return;
                        }
                        if (TextUtils.isEmpty(mNote)) {
                            note.setError("Required Field");
                            return;
                        }

                        String id = mDatabase.push().getKey();
                        String date = DateFormat.getDateInstance().format(new Date());

                        DataModel data = new DataModel(mTitle, mNote, date, id);
                        mDatabase.child(id).setValue(data);
                        Toast.makeText(HomeActivity.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<DataModel, myViewHolder> adapter = new FirebaseRecyclerAdapter<DataModel, myViewHolder>(
                DataModel.class,
                R.layout.item_data,
                myViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(myViewHolder myViewHolder, final DataModel dataModel, final int i) {

                myViewHolder.setTitle(dataModel.getTitle());
                myViewHolder.setNote(dataModel.getNote());
                myViewHolder.setDate(dataModel.getDate());

                myViewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(i).getKey();
                        title = dataModel.getTitle();
                        note = dataModel.getNote();
                        updateData();
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);
    }


    public static class myViewHolder extends RecyclerView.ViewHolder {

        View myView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setTitle(String title) {
            TextView mTitle = myView.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setNote(String note) {
            TextView mNote = myView.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date) {
            TextView mDate = myView.findViewById(R.id.date);
            mDate.setText(date);
        }
    }

    public void updateData() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.updateinputtextfield, null);
        mydialog.setView(view);
        final AlertDialog alertDialog = mydialog.create();

        title_update = view.findViewById(R.id.edt_titleupdate_upd);
        note_update = view.findViewById(R.id.edt_noteupdate_upd);

        title_update.setText(title);
        title_update.setSelection(title.length());

        note_update.setText(note);
        note_update.setSelection(note.length());

        delete_btn = view.findViewById(R.id.btn_delete_upd);
        update_btn = view.findViewById(R.id.btn_update_upd);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = title_update.getText().toString().trim();
                note = note_update.getText().toString().trim();

                String mDate = DateFormat.getDateInstance().format(new Date());
                DataModel dataModel = new DataModel(title, note, mDate, post_key);

                mDatabase.child(post_key).setValue(dataModel);

                alertDialog.dismiss();
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(post_key).removeValue();

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
