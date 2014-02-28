package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.controllers.Clarity_CreatePatient.TabListener;
import com.clarityforandroid.models.Clarity_ProviderModel;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * The find patient activity.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_FindPatient extends Activity {

	private static Clarity_ProviderModel provider;
	
	private static Activity mActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get bundles
		provider = new Clarity_ProviderModel();
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			provider = incomingIntent.getExtras().getParcelable("provider_model");
		}
		
		// Set up views
		this.setContentView(R.layout.activity_find_patient);
		mActivity = this;
		
		// Customize action bar
		ActionBar bar = this.getActionBar();
		bar.setTitle("Find");
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				
		// Add tabs to action bar
		Tab tab = bar.newTab()
				.setText(R.string.activity_find_search_by_ticket)
		        .setTabListener(new TabListener<SearchByTicketFragment>(
		        this, "qr", SearchByTicketFragment.class));
		bar.addTab(tab);
				
		tab = bar.newTab()
				.setText(R.string.activity_find_search_by_name)
		        .setTabListener(new TabListener<SearchByNameFragment>(
		        this, "name", SearchByNameFragment.class));
		bar.addTab(tab);
	}
	

	public static class SearchByTicketFragment extends Fragment {
		
		// Acts as the "context" for this tab
		private ViewGroup viewContainer;
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	    	
	        // Inflate the layout for this fragment
	        viewContainer = (ViewGroup) inflater.inflate(R.layout.fragment_search_by_id, container, false);
	        
	        return viewContainer;
	    }
	}
	
	public static class SearchByNameFragment extends Fragment {
		
		// Acts as the "context" for this tab
		private ViewGroup viewContainer;
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	    	
	        // Inflate the layout for this fragment
	        viewContainer = (ViewGroup) inflater.inflate(R.layout.fragment_search_by_name, container, false);
	        
	        return viewContainer;
	    }
	}
	
}
