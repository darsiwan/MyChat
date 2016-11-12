package com.example.filipzoricic.mychat;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactListFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    RecycleViewAdapter recycleViewAdapter;
    FloatingActionButton button;
    LinearLayout linearLayout;
    TextView textView;
    Context context;
    Button buttonAdd;
    DatabaseHandler databaseHandler;


    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        databaseHandler = new DatabaseHandler(context);
        buttonAdd = (Button) view.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);
        button = (FloatingActionButton) view.findViewById(R.id.actionButton);
        button.setOnClickListener(this);
        button.setVisibility(View.VISIBLE);
        recycleViewAdapter = new RecycleViewAdapter(databaseHandler.getUsers());
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        textView = (TextView) view.findViewById(R.id.editText);

        return view;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.actionButton:
                linearLayout.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
                textView.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(linearLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                break;

            case R.id.buttonAdd:
                final String mail=textView.getText().toString();
                textView.setText("");
                if(!mail.equals("")) {
                    DataProvider dataProvider = new DataProvider(context, new DataProvider.DataProviderInterface() {
                        @Override
                        public void onLoad(JSONObject jsonObject) {
                            try{
                                if(jsonObject.getInt("success")==1){

                                    String username = jsonObject.getString("username");
                                    databaseHandler.putUser(username);
                                    String[] newList= new String[recycleViewAdapter.userList.length+1];
                                    for(int i=0; i<recycleViewAdapter.userList.length; i++){
                                        newList[i]=recycleViewAdapter.userList[i];
                                    }
                                    newList[newList.length-1]=username;
                                    recycleViewAdapter.userList=newList;
                                    recycleViewAdapter.notifyItemChanged(newList.length-1);
                                }else{
                                    Toast.makeText(context,"User with mail "+mail+ " does not exist",Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){}
                        }
                    });
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("action", "addUser");
                        jsonObject.put("mail", mail);
                        dataProvider.sendData(jsonObject);
                    } catch (Exception e) {

                    }


                }
                break;
        }
    }


}
