package edu.ucucite.androidbudget;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import edu.ucucite.androidbudget.Model.Data;
import edu.ucucite.androidbudget.Model.DataMylist;


public class MyListFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mMyListDatabase;

    //RecyclerView
    private RecyclerView recyclerView;

    private ImageButton btn_mylist;

    private TextView mylistTotalSum;

    //Edit texts
    private EditText edtAmount;
    private EditText edtType;

    //Buttons
    private Button btnUpdate;
    private Button btnDelete;

    //Data items
    private String type;
    private int amount;

    private String post_key;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_my_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mMyListDatabase = FirebaseDatabase.getInstance().getReference().child("MyListDatabase").child(uid);

        mylistTotalSum = myview.findViewById(R.id.mylist_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_mylist);

        btn_mylist = myview.findViewById(R.id.btn_mylist);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mMyListDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int totalValue = 0;

                for (DataSnapshot mysnapshot:dataSnapshot.getChildren()){

                    DataMylist data = mysnapshot.getValue(DataMylist.class);

                    totalValue+=data.getAmount();

                    String setTotalValue = String.valueOf(totalValue);

                    mylistTotalSum.setText(setTotalValue);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_mylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }

            private void addData() {
                mylistDataInsert();
            }
        });

        return myview;
    }

    public void mylistDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.layout_insert_mylist, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);
        final EditText edtAmmount=myview.findViewById(R.id.amount_edit);
        final EditText edtType=myview.findViewById(R.id.type_edit);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCansel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type=edtType.getText().toString().trim();
                String ammount=edtAmmount.getText().toString().trim();

                if (TextUtils.isEmpty(type)){
                    edtType.setError("Required Field..");
                    return;
                }

                if (TextUtils.isEmpty(ammount)){
                    edtAmmount.setError("Required Field..");
                    return;
                }

                int ourammontint=Integer.parseInt(ammount);

                String id=mMyListDatabase.push().getKey();

                DataMylist data=new DataMylist(ourammontint,type,id);
                mMyListDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data ADDED", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<DataMylist, MyViewHolder>adapter=new FirebaseRecyclerAdapter<DataMylist, MyViewHolder>
                (
                        DataMylist.class,
                        R.layout.my_list_recycler_data,
                        MyViewHolder.class,
                        mMyListDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final DataMylist model, final int position) {
                viewHolder.setType(model.getType());
                viewHolder.setAmount(model.getAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key = getRef(position).getKey();

                        type = model.getType();
                        amount = model.getAmount();

                        updateDataItem();

                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);

    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_mylist);
            mType.setText(type);
        }

        private void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.amount_txt_mylist);
            String stamount = String.valueOf(amount);
            mAmount.setText(stamount);
        }
    }


    private void updateDataItem(){

        android.app.AlertDialog.Builder mydialog = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_mylist_data, null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edit);
        edtType = myview.findViewById(R.id.type_edit);

        btnUpdate = myview.findViewById(R.id.btnUpdate);
        btnDelete = myview.findViewById(R.id.btnDelete);

        //Set data
        edtType.setText(type);
        edtType.setSelection(type.length());


        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myview.findViewById(R.id.btnUpdate);
        btnDelete = myview.findViewById(R.id.btnDelete);

        final android.app.AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type = edtType.getText().toString().trim();

                String upamount = String.valueOf(amount);

                upamount = edtAmount.getText().toString().trim();

                int myAmount = Integer.parseInt(upamount);


                DataMylist data = new DataMylist(myAmount, type, post_key);

                mMyListDatabase.child(post_key).setValue(data);

                dialog.dismiss();


            }
        });

        dialog.show();
    }


}