package ca.ilanguage.oprime.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import ca.ilanguage.oprime.R;
import ca.ilanguage.oprime.domain.Experiment;

public class ListofExperimentsActivity extends ListActivity {
	private ProgressDialog m_ProgressDialog = null; 
	private ArrayList<Experiment> m_orders = null;
	private OrderAdapter m_adapter;
	private Runnable viewOrders;


	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.experiments_list);
	        m_orders = new ArrayList<Experiment>();
	        this.m_adapter = new OrderAdapter(this, R.layout.experiments_list, m_orders);
	                setListAdapter(this.m_adapter);
	        
	        viewOrders = new Runnable(){
	            @Override
	            public void run() {
	                getOrders();
	            }
	        };
	    Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
	        thread.start();
	        m_ProgressDialog = ProgressDialog.show(ListofExperimentsActivity.this,    
	              "Please wait...", "Retrieving data ...", true);
	    }
	private void getOrders(){
        try{
            m_orders = new ArrayList<Experiment>();
            Experiment o1 = new Experiment();
            o1.setOrderName("SF services");
            o1.setOrderStatus("Pending");
            Experiment o2 = new Experiment();
            o2.setOrderName("SF Advertisement");
            o2.setOrderStatus("Completed");
            m_orders.add(o1);
            m_orders.add(o2);
               Thread.sleep(2000);
            Log.i("ARRAY", ""+ m_orders.size());
          } catch (Exception e) { 
            Log.e("BACKGROUND_PROC", e.getMessage());
          }
          runOnUiThread(returnRes);
      }
	
	private class OrderAdapter extends ArrayAdapter<Experiment> {

        private ArrayList<Experiment> items;

        public OrderAdapter(Context context, int textViewResourceId, ArrayList<Experiment> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.experiment_list_item, null);
                }
                Experiment o = items.get(position);
                if (o != null) {
                        TextView tt = (TextView) v.findViewById(R.id.toptext);
                        TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                        if (tt != null) {
                              tt.setText("Name: "+o.getOrderName());                            }
                        if(bt != null){
                              bt.setText("Status: "+ o.getOrderStatus());
                        }
                }
                return v;
        }
	}
	private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            if(m_orders != null && m_orders.size() > 0){
                m_adapter.notifyDataSetChanged();
                for(int i=0;i<m_orders.size();i++)
                m_adapter.add(m_orders.get(i));
            }
            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
        }
      };
}