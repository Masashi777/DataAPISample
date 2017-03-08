package com.hashizumeakitoshi.dataapibytearraysample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends Activity {

    private TextView mTextView1;
    private TextView mTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView1 = (TextView) stub.findViewById(R.id.text);
                mTextView2 = (TextView) stub.findViewById(R.id.text2);
            }
        });
        ready();
    }

    public void ready() {

        //DataAPIデータ受信用
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }

    //以下DataPI用
    //DataAPIデータ受信用
    public class MessageReceiver extends BroadcastReceiver {
        private static final String TAG = "MessageReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

           Model model = null;

            Log.d(TAG, "onReceive() ");

            byte[] bytes = intent.getByteArrayExtra("receivedData");


            try {
                model = (Model) ByteConverter.toObject(bytes);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mTextView1.setText(model.getText1());
            mTextView2.setText(model.getText2());

        }
    }
}
