package gamal.myappnew.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import gamal.myappnew.chatapp.Common.Common;
import gamal.myappnew.chatapp.Moduel.Chat;
import gamal.myappnew.chatapp.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
 List<Chat> mchat;
 public static final int MES_TYPE_LEFT=0;
 public static final int MES_TYPE_RIGHT=1;
    Context context;
    String imageurl;

    public MessageAdapter(List<Chat> mchat, Context context,String imageurl) {
        this.mchat = mchat;
        this.context = context;
        this.imageurl=imageurl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MES_TYPE_RIGHT)
        {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false));
        }else {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false));

        }

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.massge.setText(mchat.get(position).getMessage());
        if (imageurl.equals("default"))
        {
            holder.profile_image.setImageResource(R.drawable.profile);
        }else {
            Glide.with(context).load(imageurl).into(holder.profile_image);
        }
        if (position==mchat.size()-1)
        {
            if (mchat.get(position).isIsseen())
            {
                holder.isseen.setImageResource(R.drawable.seen);
            }else {
                holder.isseen.setImageResource(R.drawable.notseen);
            }
        }else {
            holder.isseen.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mchat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image,isseen;
        TextView massge;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image=itemView.findViewById(R.id.profile_image);
            massge=itemView.findViewById(R.id.show_message);
            isseen=itemView.findViewById(R.id.isseen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mchat.get(position).getSender().equals(Common.id))
        {
            return MES_TYPE_RIGHT;
        }else {
            return MES_TYPE_LEFT;
        }
    }
}
