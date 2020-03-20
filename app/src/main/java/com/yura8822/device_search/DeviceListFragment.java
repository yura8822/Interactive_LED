package com.yura8822.device_search;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yura8822.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceListFragment extends Fragment {
    private static final String TAG = "DeviceListFragment";

    private RecyclerView mPairedDevicesRecycler;
    private DevicesAdapter mPairedDevicesAdapter;

    private RecyclerView mNewDevicesRecycler;
    private DevicesAdapter mNewDevicesAdapter;

    private TextView mTitlePairedDevices;
    private TextView mTitleNewDevices;
    private ImageView mDropPairedDevice;
    private ImageView mDropNewDevice;

    private List<Device> mDeviceListNew;
    private List<Device> mDeviceListPaired;

    private BluetoothAdapter mBtAdapter;
    private Button mScanButton;

    public DeviceListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceListNew = new ArrayList<>();
        mDeviceListPaired = new ArrayList<>();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list,container, false);

        mDropNewDevice = view.findViewById(R.id.drop_new_device);
        mDropPairedDevice = view.findViewById(R.id.drop_paired_device);

        mTitleNewDevices = view.findViewById(R.id.title_new_devices);
        mTitleNewDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNewDevicesRecycler.getVisibility() == View.VISIBLE){
                    mDropNewDevice.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_down));
                    mNewDevicesRecycler.setVisibility(View.GONE);
                }else {
                    mDropNewDevice.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_up));
                    mNewDevicesRecycler.setVisibility(View.VISIBLE);
                }
            }
        });
        mTitlePairedDevices = view.findViewById(R.id.title_paired_devices);
        mTitlePairedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPairedDevicesRecycler.getVisibility() == View.VISIBLE){
                    mDropPairedDevice.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_down));
                    mPairedDevicesRecycler.setVisibility(View.GONE);
                }else {
                    mDropPairedDevice.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_up));
                    mPairedDevicesRecycler.setVisibility(View.VISIBLE);
                }
            }
        });

        mPairedDevicesRecycler = view.findViewById(R.id.paired_devices);
        mPairedDevicesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPairedDevicesAdapter = new DevicesAdapter(mDeviceListPaired);
        mPairedDevicesRecycler.setAdapter(mPairedDevicesAdapter);

        mNewDevicesRecycler = view.findViewById(R.id.new_devices);
        mNewDevicesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNewDevicesAdapter = new DevicesAdapter(mDeviceListNew);
        mNewDevicesRecycler.setAdapter(mNewDevicesAdapter);

        mScanButton = view.findViewById(R.id.button_scan);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessLocationPermission();
                searchPairedDevices();
                doDiscovery();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);

        searchPairedDevices();
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        getActivity().unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, intent.getAction());

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (bluetoothDevice != null && bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mDeviceListNew.add(0, new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                    mNewDevicesAdapter.notifyItemInserted(0);
                    mNewDevicesRecycler.scrollToPosition(0);
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mScanButton.setEnabled(true);
            }

            updateUI();
        }
    };

    private class DevicesHolder extends RecyclerView.ViewHolder{
        private TextView mName;
        private TextView mAddress;
        private Device mDevice;

        public DevicesHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name_devices);
            mAddress = itemView.findViewById(R.id.address_devices);
        }

        public void bind(Device device){
            mDevice = device;
            mName.setText(mDevice.getName());
            mAddress.setText(mDevice.getAddress());
        }
    }

    private class DevicesAdapter extends RecyclerView.Adapter<DevicesHolder>{
        private List<Device> mDevicesList;

        public DevicesAdapter(List<Device> devicesList){
            mDevicesList = devicesList;
        }

        @NonNull
        @Override
        public DevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_device, parent, false);
            return new DevicesHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DevicesHolder holder, int position) {
            holder.bind(mDevicesList.get(position));
        }

        @Override
        public int getItemCount() {
            return mDevicesList.size();
        }

        public void setDevicesList(List<Device> devicesList) {
            mDevicesList = devicesList;
        }
    }

    private void accessLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        permissionCheck += ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck != 0){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
    }

    private void updateUI(){
        if (mBtAdapter.getBondedDevices().size() == 0){
            mTitlePairedDevices.setText(R.string.none_paired);
        }else {
            mTitlePairedDevices.setText(R.string.title_paired_devices);
        }

        if (mDeviceListNew.size() == 0){
            mTitleNewDevices.setText(R.string.none_found);
        }else {
            mTitleNewDevices.setText(R.string.title_other_devices);
        }
    }

    private void searchPairedDevices() {
        mDeviceListPaired.clear();
        mPairedDevicesAdapter.notifyDataSetChanged();

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mDeviceListPaired.add(new Device(device.getName(), device.getAddress()));
            }
            mPairedDevicesAdapter.notifyDataSetChanged();
        }
    }

    private void doDiscovery() {
        mScanButton.setEnabled(false);
        mTitleNewDevices.setText(R.string.scanning);
        mDeviceListNew.clear();
        mNewDevicesAdapter.notifyDataSetChanged();
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }
}
