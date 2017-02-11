package com.dorid.android.rememberit.locations;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dorid.android.rememberit.R;
import com.dorid.android.rememberit.database.LocationsTable;

/**
 * Created by Dori on 6/22/13.
 */
public class LocationsCursorAdapter extends CursorAdapter
{
    private final Context context;
    private  ViewHolder holder;
    private LayoutInflater mInflater;
    private int layoutResource;
    private LocationListener locationListener;
    private boolean mSetCurrentEnabled;

    public LocationsCursorAdapter(Context context, Cursor c, int resourceId, LocationListener locationListener) {
        super(context, c, resourceId);

        this.mCursor = c;
        this.context = context;
        this.locationListener = locationListener;
        this.mInflater = LayoutInflater.from(context);

        this.layoutResource=resourceId;
        mSetCurrentEnabled = false;
    }

    private static class ViewHolder {
        protected ImageView icon;
        protected TextView txtLocation;
        protected EditText editLocation;
        protected TextView wifiName;
    }

    public void enableSetCurrentButtons() {
        mSetCurrentEnabled = true;
        notifyDataSetChanged();
    }

    public List<LocationData> getLocations() {
        List<LocationData> locations = new ArrayList<LocationData>();

        Cursor c = getCursor();

        if (c.moveToFirst())
        {
            do
            {
                locations.add(parseLocationData(c));
            } while (c.moveToNext());
        }

        return locations;
    }

    // This will return the name of the location this bssid is assigned to
    // if no such location exist return null
    public LocationData isBSSIDAssigned(String bssid)
    {
        Cursor c = getCursor();

        if (c.moveToFirst())
        {
            do
            {
                String currBssid = c.getString(mCursor.getColumnIndex(LocationsTable.COLUMN_BSSID));

                if (currBssid != null && currBssid.equals(bssid))
                {
                    return parseLocationData(c);
                }
            } while (c.moveToNext());
        }

        return null;
    }

    private LocationData parseLocationData(Cursor cursor)
    {
        int locationId = cursor.getInt(cursor.getColumnIndex(LocationsTable.COLUMN_ID));
        String locationName = cursor.getString(cursor.getColumnIndex(LocationsTable.COLUMN_NAME));
        int iconId = cursor.getInt(cursor.getColumnIndex(LocationsTable.COLUMN_ICON_ID));
        String ssid = cursor.getString(cursor.getColumnIndex(LocationsTable.COLUMN_SSID));
        String bssid = cursor.getString(cursor.getColumnIndex(LocationsTable.COLUMN_BSSID));

        return new LocationData(locationId, locationName, iconId, ssid, bssid);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        holder = (ViewHolder) view.getTag();

        holder.txtLocation.setText(cursor.getString(cursor.getColumnIndex(LocationsTable.COLUMN_NAME)));

        // Hack for the icons not to scramble
        if (holder.txtLocation.equals(LocationsTable.HOME_NAME))
        {
            holder.icon.setImageResource(R.drawable.ic_action_home);
        }
        else if (holder.txtLocation.equals(LocationsTable.WORK_NAME))
        {
            holder.icon.setImageResource(R.drawable.ic_action_business);
        }

        //holder.icon.setImageResource(cursor.getInt(cursor.getColumnIndex(LocationsTable.COLUMN_ICON_ID)));
        holder.wifiName.setText(cursor.getString(cursor.getColumnIndex(LocationsTable.COLUMN_SSID)));

        //Button btnSetCurrent = (Button)view.findViewById(R.id.btnSetCurrent);

        final int rowId = cursor.getInt(cursor.getColumnIndex("_id"));

        String currBssid = cursor.getString(cursor.getColumnIndex(LocationsTable.COLUMN_BSSID));

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // If this is the same bssid we will disable the set current button
        /*if (wifiInfo.getBSSID() != null && wifiInfo.getBSSID().equals(currBssid))
        {
            btnSetCurrent.setEnabled(false);
        }
        else
        {
            btnSetCurrent.setEnabled(mSetCurrentEnabled);
        }


        final Cursor c = cursor;

        btnSetCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationListener.updateWifiInfo(parseLocationData(c));
            }
        });
*/
/*
        Button btnDelLocation = (Button)view.findViewById(R.id.btnDelLocation);

        // Do not allow deletion of home or office
        if (rowId == LocationsTable.HOME_ID || rowId == LocationsTable.WORK_ID)
        {
            btnDelLocation.setVisibility(View.INVISIBLE);
        }
        else
        {
            final TextView txtLoc = (TextView)view.findViewById(R.id.txtLocationName);
            final EditText editLoc = (EditText)view.findViewById(R.id.edit_location);

            txtLoc.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    editLoc.setVisibility(View.VISIBLE);
                    editLoc.setText(txtLoc.getText());
                    txtLoc.setVisibility(View.GONE);
                }
            });

            editLoc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        doneEditingLocation(txtLoc, editLoc);
                        return true;
                    }
                    return false;
                }
            });

            editLoc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    Log.e("bla", "onFocusChange=" + hasFocus);
                    // If we have lost focus we will return to the text view
                    // with the new text
                    if (!hasFocus)
                    {
                        doneEditingLocation(txtLoc, editLoc);
                    }
                }
            });

            btnDelLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    locationListener.deleteLocation(rowId);
                }
            });
        }*/
    }

    private void doneEditingLocation(TextView txtView, EditText txtEdit)
    {
        // Check that the inserted text is not empty
        if (!txtEdit.getText().equals(""))
        {
            txtView.setVisibility(View.VISIBLE);
            txtView.setText(txtEdit.getText());
            txtEdit.setVisibility(View.GONE);

            // TODO : Update the location name on the db
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
    	LocationItem rowView = (LocationItem) mInflater.inflate(android.R.layout.simple_list_item_activated_2, null);
        holder = new ViewHolder();
        holder.txtLocation = (TextView)rowView.findViewById(R.id.txtLocationName);
        //holder.editLocation = (EditText)rowView.findViewById(R.id.edit_location);
        holder.icon = (ImageView)rowView.findViewById(R.id.locationIcon);
        holder.wifiName = (TextView)rowView.findViewById(R.id.txtWifiName);

        rowView.setTag(holder);
        return rowView;
    }
}
