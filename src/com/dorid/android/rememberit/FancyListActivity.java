/*
 * Copyright (C) 2011 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dorid.android.rememberit;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;

/**
 * Shows how to create fancy {@link ListView} in your application. This
 * {@link ListActivity} is pretty basic as most the styling code can actually be
 * found in the XML (drawable, layout, etc.).
 * 
 * @author Cyril Mottier
 */
public class FancyListActivity extends SherlockListActivity {

    private static final int METHOD_DRAW_SELECTOR_ON_TOP = 1;
    private static final int METHOD_USE_SELECTOR_AS_BACKGROUND = 2;
    public static final String CHEESES[] = {
        "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis",
        "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese",
        "Ami du Chambertin", "Anejo Enchilado", "Anneau du Vic-Bilh", "Anthoriro", "Appenzell", "Aragon", "Ardi Gasna", "Ardrahan",
        "Armenian String", "Aromes au Gene de Marc", "Asadero", "Asiago", "Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss",
        "Babybel", "Baguette Laonnaise", "Bakers", "Baladi", "Balaton", "Bandal", "Banon", "Barry's Bay Cheddar", "Basing",
        "Basket Cheese", "Bath Cheese", "Bavarian Bergkase", "Baylough", "Beaufort", "Beauvoorde", "Beenleigh Blue", "Beer Cheese",
        "Bel Paese", "Bergader", "Bergere Bleue", "Berkswell", "Beyaz Peynir", "Bierkase", "Bishop Kennedy", "Blarney",
        "Bleu d'Auvergne", "Bleu de Gex", "Bleu de Laqueuille", "Bleu de Septmoncel", "Bleu Des Causses", "Blue", "Blue Castello",
        "Blue Rathgore", "Blue Vein (Australian)", "Blue Vein Cheeses", "Bocconcini", "Bocconcini (Australian)", "Boeren Leidenkaas"};
    
    private static final String[] SPECIAL_CHEESE_TAGS = new String[] {
            "'", "-", "y"
    };

    private FancyAdapter mFancyAdapter;
    private int mMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        setTheme(R.style.Theme_Sherlock_Light); //Used for theme switching in samples

    	super.onCreate(savedInstanceState);
        setContentView(R.layout.fancy_list);
        
        mFancyAdapter = new FancyAdapter(CHEESES);
        setListAdapter(mFancyAdapter);

        //changeMethod(METHOD_DRAW_SELECTOR_ON_TOP);
    }

    public void onDrawSelectorOnTop(View v) {
        changeMethod(METHOD_DRAW_SELECTOR_ON_TOP);
    }

    public void onUseSelectorAsBackground(View v) {
        changeMethod(METHOD_USE_SELECTOR_AS_BACKGROUND);
    }

    /**
     * Method that changes the current method used to draw the list selector.
     * 
     * @param method The list selector drawing method to use.
     * @see #METHOD_DRAW_SELECTOR_ON_TOP
     * @see #METHOD_USE_SELECTOR_AS_BACKGROUND
     */
    private void changeMethod(int method) {
        if (mMethod != method) {
            switch (method) {
                case METHOD_DRAW_SELECTOR_ON_TOP:
                    mMethod = METHOD_DRAW_SELECTOR_ON_TOP;
                    getListView().setSelector(R.drawable.list_selector_on_top);
                    getListView().setDrawSelectorOnTop(true);
                    break;

                case METHOD_USE_SELECTOR_AS_BACKGROUND:
                    mMethod = METHOD_USE_SELECTOR_AS_BACKGROUND;
                    getListView().setSelector(R.drawable.list_selector);
                    getListView().setDrawSelectorOnTop(false);
                    break;

                default:
                    // Do nothing : this value is not handled
                    break;
            }

            // HACK Cyril: Most of the time, the following line is not
            // necessary. The purpose of this line is to force the ListView to
            // re-layout and re-draw all of its children. Indeed, when changing
            // selector properties of the ListView 'on the fly', problems may
            // occur (bad itemview states, invisible list selector, etc.).
            // Usually, you'll change the list selector properties at
            // creation time (or event better in the XML definition of your
            // layout) and won't touch this afterwards.
            getListView().invalidateViews();
        }
    }

    /**
     * A pretty stupid Adapter managing a list of cheeses... Some of those cheeses
     * are very special when processed by a very advanced algorithm ... :-).
     * 
     * @author Cyril Mottier
     */
    private class FancyAdapter extends BaseAdapter {

    	private class ViewHolder {
            protected Button btnSetCurrent;
            protected ImageView icon;
            protected TextView txtLocation;
            protected TextView wifiName;
        }

        /*
         * we are overriding the getView method here - this is what defines how each
         * list item will look.
         */
        public View getView(int position, View convertView, ViewGroup parent){

            ViewHolder holder;

            // first check to see if the view is null. if so, we have to inflate it.
            // to inflate it basically means to render, or show, the view.
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.location_item, null);

                holder = new ViewHolder();
                holder.txtLocation = (TextView)convertView.findViewById(R.id.txtLocationName);
                holder.icon = (ImageView)convertView.findViewById(R.id.locationIcon);
                holder.wifiName = (TextView)convertView.findViewById(R.id.txtWifiName);
                //holder.btnSetCurrent = (Button)convertView.findViewById(R.id.btnSetCurrent);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            final String cheese = getItem(position);
                        
            holder.txtLocation.setText(cheese);
            holder.wifiName.setText("(" + cheese + ")");
            holder.icon.setImageResource(R.drawable.ic_action_home);

            // the view must be returned to our activity
            return convertView;
        }

        private String[] mData;

        public FancyAdapter(String[] data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public String getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /*
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView result;

            if (convertView == null) {
                result = (TextView) getLayoutInflater().inflate(R.layout.text_item, parent, false);
                // Set the text color to black here in order to reuse the
                // test_item layout. The preferred way to do it is obviously to
                // set it in the XML possibly via a text appearance.
                // Here we are using a plain Color but keep in mind you can use
                // a ColorStateList if you want your text color to change
                // depending on the current state of the itemview.
                result.setTextColor(Color.BLACK);
            } else {
                result = (TextView) convertView;
            }

            final String cheese = getItem(position);

            result.setText(cheese);

            int normalId;
            int specialId;

            switch (mMethod) {
                case METHOD_USE_SELECTOR_AS_BACKGROUND:
                    // The two following resource identifiers refer to
                    // StateListDrawables.
                    normalId = R.drawable.list_item_selector_normal;
                    specialId = R.drawable.list_item_selector_special;
                    break;
                case METHOD_DRAW_SELECTOR_ON_TOP:
                default:
                    normalId = R.drawable.list_item_background_normal;
                    specialId = R.drawable.list_item_background_special;
                    break;
            }

            // Change the background of this itemview depending on whether the
            // underlying cheese is special or not.
            result.setBackgroundResource(isSpecial(cheese) ? specialId : normalId);

            return result;
        }
        */
        /**
         * Stupid method considering if a cheese is special or not. The
         * algorithm is not the important thing in this tip ^^.
         * 
         * @param cheese The cheese to analyze
         * @return true if that cheese is important else it returns false.
         */
        private boolean isSpecial(String cheese) {
            if (cheese != null) {
                for (String tag : SPECIAL_CHEESE_TAGS) {
                    if (cheese.contains(tag)) {
                        return true;
                    }
                }
            }

            return false;
        }

    }

}
