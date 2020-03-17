package com.yura8822.device_search;

public class Device {
    private String name;
    private String address;
    private boolean state;

    public Device(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
