package com.example.filipzoricic.mychat;

/**
 * Created by filipzoricic on 10/15/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MyChat";
    private static final String TABLE_NAME = "setup";
    private static final String TABLE_NAME2 = "message";
    private static final String TABLE_NAME3 = "user";
    private static final String KEY_ID = "id";
    private static final String KEY_STATUS = "status";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FROM = "user_from";
    private static final String KEY_TO = "user_to";
    private static final String KEY_DATA = "data";
    private static final String KEY_TYPE = "type";
    //private static final String KEY_VALUE = "favorite";
    //private static final String KEY_POSITION = "position";

    public interface OnLoadMessageInterface{
        public void onLoad(ArrayList<MessageData> newlist);
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table "+TABLE_NAME+ " ("+KEY_ID +" INTEGER PRIMARY KEY, "+KEY_USERNAME+" varchar(20))";
        Log.e("eee","STVARAM");
        db.execSQL(sql);
        sql="insert into "+TABLE_NAME+" ("+KEY_ID+") values ("+1+")";
        db.execSQL(sql);
        sql = "create table "+TABLE_NAME2+ " ("+KEY_ID +" INTEGER PRIMARY KEY, "+KEY_FROM+" varchar(20), "+KEY_TO+" varchar(20), "+KEY_DATA+" text, "+KEY_TYPE+" text," +KEY_STATUS+" INTEGER)";
        db.execSQL(sql);
        sql = "create table "+TABLE_NAME3+ " ("+KEY_USERNAME+" varchar(20) PRIMARY KEY)";
        Log.i("eeeeee",sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME3);
        onCreate(db);
    }

    public void setUsername(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="update "+TABLE_NAME+" set "+KEY_USERNAME+"='"+username+"' where id=1";
        db.execSQL(sql);
        db.close(); // Closing database connection
    }

    public String getUsername() {
        SQLiteDatabase db = this.getWritableDatabase();
        String id="";

        String sql="select * from "+TABLE_NAME+" where "+KEY_ID+"= 1";
        Cursor cursor =  db.rawQuery(sql, null);
        if(cursor.moveToFirst())
            id=cursor.getString(1);
        else
            Log.i("eee","nema nista");

        db.close(); // Closing database connection
        return id;

    }

    public Thread getMessagesThread(final String from, final String to, final ArrayList<MessageData> oldList, final DatabaseHandler.OnLoadMessageInterface onLoadMessageInterface){

        return new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<MessageData> newlist= getMessages(from, to, oldList);
                onLoadMessageInterface.onLoad(newlist);
            }
        });

    }


    public AsyncTask saveMessages(){


        class MyThread extends AsyncTask<Object,Void, ArrayList<MessageData>>{
            OnLoadMessageInterface onLoadMessageInterface;
            @Override
            protected ArrayList<MessageData> doInBackground(Object... params) {
                JSONArray jsonArray=null;
                Activity activity=null;
                ArrayList<MessageData> messageDatas = new ArrayList<MessageData>();
                try{
                    jsonArray = (JSONArray) params[0];
                    onLoadMessageInterface = (OnLoadMessageInterface) params[1];
                    activity = (Activity) params[2];
                    if(activity==null){
                        Log.e("WWWWW","NULL");
                    }
                }catch (Exception e){}

                if(onLoadMessageInterface!=null&&jsonArray!=null){
                    for(int i=0; i<jsonArray.length(); i++){
                        try {
                            JSONObject jsonObject = (jsonArray.getJSONObject(i));
                            MessageData messageData = new MessageData();
                            messageData.setFrom(jsonObject.getString("from"));
                            messageData.setTo(jsonObject.getString("to"));
                            messageData.setType(jsonObject.getString("type"));
                            if(messageData.getType().equals("image")){
                                byte[] bytes = Base64.decode(jsonObject.getString("data"),Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                messageData.bitmap=bitmap;
                                messageData.setData(DatabaseHandler.saveBitmap(bitmap,"recived", activity));
                            }else{
                                messageData.setData(jsonObject.getString("data"));
                            }
                            messageData.setStatus(2);
                            putMessage(messageData);
                            messageDatas.add(messageData);
                        }catch (Exception e){Log.e("qqq",e.toString());}
                    }

                }
                Log.i("eee","eee");
                return messageDatas;
            }

            @Override
            protected void onPostExecute(ArrayList<MessageData> messageDatas) {
                super.onPostExecute(messageDatas);
                onLoadMessageInterface.onLoad(messageDatas);
            }
        }

       return new MyThread();
    }

    public ArrayList<MessageData> getMessages(String from, String to, ArrayList<MessageData> oldList){

        SQLiteDatabase db = this.getWritableDatabase();
        String id="";

        String sql="select * from "+TABLE_NAME2+" where "+KEY_FROM+"='"+from+"' and "+KEY_TO+"= '"+to+"' or "+KEY_FROM+"='"+to+"' and "+KEY_TO+"= '"+from+"' order by id desc limit "+oldList.size()+",10";
        Cursor cursor =  db.rawQuery(sql, null);
        Log.i("eee","broj "+cursor.getCount());



        if(cursor.moveToLast()) {
            int i=0;
            int from_num = 1;
            int to_num = 2;
            int data_num = 3;
            int type_num = 4;
            int status_num = 5;
            do{
                MessageData messageData = new MessageData();
                messageData.setStatus(cursor.getInt(status_num));
                messageData.setData(cursor.getString(data_num));
                messageData.setFrom(cursor.getString(from_num));
                messageData.setTo(cursor.getString(to_num));
                messageData.setType(cursor.getString(type_num));
                oldList.add(i, messageData);
                //Log.i("eee="+i,cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3));
                i+=1;
            }
            while (cursor.moveToPrevious());
            db.close();


        }
        return oldList;
    }



    public synchronized int putMessage(MessageData messageData){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="insert into "+TABLE_NAME2+" ("+KEY_FROM+", "+KEY_TO+", "+KEY_DATA+", "+KEY_TYPE+", "+KEY_STATUS+") values ('"+messageData.getFrom()+"', '"+messageData.getTo()+"', '"+messageData.getData()+"', '"+messageData.getType()+"',"+messageData.getStatus()+")";
        db.execSQL(sql);
        Log.i("qqqq",messageData.getData());
        sql="select "+KEY_ID+" from "+TABLE_NAME2+" ORDER BY "+KEY_ID+" DESC LIMIT 1";
        Cursor cursor =  db.rawQuery(sql, null);
        int id=0;
        if(cursor.moveToFirst()){
            int id_position= 0;
            id=cursor.getInt(id_position);
        }

        db.close(); // Closing database connection
        return id;
    }

    public void putUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "insert into "+TABLE_NAME3+" ("+KEY_USERNAME+") values ('"+username+"')";
        Log.i("eee11",sql);
        db.execSQL(sql);
    }

    public String[] getUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] userList = null;
        String sql="select * from "+TABLE_NAME3;
        Cursor cursor =  db.rawQuery(sql, null);


        if(cursor.moveToFirst()) {
            userList = new String[cursor.getCount()];
            int i=0;
            do {
                userList[i++]=cursor.getString(0);
            }
            while (cursor.moveToNext());
            db.close();
        }else{
            userList = new String[0];
        }

        return userList;
    }

    public String[] getUsersFromMessages(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] userList = null;
        String sql="SELECT DISTINCT "+KEY_FROM+" from "+TABLE_NAME2+" where "+KEY_FROM+"!='"+username+"'";
        Cursor cursor =  db.rawQuery(sql, null);

        if(cursor!=null) {
            if(cursor.moveToFirst()) {
                userList = new String[cursor.getCount()];
                int i=0;
                do {
                    userList[i++]=cursor.getString(0);
                }
                while (cursor.moveToNext());
                db.close();
            }else{
                userList = new String[0];
            }
        }


        return userList;
    }

    public ArrayList<MessageData> getMessagesByContacts(){
        String username = getUsername();
        String[] users = getUsersFromMessages(username);
        SQLiteDatabase db = this.getWritableDatabase();
        String sql;
        ArrayList<MessageData> messageDatas = new ArrayList<MessageData>();
        for(int i =0; i<users.length; i++){
            sql ="select "+KEY_DATA+", "+KEY_TYPE+" from "+TABLE_NAME2+" where "+KEY_FROM+"='"+users[i]+ "' or "+KEY_TO+ " = '"+users[i]+"' order by "+KEY_ID+" desc limit 1";
            Cursor cursor =  db.rawQuery(sql, null);
            if(cursor.moveToFirst()){
                MessageData messageData = new MessageData();
                messageData.setData(cursor.getString(0));
                messageData.setType(cursor.getString(1));
                messageData.setFrom(users[i]);
                messageDatas.add(messageData);

            }
        }
        db.close();

        return messageDatas;
    }




    public void changeMessageStatus(int id, int status){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="update "+TABLE_NAME2+" set "+KEY_STATUS+" = "+status+" where "+KEY_ID+"="+id;
        db.execSQL(sql);
        db.close();
    }

    static class MessagesThread extends AsyncTask<Void, Void, ArrayList<MessageData>>{

        DatabaseHandler.OnLoadMessageInterface onLoadMessageInterface;
        DatabaseHandler databaseHandler;

        public MessagesThread(DatabaseHandler.OnLoadMessageInterface onLoadMessageInterface, DatabaseHandler databaseHandler){
            this.onLoadMessageInterface = onLoadMessageInterface;
            this.databaseHandler = databaseHandler;
        }

        @Override
        protected ArrayList<MessageData> doInBackground(Void... params) {
            return databaseHandler.getMessagesByContacts();
        }

        @Override
        protected void onPostExecute(ArrayList<MessageData> messageDatas) {
            super.onPostExecute(messageDatas);
            onLoadMessageInterface.onLoad(messageDatas);
        }
    }

    public static String saveBitmap(Bitmap imageToSave, String type, Context context) {
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR);
        int minutest = c.get(Calendar.MINUTE);
        int secundes = c.get(Calendar.SECOND);
        String path="Error Image";
        int permission = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            File folder = new File(Environment.getExternalStorageDirectory() + "/Pictures/myChat/myChat"+type);
            boolean success = true;
            if (!folder.exists()) {
                folder.mkdir();
            }
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                imageToSave.compress(Bitmap.CompressFormat.PNG, 0, bytes);
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"/Pictures/myChat/myChat"+type+"/"+date+"_"+hour+"_"+minutest+"_"+secundes+".png");
                f.createNewFile();
                path=f.getPath();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
                context.sendBroadcast (new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
            } catch (Exception e) {
                Log.e("eee",e.toString());

            }
        }
        return  path;
    }
}

