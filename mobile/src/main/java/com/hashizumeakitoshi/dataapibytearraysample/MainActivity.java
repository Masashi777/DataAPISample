package com.hashizumeakitoshi.dataapibytearraysample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    int counter = 0;

    EditText editText1;
    EditText editText2;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText1 = (EditText)findViewById(R.id.editText1);
        editText2 = (EditText)findViewById(R.id.editText2);

        readyAPI();
    }


    public void share(View v){

        String DATA_PATH = "/path";


        // DataMap object の生成、毎回違う値を送信
        DataMap dataMap = new DataMap();
        dataMap.putString("CountData", String.valueOf(counter));
        counter++;


        byte[] bytes = new byte[0];
        Model model = new Model(editText1.getText().toString(),editText2.getText().toString());

        try {
            bytes = ByteConverter.fromObject(model);
            dataMap.putByteArray("data", bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // UI がブロックするかもしれないので新しくThreadを立てる
        new SendToDataLayerThread(DATA_PATH, dataMap).start();


    }

    public void readyAPI() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("TAG", "onConnectionFailed()");
    }

    //DataAPIデータ送信用
    // data layer へ送るための Thread
    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;


        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
            for (Node node : nodes.getNodes()) {

                PutDataMapRequest putDMR = PutDataMapRequest.create(path);

                putDMR.getDataMap().putAll(dataMap);
                PutDataRequest request = putDMR.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleApiClient, request).await();

                if (result.getStatus().isSuccess()) {
                    Log.d("TAG", "DataMap: " + dataMap + " sent to: " + node.getDisplayName());
                } else {
                    Log.d("TAG", "ERROR");
                }
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    // 接続時
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("TAG", "onConnected()");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG", "onConnectionSuspended()");
    }
    // Activity が止まったら切断する
    @Override
    protected void onStop() {
        if (null != googleApiClient && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }


}
