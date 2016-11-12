package com.example.filipzoricic.mychat;

import android.content.Context;
import android.net.Uri;
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

import org.json.JSONObject;

import java.util.ArrayList;


public class MessagesListFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView;
    RecycleViewAdapter2 recycleViewAdapter;
    FloatingActionButton button;
    LinearLayout linearLayout;
    TextView textView;
    Context context;
    Button buttonAdd;
    DatabaseHandler databaseHandler;


    public MessagesListFragment() {
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

        recycleViewAdapter = new RecycleViewAdapter2();
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        textView = (TextView) view.findViewById(R.id.editText);

        return view;
    }

    public void setMesageDatas(ArrayList<MessageData> messageDatas){
        recycleViewAdapter.setMesageDatas(messageDatas);
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseHandler.MessagesThread messagesThread= new DatabaseHandler.MessagesThread(new DatabaseHandler.OnLoadMessageInterface() {
            @Override
            public void onLoad(ArrayList<MessageData> newlist) {
                recycleViewAdapter.setNewData(newlist);
                Log.i("eee3","postavio");
            }
        },databaseHandler);
        messagesThread.execute();
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
                String mail=textView.getText().toString();
                textView.setText("");
                if(!mail.equals("")) {
                    DataProvider dataProvider = new DataProvider(context, new DataProvider.DataProviderInterface() {
                        @Override
                        public void onLoad(JSONObject jsonObject) {
                            try{
                                if(jsonObject.getInt("success")==1){
                                    databaseHandler.putUser(jsonObject.getString("username"));
                                }else{
                                    Toast.makeText(context,"User with mail "+jsonObject.getString("username")+ " does not exist",Toast.LENGTH_LONG).show();
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
