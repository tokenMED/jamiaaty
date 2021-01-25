package com.example.jamiaaty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jamiaaty.Home.Module_pack.Module;
import com.example.jamiaaty.Home.Module_pack.ModuleCardAdapter;
import com.example.jamiaaty.Home.localdb.localdb;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
//import com.squareup.picasso.Picasso;

public class Fragment1 extends Fragment implements  View.OnClickListener{
    ImageView imageView;
    TextView nameEt, profEt, bioEt, emailEt,webEt;
    ImageButton imageButtonEdit,imageButtonMenu;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


          view = inflater.inflate(R.layout.fragment1,container,false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView = getActivity().findViewById(R.id.iv_profile_pic);
        nameEt = getActivity().findViewById(R.id.et_name_cp);
//        profEt = getActivity().findViewById(R.id.tv_prof_f1);
        bioEt = getActivity().findViewById(R.id.et_bio_cp);
        emailEt = getActivity().findViewById(R.id.et_email_cp);
        webEt = getActivity().findViewById(R.id.et_website_cp);


        imageButtonEdit = getActivity().findViewById(R.id.ib_edit_f1);
        imageButtonMenu = getActivity().findViewById(R.id.ib_menu_f1);


       imageButtonMenu.setOnClickListener(this);
        imageButtonEdit.setOnClickListener(this);
        imageView.setOnClickListener(this);
        webEt.setOnClickListener(this);

//        try {//fill posts in profile just for test
//            RecyclerView recyclerView = view.findViewById(R.id.posts_RV);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            recyclerView.setHasFixedSize(true);
//            ArrayList<com.example.jamiaaty.Home.Module_pack.Module> modulesCherche=new ArrayList<>();
//            localdb dbbookmark=new localdb(getContext());
//            ArrayList<Module> mo=dbbookmark.getAllModules();
//            mo.addAll(dbbookmark.getAllModules());
//            mo.addAll(dbbookmark.getAllModules());
//            mo.addAll(dbbookmark.getAllModules());
//            mo.addAll(dbbookmark.getAllModules());
//            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
//            ModuleCardAdapter adapter = new ModuleCardAdapter(getContext(),mo );
//            //adapter.setClickListener(this);
//            recyclerView.setAdapter(adapter);
//
//        }catch (Exception e){
//            Toast.makeText(getContext(), "error"+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.ib_edit_f1:
                Intent intent = new Intent(getActivity(),UpdateProfile.class);
                startActivity(intent);
                break;
            case R.id.ib_menu_f1 :
                BottomSheetMen bottomSheetMen = new BottomSheetMen();
                bottomSheetMen.show(getFragmentManager(),"bottomsheet");
                break;
            case R.id.profile_pic :
                Intent intent1 = new Intent(getActivity(),ImageActivity.class);
                startActivity(intent1);
                break;
            case R.id.et_website_cp :
                try {
                    String url = webEt.getText().toString();
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    intent2.setData(Uri.parse(url));
                    startActivity(intent2);

                }catch(Exception e){
                    Toast.makeText(getActivity(),"Ivalid Url",Toast.LENGTH_SHORT).show();
                }

                break;

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        String cuurentUser="";
        DocumentReference reference ;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null){
             cuurentUser = user.getUid();
            reference = firestore.collection("user").document(cuurentUser);
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                String nameResult = task.getResult().getString("name");
                                String emailResult = task.getResult().getString("email");
                                String bioResult = task.getResult().getString("bio");
                                String profResult = task.getResult().getString("prof");
                                String uidResult = task.getResult().getString("uid");
                                String webResult = task.getResult().getString("web");
                                String privacyResult = task.getResult().getString("privacy");
                                String urlResult = task.getResult().getString("url");
                                if(!urlResult.equals("")){
//                                    Picasso.get().load(urlResult).into(imageView);
                                    try {
                                        Glide.with(view.getContext()).load(urlResult).into(imageView);
                                    }catch (Exception e){
                                        Log.i("Errooor","error while getting img frim db :"+e.getMessage());
                                    }
                                }
                                nameEt.setText(nameResult);
                                bioEt.setText(bioResult);
                                emailEt.setText(emailResult);
                                webEt.setText(webResult);
                                //profEt.setText(profResult);


                            } else {
                                Intent intent = new Intent(getActivity(), CreateProfile.class);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }
}
