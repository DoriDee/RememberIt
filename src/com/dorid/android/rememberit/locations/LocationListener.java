package com.dorid.android.rememberit.locations;

/**
 * Created by Dori on 6/29/13.
 */
public interface LocationListener
{
    void locationSelected(int locationId);
    void locationEdit(int locationId);

    //void deleteLocation(int locationId);
    //void updateWifiInfo(LocationData locationData);
}