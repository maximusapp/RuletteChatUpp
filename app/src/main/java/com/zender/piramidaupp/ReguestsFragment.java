package com.zender.piramidaupp;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReguestsFragment extends Fragment {

    private View mGroupView;


    public ReguestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mGroupView = inflater.inflate(R.layout.fragment_reguests,container, false);


        FloatingActionButton fab = (FloatingActionButton)mGroupView.findViewById(R.id.fabReg);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentUsersGroup = new Intent(getContext(), UserGroupActivity.class);
                startActivity(intentUsersGroup);

                //Toast.makeText(getContext(), "Добавить чат-группу", Toast.LENGTH_SHORT).show();
            }
        });


        // Inflate the layout for this fragment
        return mGroupView;
    }

}
