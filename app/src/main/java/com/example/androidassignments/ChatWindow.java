package com.example.androidassignments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {


    EditText messageText;
    Button sendButton;
    ListView listView;
    ArrayList<String> messages = new ArrayList<>();
    ChatDatabaseHelper myDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

         myDb=new ChatDatabaseHelper(this);
        Cursor answer = myDb.getData();
        Log.i("ChatWindow", "Cursor’s  column count =" + answer.getColumnCount() );
        if(answer.getCount()==0){
            Log.d("ChatWindow", "<NO MESSAGES YET>");
        }else{
            while(answer.moveToNext()) {
                Log.i("ChatWindow", "SQL MESSAGE:" + answer.getString( answer.getColumnIndex( ChatDatabaseHelper.COL_2) ) );
                messages.add(answer.getString(1));
            }
        }


        listView = findViewById(R.id.listView);
        sendButton = findViewById(R.id.sendButton);
        messageText = (EditText) findViewById(R.id.message);


        final ChatAdapter messageAdapter = new ChatAdapter(this, 0);
        listView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!messageText.getText().toString().equals("")){
                    messages.add(messageText.getText().toString());
                    myDb.insertData(messageText.getText().toString());
                    messageAdapter.notifyDataSetChanged();
                    messageText.setText("");
                }
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        myDb.close();
    }

    class ChatAdapter extends ArrayAdapter<String> {

        public ChatAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        public int getCount(){
            return messages.size();
        }

        public String getItem(int position){
            return messages.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();

            View result = null;
            if(position%2==0){
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            }else{
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
            }

            TextView msg = (TextView) result.findViewById(R.id.messageText);
            msg.setText(getItem(position));
            return result;
        }
    }
}