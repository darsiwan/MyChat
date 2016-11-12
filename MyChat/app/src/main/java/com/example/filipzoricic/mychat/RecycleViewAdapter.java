package com.example.filipzoricic.mychat;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;

/**
 * Created by filipzoricic on 10/18/16.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {
    String[] userList;

    public RecycleViewAdapter(String[] userList){
        this.userList=userList;
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
            final String name = userList[position];
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
            textView.setText(name);
        }catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        return userList.length;
    }




}
