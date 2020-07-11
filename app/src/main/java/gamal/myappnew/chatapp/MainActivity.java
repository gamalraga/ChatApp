package gamal.myappnew.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import gamal.myappnew.chatapp.Common.Common;
import gamal.myappnew.chatapp.Fragment.ChatsFragment;
import gamal.myappnew.chatapp.Fragment.ProfileFragment;
import gamal.myappnew.chatapp.Fragment.UsersFragment;
import gamal.myappnew.chatapp.Moduel.Chat;
import gamal.myappnew.chatapp.Moduel.User;

public class MainActivity extends AppCompatActivity {
  CircleImageView imageView;
  TextView username;
  FirebaseUser firebaseUser;
  AlertDialog pd;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        Toolbar toolbar=findViewById(R.id.toolbar);
   firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        pd=new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
    pd.show();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            pd.dismiss();
                             user = dataSnapshot.getValue(User.class);
                            username.setText(user.getUsername());
                            Common.id = user.getId();
                            if (user.getImageURL().equals("default") | user.getImageURL().equals(null)) {
                                imageView.setImageResource(R.drawable.profile);

                            } else {
                                Glide.with(getApplicationContext()).load(user.getImageURL()).into(imageView);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Chexk internet", Toast.LENGTH_SHORT).show();
                    }
                });
        final TabLayout tabLayout=findViewById(R.id.tab_layout);
        final ViewPager viewPager=findViewById(R.id.view_pager);
        FirebaseDatabase.getInstance().getReference("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
                    int unread=0;
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        Chat chat=snapshot.getValue(Chat.class);
                        if(chat.getReceiver().equals(firebaseUser.getUid())&&!chat.isIsseen())
                        {
                            unread++;
                        }
                    }
                    if (unread==0)
                    {
                       viewPagerAdapter.addFragment(new ChatsFragment(),"Chats");
                    }else {
                        viewPagerAdapter.addFragment(new ChatsFragment(),"("+unread+") "+"Chats");
                    }
               viewPagerAdapter.addFragment(new UsersFragment(),"Users");
                viewPagerAdapter.addFragment(new ProfileFragment(),"Profile");
               viewPager.setAdapter(viewPagerAdapter);
               tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,StartActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            Toast.makeText(this, "Log out is Done!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        ArrayList<Fragment> fragments;
        ArrayList<String> titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public  void addFragment(Fragment fragment,String titel)
        {
            fragments.add(fragment);
            titles.add(titel);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    private void status(String status)
    {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(firebaseUser.getUid())
                    .updateChildren(hashMap);

        }



    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }






}
