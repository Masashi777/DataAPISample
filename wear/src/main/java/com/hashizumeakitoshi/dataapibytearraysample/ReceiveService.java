package com.hashizumeakitoshi.dataapibytearraysample;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;


public class ReceiveService extends WearableListenerService {

    private static final String DATA_PATH = "/path";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("ReceiceService", "DonDataChanged() ");

        DataMap dataMap;
        for (DataEvent event : dataEvents) {

            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(DATA_PATH)) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.d("ReceiceService", "DataMap received on watch: " + dataMap);

                    byte[] bytes = dataMap.getByteArray("data");


                    // Local Broadcast
                    Intent messageIntent = new Intent();
                    messageIntent.setAction(Intent.ACTION_SEND);
                    messageIntent.putExtra("receivedData",bytes);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

                }


            }
        }
    }

}