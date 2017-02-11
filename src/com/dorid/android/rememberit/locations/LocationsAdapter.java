package com.dorid.android.rememberit.locations;

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dorid.android.rememberit.R;

/**
 * Created by Dori on 7/4/13.
 */
public class LocationsAdapter extends ArrayAdapter<LocationData>{

    // declaring our ArrayList of items
    private List<LocationData> mLocations;
    private Context mContext;
    private LocationListener mLocationsListener;
    private WifiManager mWifiManager;

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public LocationsAdapter(Context context, int textViewResourceId, List<LocationData> locations, LocationListener locationListener) {
        super(context, textViewResourceId, locations);
        this.mLocations = locations;
        this.mContext = context;
        this.mLocationsListener = locationListener;
        mWifiManager  = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    private static class ViewHolder {

        protected Button btnSetCurrent;
        protected ImageView icon;
        protected TextView txtLocation;
        protected TextView wifiName;
        protected ImageView btnEdit;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder holder;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.location_item, null);

            holder = new ViewHolder();
            holder.txtLocation = (TextView)convertView.findViewById(R.id.txtLocationName);
            holder.icon = (ImageView)convertView.findViewById(R.id.locationIcon);
            holder.wifiName = (TextView)convertView.findViewById(R.id.txtWifiName);
            holder.btnEdit = (ImageView)convertView.findViewById(R.id.btnEdit);
            //holder.btnSetCurrent = (Button)convertView.findViewById(R.id.btnSetCurrent);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        final LocationData locationData = mLocations.get(position);

        if (locationData != null) {
            holder.txtLocation.setText(locationData.getLocationName());
            holder.wifiName.setText(locationData.getWifiName());
            holder.icon.setImageResource(locationData.getIconId());

            final int locationId = locationData.getLocationId();
            
//            if (convertView.isSelected())
//            {
//            	convertView.setBackgroundColor(0xff33b5e5);
//            }
//            else
//            {
//            	convertView.setBackgroundColor(Color.WHITE);
//            }
  /*          
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        //view.setBackgroundColor(0xD3D3D3);
                        //view.setBackgroundColor(Color.LTGRAY);
                    	view.setBackgroundColor(0xff33b5e5);
                    }
                    else if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                             motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT || 
                             motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE)
                    {
                        view.setBackgroundColor(Color.WHITE);
                    }
                    return false;
                }
            });
*/
            
            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLocationsListener.locationEdit(locationId);
                }
            });
            
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLocationsListener.locationSelected(locationId);
                }
            });

            /*Button btnSetCurrent = (Button)holder.btnSetCurrent;


            String currBssid = locationData.getBssid();

            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

            // If this is the same bssid we will disable the set current button
            if (wifiInfo.getBSSID() == null || wifiInfo.getBSSID().equals(currBssid))
            {
                btnSetCurrent.setEnabled(false);
            }
            else
            {
                btnSetCurrent.setEnabled(true);
            }

            btnSetCurrent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mLocationsUpdater.updateWifiInfo(locationData);
                }
            });*/
        }


        // the view must be returned to our activity
        return convertView;
    }
}
