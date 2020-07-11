package gamal.myappnew.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gamal.myappnew.chatapp.Adapter.MessageAdapter;
import gamal.myappnew.chatapp.Moduel.Chat;
import gamal.myappnew.chatapp.Moduel.User;
import gamal.myappnew.chatapp.Notification.APIService;
import gamal.myappnew.chatapp.Notification.Client;
import gamal.myappnew.chatapp.Notification.Data;
import gamal.myappnew.chatapp.Notification.MyResponse;
import gamal.myappnew.chatapp.Notification.Sender;
import gamal.myappnew.chatapp.Notification.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
  CircleImageView profile_image;
  TextView username,status;
  String userid;
  ImageView image_send;
  EditText massege;
  MessageAdapter adapter;
  List<Chat> mchat;
  RecyclerView recyclerView;
  LayoutAnimationController layoutAnimationController;
  ValueEventListener seenlisiner;
    DatabaseReference reference;
APIService apiService;
boolean notify=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //intent....
        if (getIntent()!=null)
        {
            userid=getIntent().getStringExtra("userid");
        }
        // toolbar
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.collapseActionView();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        // Widget...
        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        image_send=findViewById(R.id.image_send);
        recyclerView=findViewById(R.id.recycle_message);
        recyclerView.setHasFixedSize(true);
        status=findViewById(R.id.stutas);
        layoutAnimationController= AnimationUtils.loadLayoutAnimation(getApplicationContext(),R.anim.layoutitem_from_left);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        massege=findViewById(R.id.message);

        image_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                if (massege.getText().toString().isEmpty())
                {
                    Toast.makeText(MessageActivity.this, "Can't send empty message...", Toast.LENGTH_SHORT).show();
                }else {
                    sendmessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),userid,massege.getText().toString());
                }
                massege.setText("");
            }
        });
        // load from firebase....
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default"))
                {
                    profile_image.setImageResource(R.drawable.profile);
                }else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
                if (user.getStatus().equals("online"))
                {
                    status.setText("Online");
                }else {
                    status.setText("OFFline");
                }
                readmessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenMessage(userid);
    }
    public  void sendmessage(String senderid, final String reciverid, String message)
    {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",senderid);
        hashMap.put("receiver",reciverid);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        FirebaseDatabase.getInstance().getReference("Chats")
                .push().setValue(hashMap);
       final DatabaseReference chatref=  FirebaseDatabase.getInstance().getReference("Chatlis")
               .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
               .child(userid);
       chatref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    chatref.child("id").setValue(userid);
                }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
       final String msg=message;
       FirebaseDatabase.getInstance().getReference("Users")
               .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
               .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       User user=dataSnapshot.getValue(User.class);
                       if (notify) {
                           SendNotification(reciverid, user.getUsername(), msg);
                       }
                       notify=false;
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
    }

    private void SendNotification(String reciverid, final String username, final String msg) {
       DatabaseReference tokens= FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(reciverid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Token token=snapshot.getValue(Token.class);
                    Data data=new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            R.mipmap.ic_launcher,username+": "+msg,"New Message",userid);
                    Sender sender=new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                    .enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code()==200)
                            {
                                if (response.body().success!=1)
                                {
                                    Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readmessage(final String myid, final String userid, final String imageurl)
    {
        mchat=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mchat.clear();
                        for (DataSnapshot snapshot:dataSnapshot.getChildren())
                        {
                            Chat chat=snapshot.getValue(Chat.class);
                            if (chat.getReceiver().equals(myid)&&chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid)&&chat.getSender().equals(myid))
                            {
                                mchat.add(chat);

                            }
                            adapter=new MessageAdapter(mchat,MessageActivity.this,imageurl);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void status(String status)
    {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        Currentuser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        reference.removeEventListener(seenlisiner);
        Currentuser("none");
    }
    private void seenMessage(final String userid)
    {

        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenlisiner=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())&&chat.getSender().equals(userid))
                    {
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void Currentuser(String userid)
    {
        SharedPreferences.Editor editor=getSharedPreferences("Gamal",MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();
    }

}
