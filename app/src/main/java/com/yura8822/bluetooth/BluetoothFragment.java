package com.yura8822.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.yura8822.R;

public class BluetoothFragment extends Fragment {
    private static final String TAG = "BluetoothFragment";

    public interface OnBluetoothConnected{
        void onStateConnected();
    }
    private OnBluetoothConnected mOnBluetoothConnected;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    //Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    //Member object for the bluetooth services
    private BluetoothService mBTService = null;

    public BluetoothFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        FragmentActivity activity = getActivity();
        if (mBluetoothAdapter == null && activity != null) {
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnBluetoothConnected = (OnBluetoothConnected) getActivity();
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mBluetoothAdapter == null) {
            return;
        }
        // If BT is not on, request that it be enabled.
        // setupBT() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the bluetooth session
        } else if (mBTService == null) {
            setupBT();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBTService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth services
                mBTService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Deletes all messages in the queue of this Handler
        mHandler.removeCallbacksAndMessages(null);
    }

    private void setupBT() {
        Log.d(TAG, "setupChat()");
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        // Initialize the BluetoothChatService to perform bluetooth connections
        mBTService = new BluetoothService(activity, mHandler);
    }

    //Sends a message
    public void sendMessage(int[][] colorList) {
        // Check that we're actually connected before trying anything
        if (mBTService == null ||
                mBTService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        byte[] resultArray = new byte[677];
        int index = 0;
        resultArray[index] = (byte) 111;

        for (int i = 0; i < colorList.length; i++) {
            for (int j = 0; j < colorList[i].length; j++) {
                resultArray[++index] = (byte) Color.red(colorList[i][j]);
                resultArray[++index] = (byte) Color.green(colorList[i][j]);
                resultArray[++index] = (byte) Color.blue(colorList[i][j]);
            }
        }
        resultArray[++index] = (byte) 112;
        mBTService.write(resultArray);
    }

    //The Handler that gets information back from the BluetoothChatService
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    if (activity != null) { //temp !!!
                        switch (msg.arg1) {

                            case BluetoothService.STATE_CONNECTED:
                                mOnBluetoothConnected.onStateConnected();
                                Log.d(TAG, "MESSAGE_STATE_CHANGE: STATE_CONNECTED");
                                break;

                            case BluetoothService.STATE_CONNECTING:
                                mOnBluetoothConnected.onStateConnected();
                                Log.d(TAG, "MESSAGE_STATE_CHANGE: STATE_CONNECTING");
                                break;

                            case BluetoothService.STATE_NONE:
                                mOnBluetoothConnected.onStateConnected();
                                Log.d(TAG, "MESSAGE_STATE_CHANGE: STATE_NONE");
                                break;
                        }
                    }
                    break;

                case BluetoothService.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;

                case BluetoothService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    break;

                case BluetoothService.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String connectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + connectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case BluetoothService.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(BluetoothService.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT: {
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupBT();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        Toast.makeText(activity, R.string.bt_not_enabled_leaving,
                                Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                }
            }
        }
    }

    public void connectDevice(Intent data) {
        // Get the device MAC address
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        String address = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBTService.connect(device);
    }

    //Stop all threads
    public void stop() {
        if (mBTService != null) {
            mBTService.stop();
        }
    }

    public boolean getConnected() {
        if (mBTService.getState() == BluetoothService.STATE_CONNECTED) {
            return true;
        } else  {
            return false;
        }
    }
}


