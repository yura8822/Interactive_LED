package com.yura8822.device_search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yura8822.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceListFragment extends Fragment {
    private static final String TAG = "DeviceListFragment";

    private RecyclerView mPairedDevicesRecycler;
    private DevicesAdapter mPairedDevicesAdapter;

    private RecyclerView mNewDevicesRecycler;
    private DevicesAdapter mNewDevicesAdapter;

    private TextView mTitlePairedDevices;
    private TextView mTitleNewDevices;

    private List<Device> mDeviceListNew;
    private List<Device> mDeviceListPaired;

    public DeviceListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceListNew = new ArrayList<>();
        mDeviceListPaired = new ArrayList<>();

        for (int i = 0; i < 20; i++){
            Device deviceP = new Device("table interac amazing", "23:343:dfs:fdsg;");
            deviceP.setState(i%4 == 0);
            mDeviceListPaired.add(deviceP);
            Device deviceN = new Device("new np paired", "23:343:dfs:fdsg;");
            deviceN.setState(i%3 == 0);
            mDeviceListNew.add(deviceN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list,container, false);
        mTitleNewDevices = view.findViewById(R.id.title_new_devices);
        mTitlePairedDevices = view.findViewById(R.id.title_paired_devices);

        mPairedDevicesRecycler = view.findViewById(R.id.paired_devices);
        mPairedDevicesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPairedDevicesAdapter = new DevicesAdapter(mDeviceListPaired);
        mPairedDevicesRecycler.setAdapter(mPairedDevicesAdapter);

        mNewDevicesRecycler = view.findViewById(R.id.new_devices);
        mNewDevicesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNewDevicesAdapter = new DevicesAdapter(mDeviceListNew);
        mNewDevicesRecycler.setAdapter(mNewDevicesAdapter);

        return view;
    }

    private class DevicesHolder extends RecyclerView.ViewHolder{
        private TextView mName;
        private TextView mAddress;
        private ImageView mSignaleState;
        private Device mDevice;

        public DevicesHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name_devices);
            mAddress = itemView.findViewById(R.id.address_devices);
            mSignaleState = itemView.findViewById(R.id.state_devices);
        }

        public void bind(Device device){
            mDevice = device;
            mName.setText(mDevice.getName());
            mAddress.setText(mDevice.getAddress());
            mSignaleState.setImageDrawable(mDevice.isState() ?
            getResources().getDrawable(R.drawable.ic_bluetooth_siganl_on) : getResources().getDrawable(R.drawable.ic_bluetooth_siganl_off));
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
}
