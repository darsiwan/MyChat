package com.example.filipzoricic.mychat;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by filipzoricic on 10/30/16.
 */
public class RecycleViewAdapter2 extends RecyclerView.Adapter<RecycleViewAdapter2.MyViewHolder> {
    ArrayList<MessageData> messageDatas;

    public RecycleViewAdapter2(){
        messageDatas = new ArrayList<MessageData>();
    }

    public void setMesageDatas( ArrayList<MessageData> messageDatas){
        this.messageDatas = messageDatas;
        this.notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        public MyViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView =(CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_card_view_layout,parent,false);
        return new MyViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        try{
            MessageData messageData = messageDatas.get(position);
            final String name = messageData.getFrom();
            final String message;
            Log.i("qqq4",messageData.getType()+" l");
            Log.i("qqq4",messageData.getData()+" l");
            if(messageData.getType().equals("image")){
                message=messageData.getType();
            }else{
                message=messageData.getData();
            }
            final CardView cardView = holder.cardView;
            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(cardView.getContext(), ChatActivity.class);
                    i.putExtra("to",name);
                    cardView.getContext().startActivity(i);
                }
            });
            TextView textView = (TextView) cardView.findViewById(R.id.name);
            TextView textView2 = (TextView) cardView.findViewById(R.id.text);
            textView.setText(name);
            textView2.setVisibility(View.VISIBLE);
            textView2.setText(message);
        }catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        return messageDatas.size();
    }

    public void setNewData(ArrayList<MessageData> newlist){
        messageDatas=newlist;
        this.notifyDataSetChanged();
    }

}

