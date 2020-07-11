package gamal.myappnew.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gamal.myappnew.chatapp.Common.Common;
import gamal.myappnew.chatapp.MessageActivity;
import gamal.myappnew.chatapp.Moduel.Chat;
import gamal.myappnew.chatapp.Moduel.User;
import gamal.myappnew.chatapp.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
List<User> list;
Context context;
String thelasetmessage;

    public UserAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
     holder.username.setText(list.get(position).getUsername());
     if (list.get(position).getImageURL().equals("default")||list.get(position).getImageURL().equals(null))
     {
         holder.profile_image.setImageResource(R.drawable.profile);
     }else {
         Glide.with(context).load(list.get(position).getImageURL()).into(holder.profile_image);
     }
     holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             context.startActivity(new Intent(context, MessageActivity.class)
             .putExtra("userid",list.get(position).getId()));
         }
     });
     if (list.get(position).getStatus().equals("online"))
     {
         holder.image_on.setVisibility(View.VISIBLE);
     }else {
         holder.image_on.setVisibility(View.GONE);
     }
     getlastmessage(list.get(position).getId(),holder.lastmessage);

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView username,lastmessage;
        ImageView image_on;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image=itemView.findViewById(R.id.profile_image);
            username=itemView.findViewById(R.id.username);
            image_on=itemView.findViewById(R.id.image_on);
            lastmessage=itemView.findViewById(R.id.lastmessage);
        }
    }
    private void getlastmessage(final String userid, final TextView lasemessage)
    {
        thelasetmessage="default";
        FirebaseDatabase.getInstance().getReference("Chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot:dataSnapshot.getChildren())
                        {
                            Chat chat=snapshot.getValue(Chat.class);
                            if (chat.getReceiver().equals(Common.id)
                            &&chat.getSender().equals(userid) ||chat.getReceiver().equals(userid)
                            &&chat.getSender().equals(Common.id))
                            {
                                thelasetmessage=chat.getMessage();
                            }
                        }
                        switch (thelasetmessage)
                        {
                            case "default":
                                lasemessage.setText("No Message between them");
                                break;
                                default:
                                    lasemessage.setText(thelasetmessage);
                                    break;
                        }
                        thelasetmessage="default";
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
