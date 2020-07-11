package gamal.myappnew.chatapp.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import gamal.myappnew.chatapp.Adapter.UserAdapter;
import gamal.myappnew.chatapp.Common.Common;
import gamal.myappnew.chatapp.Moduel.Chat;
import gamal.myappnew.chatapp.Moduel.Chatlist;
import gamal.myappnew.chatapp.Moduel.User;
import gamal.myappnew.chatapp.Notification.Token;
import gamal.myappnew.chatapp.R;


public class ChatsFragment extends Fragment {

RecyclerView recyclerView;
UserAdapter userAdapter;
List<User> musers;

List<Chatlist> userlist;
    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
          View view=inflater.inflate(R.layout.fragment_chats, container, false);
          recyclerView=view.findViewById(R.id.recycle_view);
          recyclerView.setHasFixedSize(true);
          recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
          userlist=new ArrayList<>();
          musers=new ArrayList<>();
            FirebaseDatabase.getInstance().getReference("Chatlis")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (isAdded()) {
                        userlist.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chatlist chatlist = snapshot.getValue(Chatlist.class);
                            userlist.add(chatlist);
                        }
                        chatlist();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            updataToken(FirebaseInstanceId.getInstance().getToken());
               return view;
    }
private void updataToken(String token)
{
    Token token1=new Token(token);
    FirebaseDatabase.getInstance().getReference("Tokens")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .setValue(token1);
}
    private void chatlist() {
        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (isAdded()) {
                            musers.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                for (Chatlist chatlist : userlist) {
                                    if (chatlist.getId().equals(user.getId())) {
                                        musers.add(user);
                                    }
                                }
                            }
                            userAdapter = new UserAdapter(musers, getContext());
                            recyclerView.setAdapter(userAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
