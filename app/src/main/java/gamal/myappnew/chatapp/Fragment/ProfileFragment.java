package gamal.myappnew.chatapp.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import gamal.myappnew.chatapp.Common.Common;
import gamal.myappnew.chatapp.R;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

 CircleImageView profileimage;
 ImageView changephoto;
 TextView username;
StorageReference storageReference;
Uri imageuri;
StorageTask uploadtask;
FirebaseUser firebaseUser;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view= inflater.inflate(R.layout.fragment_profile, container, false);
         profileimage=view.findViewById(R.id.profile_image);
         username=view.findViewById(R.id.username);
         changephoto=view.findViewById(R.id.changeprofile);
         firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
         storageReference= FirebaseStorage.getInstance().getReference("uploads");
         // read info
        FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (isAdded()) {
                            if (dataSnapshot.child("imageURL").getValue().equals("default")) {
                                profileimage.setImageResource(R.drawable.profile);
                            } else {
                                Glide.with(getContext()).load(dataSnapshot.child("imageURL").getValue().toString()).into(profileimage);
                            }
                            username.setText(dataSnapshot.child("username").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

         // change photo
        changephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.
                            READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                , 1);
                    } else {
                        opengallery();


                    }
                }

            }
        });

         return view;
    }

    private void opengallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                 opengallery();

                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), "Can't change image profile..", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode== RESULT_OK && data!=null)
        {
            imageuri=data.getData();
            if (uploadtask!=null&&uploadtask.isInProgress())
            {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadimage();
            }

        }

    }
    private String getfileextensions(Uri uri)
    {
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadimage()
    {
        final AlertDialog pd;
        pd=new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();
       pd.show();
       if (imageuri!=null)
       {
           final StorageReference filereference=storageReference.child(System.currentTimeMillis()+"."+getfileextensions(imageuri));
           uploadtask=filereference.putFile(imageuri);
           uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
               @Override
               public Task<Uri> then(@NonNull Task task) throws Exception {
                   if (!task.isSuccessful())
                   {
                       throw task.getException();
                   }
                   return filereference.getDownloadUrl();
               }
           }).addOnCompleteListener(new OnCompleteListener<Uri>() {
               @Override
               public void onComplete(@NonNull Task<Uri> task) {
                   if (task.isSuccessful())
                   {
                       Uri downloaduri=task.getResult();
                       String muri=downloaduri.toString();
                       HashMap<String,Object> hashMap=new HashMap<>();
                       hashMap.put("imageURL",muri);
                       Glide.with(getContext()).load(muri).into(profileimage);
                       FirebaseDatabase.getInstance().getReference("Users")
                               .child(Common.id)
                               .updateChildren(hashMap);
                       pd.dismiss();
                   }else {
                       Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                       pd.dismiss();
                   }
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                   pd.dismiss();
               }
           });
       }else {
           Toast.makeText(getContext(), "No Image Selected !.", Toast.LENGTH_SHORT).show();
       }
    }
}
