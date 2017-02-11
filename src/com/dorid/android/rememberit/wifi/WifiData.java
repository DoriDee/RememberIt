package com.dorid.android.rememberit.wifi;

import java.util.HashMap;
import java.util.Map;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.os.Parcelable;

public class WifiData implements Parcelable{

	private String ssid;	
	private String bssid;
	private int signalStrength;


	public WifiData()
	{
		
	}
	
	public WifiData(String bssid, int signalStrength)
	{
		this.bssid = bssid;
		this.signalStrength = signalStrength;
	}
	
	public WifiData(Parcel p)
	{
		readFromParcel(p);
	}
	
	@Override
	public String toString()
	{
		return bssid + "_" + signalStrength;
	}
	
	static public Map<String, Integer> parseStringToWifisData(String data)
	{
		String[] wifisStrings = data.split(";");
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for (int i = 0; i < wifisStrings.length; ++i)
		{
			WifiData wifiData = new WifiData();
			wifiData.parseFromString(wifisStrings[i]);
			
			map.put(wifiData.getBSSID(), wifiData.getSignalStrength());
		}			
		
		return map;
	}
	
	public void parseFromString(String data)
	{
		String[] splitData = data.split("_");
		
		bssid = splitData[0];
		signalStrength = Integer.valueOf(splitData[1]);
	}
	
	public int getWifiSignalStrength(int rssi){
        int MIN_RSSI        = -100;
        int MAX_RSSI        = -55;  
        int levels          = 101;
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            return WifiManager.calculateSignalLevel(rssi, levels);
        } else {             
            // this is the code since 4.0.1
            if (rssi <= MIN_RSSI) {
                return 0;
            } else if (rssi >= MAX_RSSI) {
                return levels - 1;
            } else {
                float inputRange = (MAX_RSSI - MIN_RSSI);
                float outputRange = (levels - 1);
                return (int)((float)(rssi - MIN_RSSI) * outputRange / inputRange);
            }
        }
    }//end method

	public void parseFromScanResult(ScanResult scanResult)
	{
		ssid = scanResult.SSID;
		bssid = scanResult.BSSID;
		signalStrength = getWifiSignalStrength(scanResult.level);
		
	}
	
	public String getSSID() {
		return ssid;
	}
	
	public void setSSID(String sSID) {
		ssid = sSID;
	}
	
	public String getBSSID() {
		return bssid;
	}
	
	public void setBSSID(String bSSID) {
		bssid = bSSID;
	}
	
	public int getSignalStrength() {
		return signalStrength;
	}
	
	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(ssid);
		arg0.writeString(bssid);
		arg0.writeInt(signalStrength);
	}

	private void readFromParcel(Parcel p)
	{
		ssid = p.readString();
		bssid = p.readString();
		signalStrength = p.readInt();
	}
			
	public static final Parcelable.Creator<WifiData> CREATOR =
            new Parcelable.Creator<WifiData>() {
                public WifiData createFromParcel(Parcel in) {
                    return new WifiData(in);
                }

                public WifiData[] newArray(int size) {
                    return new WifiData[size];
                }
            };

}
