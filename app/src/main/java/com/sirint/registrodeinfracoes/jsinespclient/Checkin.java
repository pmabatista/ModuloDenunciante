package com.sirint.registrodeinfracoes.jsinespclient;

import java.util.List;

public class Checkin {

    private String device_data_version_info;
    private boolean stats_ok;
    private long security_token;
    private long time_msec;
    private String version_info;
    private long android_id;
    private List<Action> intent;

    public String getDevice_data_version_info() {
        return device_data_version_info;
    }

    public boolean isStats_ok() {
        return stats_ok;
    }

    public long getSecurity_token() {
        return security_token;
    }

    public long getTime_msec() {
        return time_msec;
    }

    public String getVersion_info() {
        return version_info;
    }

    public long getAndroid_id() {
        return android_id;
    }

    public List<Action> getIntent() {
        return intent;
    }

    @Override
    public String toString() {
        return "Checkin{" + "device_data_version_info=" + device_data_version_info + ", stats_ok=" + stats_ok + ", security_token=" + security_token + ", time_msec=" + time_msec + ", version_info=" + version_info + ", android_id=" + android_id + ", intent=" + intent + '}';
    }


}
