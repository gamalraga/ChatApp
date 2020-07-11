package gamal.myappnew.chatapp.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gamal.myappnew.chatapp.Adapter.UserAdapter;
import gamal.myappnew.chatapp.Moduel.User;
import gamal.myappnew.chatapp.R;
import gamal.myappnew.chatapp.StartActivity;

public class UsersFragment extends Fragment {
  RecyclerView recyclerView;
  LayoutAnimationController layoutAnimationController;
  UserAdapter adapter;
  ArrayList<User> muser;
  EditText search;
  FirebaseUser firebaseUser;
    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
          View view=inflater.inflate(R.layout.fragment_users, container, false);
          recyclerView=view.findViewById(R.id.recycleview_user);
          search=view.findViewById(R.id.search_user);
          recyclerView.setHasFixedSize(true);
          firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
          recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutAnimationController= AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layoutitem_from_left);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        muser=new ArrayList<>();
        adapter=new UserAdapter(muser,getContext());
          recyclerView.setAdapter(adapter);
          readUSers();
          search.addTextChangedListener(new TextWatcher() {
              @Override
              public void beforeTextChanged(CharSequence s, int start, int count, int after) {

              }

              @Override
              public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchUser(s.toString().toLowerCase());
              }

              @Override
              public void afterTextChanged(Editable s) {

              }
          });
          return view;
    }

    private void SearchUser(String words) {
        FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("search")
                .startAt(words)
                .endAt(words+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isAdded()) {
                muser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!firebaseUser.getUid().equals(user.getId())) {
                        muser.add(user);
                    }

                }
                adapter.notifyDataSetChanged();
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUSers() {
        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (isAdded()) {
                            if (dataSnapshot.exists()) {
                                if (search.getText().toString().equals("")) {
                                    muser.clear();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        User user = snapshot.getValue(User.class);
                                        if (!firebaseUser.getUid().equals(user.getId())) {
                                            muser.add(user);
                                        }

                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }else {
                                startActivity(new Intent(getContext(), StartActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
