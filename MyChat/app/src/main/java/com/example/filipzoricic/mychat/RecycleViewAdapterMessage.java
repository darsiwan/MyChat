package com.example.filipzoricic.mychat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipzoricic on 10/18/16.
 */
public class RecycleViewAdapterMessage extends RecyclerView.Adapter<RecycleViewAdapterMessage.MyViewHolder> {

    ArrayList<MessageData> list;
    String from;
    String to;
    DatabaseHandler databaseHandler;
    LinearLayoutManager linearLayoutManager;
    Context context;
    int margin_horizontal;
    int margin_vertical;
    int widthScreen;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        public String layout;

        public MyViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }



    public RecycleViewAdapterMessage (String from, String to, Context context, LinearLayoutManager linearLayoutManager){
        this.to = to;
        this.from = from;
        this.linearLayoutManager=linearLayoutManager;
        this.context=context;
        databaseHandler = new DatabaseHandler(context);
        list = databaseHandler.getMessages(from, to, new ArrayList<MessageData>());
        float scale = context.getResources().getDisplayMetrics().density;
        margin_horizontal = (int) (140 * scale + 0.5f);
        margin_vertical = (int) (2 * scale + 0.5f);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        widthScreen = display.getWidth();
    }



    @Override
    public RecycleViewAdapterMessage.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.basic_card_view_layout,parent,false);
        return new RecycleViewAdapterMessage.MyViewHolder(cardView);
    }


    @Override
    public void onBindViewHolder(RecycleViewAdapterMessage.MyViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView) cardView.findViewById(R.id.imageView);
        TextView textView = (TextView) cardView.findViewById(R.id.name);

        switch (list.get(position).getType()){
            case "text":
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)cardView.getLayoutParams();
                textView.setText(list.get(position).getData());
                switch (list.get(position).getStatus()){
                    case 0:
                        textView.setBackgroundColor(Color.GREEN);
                        layoutParams.setMargins(margin_horizontal,margin_vertical,margin_vertical,margin_vertical);
                        break;
                    case 1:
                        textView.setBackgroundColor(Color.rgb(200,229,255));
                        layoutParams.setMargins(margin_horizontal, margin_vertical,margin_vertical,margin_vertical);
                        break;
                    case 2:
                        layoutParams.setMargins(margin_vertical,margin_vertical,margin_horizontal,margin_vertical);
                        textView.setBackgroundColor(Color.rgb(255,255,255));
                        break;
                }
                break;
            case "image":
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                layoutParams = (ViewGroup.MarginLayoutParams)cardView.getLayoutParams();
                Bitmap bitmap = list.get(position).bitmap;
                if(bitmap!=null){
                    imageView.setImageBitmap(bitmap);
                }else{
                    bitmap =BitmapFactory.decodeFile(list.get(position).getData());
                    if(bitmap!=null)
                        imageView.setImageBitmap(bitmap);
                    else{
                        bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
                        imageView.setImageBitmap(bitmap);
                    }
                }

                switch (list.get(position).getStatus()){
                    case 0:
                        imageView.setBackgroundColor(Color.GREEN);
                        layoutParams.setMargins(widthScreen-(bitmap.getWidth()+margin_vertical),margin_vertical,margin_vertical,margin_vertical);
                        break;
                    case 1:
                        imageView.setBackgroundColor(Color.rgb(200,229,255));
                        layoutParams.setMargins(widthScreen-(bitmap.getWidth()+margin_vertical),margin_vertical,margin_vertical,margin_vertical);
                        break;
                    case 2:
                        layoutParams.setMargins(margin_vertical,margin_vertical,widthScreen-bitmap.getWidth(),margin_vertical);
                        imageView.setBackgroundColor(Color.rgb(255,255,255));
                        break;
                }
                break;
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
