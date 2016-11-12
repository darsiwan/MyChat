
package com.example.filipzoricic.mychat;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Hashtable;


public class DataProvider implements Response.Listener<JSONObject>, Response.ErrorListener {

    public static interface DataProviderInterface{
        public void onLoad(JSONObject jsonObject);
    };

    String url="http://192.168.5.14/MyChat/recive.php";
    Context context;
    DataProviderInterface dataProviderInterface;

    public DataProvider(Context context, DataProviderInterface dataProviderInterface ){
        this.context = context;
        this.dataProviderInterface = dataProviderInterface;
    }

    public DataProvider(Context context){
        this.context = context;
    }



    public void sendData(JSONObject params){
        RequestQueue queue = Volley.newRequestQueue(context);

        Log.i("eeetttt", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,params,this,this);

        queue.add(jsObjRequest);
    };


    @Override
    public void onResponse(JSONObject response) {
        dataProviderInterface.onLoad(response);

    }



    @Override
    public void onErrorResponse(VolleyError error) {

        Log.i("eee", error.toString());
        if(dataProviderInterface!=null)
            dataProviderInterface.onLoad(null);
    }
}
