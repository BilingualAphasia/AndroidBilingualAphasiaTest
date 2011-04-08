package ca.ilanguage.oprime.ui;

import java.io.File;
import java.util.ArrayList;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.ilanguage.oprime.R;
import ca.ilanguage.oprime.domain.*;

public class ListofExperimentsActivity extends ListActivity{

	private ProgressDialog m_ProgressDialog = null; 
	private ArrayList<Experiment> m_experiments = null;
	private ExperimentAdapter m_adapter;
	private Runnable viewExperiments;
	private String baseDir ="/sdcard/Oprime/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.experiments_list);
		m_experiments = new ArrayList<Experiment>();
		this.m_adapter = new ExperimentAdapter(this, R.layout.experiment_list_item, m_experiments);
		setListAdapter(this.m_adapter);

		viewExperiments = new Runnable(){
			@Override
			public void run() {
				getExperiments();
			}
		};
		Thread thread =  new Thread(null, viewExperiments, "MagentoBackground");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(ListofExperimentsActivity.this,    
				"Please wait...", "Retrieving data ...", true);
	}
	public void onPlayClick(View v){

		//startActivity(new Intent(this, OPrimeHomeActivity.class));
		startActivity(new Intent(this, OprimeLogoActivity.class));
	}
	public void onSettingsClick(View v){

		//startActivity(new Intent(this, OPrimeHomeActivity.class));
		startActivity(new Intent(this, OprimeLogoActivity.class));
	}
	public void onEditClick(View v){

		//startActivity(new Intent(this, OPrimeHomeActivity.class));
		startActivity(new Intent(this, OprimeLogoActivity.class));
	}
	public void onVideoClick(View v){

		//startActivity(new Intent(this, OPrimeHomeActivity.class));
		startActivity(new Intent(this, OprimeLogoActivity.class));
	}   
	public void onListenClick(View v){

		//startActivity(new Intent(this, OPrimeHomeActivity.class));
		startActivity(new Intent(this, OprimeLogoActivity.class));
	}
	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if(m_experiments != null && m_experiments.size() > 0){
				m_adapter.notifyDataSetChanged();
				for(int i=0;i<m_experiments.size();i++)
					m_adapter.add(m_experiments.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};
	private void getExperiments(){
		try{
			m_experiments = new ArrayList<Experiment>();
			Experiment o1 = new Experiment();
			o1.setExperimentName("Experiment 1");
			o1.setExperimentStatus("Smith et al");
			Experiment o2 = new Experiment();
			o2.setExperimentName("Experiment 2");
			o2.setExperimentStatus("Jones et al");
			m_experiments.add(o1);
			m_experiments.add(o2);
			
			//get a list of files in a directory
			File dir = new File(baseDir);

			
			String[] children = dir.list();
			if (children == null) {
			    // Either dir does not exist or is not a directory
			} else {
			    for (int i=0; i<children.length; i++) {
			        // Get filename of file or directory
			        String filename = children[i];
			        o1.setExperimentName(filename);
			        o1.setExperimentFolder(baseDir+filename+"/");
			        o1.setExperimentStatus("stimuli_demo.csv");
			        m_experiments.add(o1);
			    }
			}
			
			
			Thread.sleep(1000);
			Log.i("ARRAY", ""+ m_experiments.size());
		} catch (Exception e) { 
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}
	private class ExperimentAdapter extends ArrayAdapter<Experiment> {
		//based on http://www.softwarepassion.com/android-series-custom-listview-items-and-adapters/
		private ArrayList<Experiment> items;

		public ExperimentAdapter(Context context, int textViewResourceId, ArrayList<Experiment> items) {
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
				ImageView image = (ImageView) v.findViewById(R.id.experimentIcon);
				if (tt != null) {
					tt.setText("Title: "+o.getExperimentName());                            }
				if(bt != null){
					bt.setText("Authors: "+ o.getExperimentStatus());
				}
				if(image != null){
					image.setImageResource(R.drawable.ic_oprime);

				}
			}
			return v;
		}
	}
}