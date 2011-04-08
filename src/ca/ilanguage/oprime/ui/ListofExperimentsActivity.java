package ca.ilanguage.oprime.ui;


import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import ca.ilanguage.oprime.R;

public class ListofExperimentsActivity extends ListActivity {


    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // Use our own list adapter
        setListAdapter(new SpeechListAdapter(this));
    }
        
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {    
       ((SpeechListAdapter)getListAdapter()).toggle(position);
    }
    
    /**
     * A sample ListAdapter that presents content
     * from arrays of speeches and text.
     *
     */
    private class SpeechListAdapter extends BaseAdapter {
        public SpeechListAdapter(Context context)
        {
            mContext = context;
        }

        
        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         * 
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return mTitles.length;
        }

        /**
         * Since the data comes from an array, just returning
         * the index is sufficent to get at the data. If we
         * were using a more complex data structure, we
         * would return whatever object represents one 
         * row in the list.
         * 
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a SpeechView to hold each row.
         * @see android.widget.ListAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            SpeechView sv;
            if (convertView == null) {
                sv = new SpeechView(mContext, mTitles[position], mDescription[position], mExpanded[position]);
            } else {
                sv = (SpeechView)convertView;
                sv.setTitle(mTitles[position]);
                sv.setDialogue(mDescription[position]);
//                sv.setImge(m)
                sv.setExpanded(mExpanded[position]);
            }
            
            return sv;
        }

        public void toggle(int position) {
            mExpanded[position] = !mExpanded[position];
            notifyDataSetChanged();
        }
        
        /**
         * Remember our context so we can use it when constructing views.
         */
        private Context mContext;
        
        /**
         * Our data, part 1.
         */
        private String[] mTitles = 
        {
        		"Bilingual Aphasia Test",
        		"Morphological Awareness Elicitation"
        };
        
        /**
         * Our data, part 2.
         */
        private String[] mDescription = 
        {
        		"Achim et al",
        		"Marquis et al"
        };
        
        /**
         * Our data, part 3.
         */
        private boolean[] mExpanded = 
        {
                false,
                false   
        };
    }
    
    /**
     * We will use a SpeechView to display each speech. It's just a LinearLayout
     * with two text fields.
     *
     */
    private class SpeechView extends LinearLayout {
        private TextView mTitle;
        private TextView mDescription;
        private ImageView mImage;
        public SpeechView(Context context, String title, String dialogue, boolean expanded) {
            super(context);
            
            this.setOrientation(VERTICAL);
            
            // Here we build the child views in code. They could also have
            // been specified in an XML file.
            mImage = (ImageView) findViewById(R.id.mainimage);
            mImage.setImageResource(R.drawable.androids_experimenter_kids);
//            mImage.setAdjustViewBounds(true);
//            mImage.setMaxHeight(100);
//            mImage.setMaxWidth(100);
            addView(mImage, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            //TODO: check if add image worked
            mTitle = new TextView(context);
            mTitle.setText(title);
            addView(mTitle, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            
            mDescription = new TextView(context);
            mDescription.setText(dialogue);
            addView(mDescription, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            
            mDescription.setVisibility(expanded ? VISIBLE : GONE);
        }
        
        /**
         * Convenience method to set the title of a SpeechView
         */
        public void setTitle(String title) {
            mTitle.setText(title);
        }
        
        /**
         * Convenience method to set the dialogue of a SpeechView
         */
        public void setDialogue(String words) {
            mDescription.setText(words);
        }
        
        /**
         * Convenience method to expand or hide the dialogue
         */
        public void setExpanded(boolean expanded) {
            mDescription.setVisibility(expanded ? VISIBLE : GONE);
        }
        

    }
}
