package com.example.filipzoricic.mychat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    Toolbar toolbar;
    Button buttonSend;
    ImageView buttonUpload;
    EditText editText;
    String to;
    String from;
    RecycleViewAdapterMessage recycleViewAdapterMessage;
    RecyclerView recyclerView;
    DatabaseHandler databaseHandler;
    BroadcastReceiver messegingServiceRecever;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.toolBar);

        editText = (EditText) findViewById(R.id.editText);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonUpload = (ImageView) findViewById(R.id.buttonUpload);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        buttonSend.setOnClickListener(this);
        databaseHandler = new DatabaseHandler(this);
        context = this;
        messegingServiceRecever = new MyBroadcastReceiver(this, new MyBroadcastReceiver.MyBrodcastReceiverInterface() {
            @Override
            public void OnLoad(MessageData messageData) {
                recycleViewAdapterMessage.list.add(messageData);
                recycleViewAdapterMessage.notifyItemInserted(recycleViewAdapterMessage.list.size() - 1);
                recyclerView.scrollToPosition(recycleViewAdapterMessage.list.size()-1);
            }
        });
         Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            to = bundle.getString("to");
            toolbar.setTitle(to);
        }
        from = databaseHandler.getUsername();
        linearLayoutManager = new LinearLayoutManager(this);

        recycleViewAdapterMessage = new RecycleViewAdapterMessage(from, to,this, linearLayoutManager);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recycleViewAdapterMessage);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp));
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE,PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Thread thread = databaseHandler.getMessagesThread(from, to, recycleViewAdapterMessage.list, new DatabaseHandler.OnLoadMessageInterface() {
                    @Override
                    public void onLoad(ArrayList<MessageData> newlist) {
                        swipeRefreshLayout.setRefreshing(false);
                        recycleViewAdapterMessage.notifyDataSetChanged();
                    }
                });
                thread.start();
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.listenerLayout);
        linearLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.i("qqq","qqqq "+recycleViewAdapterMessage.getItemCount());
                linearLayoutManager.scrollToPosition(recycleViewAdapterMessage.getItemCount()-1);
            }
        });
    }


    @Override
    public void onClick(View v) {
        String text  = editText.getText().toString();
        editText.setText("");
        MessageData messageData = new MessageData(text,from,to,0,"text");
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action","sendMessage");
            jsonObject.put("data",messageData.getData());
            jsonObject.put("to",messageData.getTo());
            jsonObject.put("from",messageData.getFrom());
            jsonObject.put("type",messageData.getType());
            final int id = addItem(messageData);
            final int position = recycleViewAdapterMessage.list.size()-1;
            DataProvider dataProvider = new DataProvider(this, new DataProvider.DataProviderInterface() {
                @Override
                public void onLoad(JSONObject jsonObject) {
                    if(jsonObject!=null) {
                        databaseHandler.changeMessageStatus(id, 1);
                        recycleViewAdapterMessage.list.get(position).setStatus(1);
                        recycleViewAdapterMessage.notifyItemChanged(position);
                    }
                }
            });
            dataProvider.sendData(jsonObject);
        }catch (Exception e){
            Log.i("eee",e.toString());}

    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(messegingServiceRecever, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(messegingServiceRecever);
    }

    public int addItem(MessageData messageData){
        Log.i("qqqq",messageData.getData());
        recycleViewAdapterMessage.list.add(messageData);
        recycleViewAdapterMessage.notifyItemInserted(recycleViewAdapterMessage.list.size() - 1);
        recyclerView.scrollToPosition(recycleViewAdapterMessage.list.size()-1);
        return databaseHandler.putMessage(messageData);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.i("fff","orginal w:"+ bitmap.getWidth() + " h:"+bitmap.getHeight());
                if(bitmap.getHeight()>=bitmap.getWidth()){
                    float width=(float)640/bitmap.getHeight();
                    bitmap= getResizedBitmap(bitmap, Math.round(bitmap.getWidth()*width), 640);
                    Log.i("fff","1 w:"+bitmap.getWidth()*width + " h:"+640 +" --- "+width+" * "+bitmap.getWidth());
                }else{
                    float height=(float)640/bitmap.getWidth();
                    bitmap= getResizedBitmap(bitmap, 640, Math.round(bitmap.getHeight()*height));
                    Log.i("fff","2 w:"+640 + " h:"+bitmap.getHeight()*height);
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();
                String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);

                DataProvider dataProvider = new DataProvider(this,new DataProvider.DataProviderInterface(){

                    @Override
                    public void onLoad(JSONObject jsonObject) {
                        try{
                            Log.i("fff",jsonObject.getString("success"));
                        }catch (Exception e){
                            Log.i("fff",e.toString());
                        }
                    }
                });
                MessageData messageData = new MessageData(encoded,from, to, 1, "image");
                messageData.bitmap=bitmap;

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action","sendMessage");
                jsonObject.put("data",messageData.getData());
                jsonObject.put("to",messageData.getTo());
                jsonObject.put("from",messageData.getFrom());
                jsonObject.put("type",messageData.getType());
                dataProvider.sendData(jsonObject);

                messageData.setData(uri.getPath());
                messageData.setData(DatabaseHandler.saveBitmap(bitmap, "send",this));
                addItem(messageData);
            } catch (Exception e) {
                Log.i("eeer",e.toString());
            }
        }


    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }




}
