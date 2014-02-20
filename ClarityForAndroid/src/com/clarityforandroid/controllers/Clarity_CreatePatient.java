package com.clarityforandroid.controllers;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.models.Clarity_ProviderModel;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * The create patient activity.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_CreatePatient extends Activity {
	
	private Clarity_ProviderModel provider;

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
		this.setContentView(R.layout.activity_create_patient);
				
		// Customize action bar
		ActionBar bar = this.getActionBar();
		bar.setTitle("Create");
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Add tabs to action bar
		Tab tab = bar.newTab()
                .setText(R.string.activity_create_patient_demographics)
                .setTabListener(new TabListener<DemographicsFragment>(
                        this, "demo", DemographicsFragment.class));
		bar.addTab(tab);
		
		tab = bar.newTab()
                .setText(R.string.activity_create_patient_picture)
                .setTabListener(new TabListener<DemographicsFragment>(
                        this, "photo", DemographicsFragment.class));
		bar.addTab(tab);
		
		tab = bar.newTab()
                .setText(R.string.activity_create_patient_qrcode)
                .setTabListener(new TabListener<DemographicsFragment>(
                        this, "qr", DemographicsFragment.class));
		bar.addTab(tab);
	}
	
	/**
	 * Note: Used to inflate the Action Bar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_create_patient_ab_options, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onBackPressed() {
		final ProgressDialog dialog = Clarity_DialogFactory.displayNewChoiceDialog(Clarity_CreatePatient.this, "Leave", 
				getString(R.string.activity_capdemographics_quit_assurance), "Yes", "No");
		dialog.findViewById(R.id.affirmative_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
			}
		});
		dialog.findViewById(R.id.negative_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	
	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
	    private Fragment mFragment;
	    private final Activity mActivity;
	    private final String mTag;
	    private final Class<T> mClass;

	    /** Constructor used each time a new tab is created.
	      * @param activity  The host Activity, used to instantiate the fragment
	      * @param tag  The identifier tag for the fragment
	      * @param clz  The fragment's Class, used to instantiate the fragment
	      */
	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
	    }

	    /* The following are each of the ActionBar.TabListener callbacks */

	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        // Check if the fragment is already initialized
	        if (mFragment == null) {
	            // If not, instantiate and add it to the activity
	            mFragment = Fragment.instantiate(mActivity, mClass.getName());
	            ft.add(android.R.id.content, mFragment, mTag);
	        } else {
	            // If it exists, simply attach it in order to show it
	            ft.attach(mFragment);
	        }
	    }

	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	        if (mFragment != null) {
	            // Detach the fragment, because another one is being attached
	            ft.detach(mFragment);
	        }
	    }

	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        // User selected the already selected tab. Usually do nothing.
	    }

	}
	
	public static class DemographicsFragment extends Fragment {
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        // Inflate the layout for this fragment
	        return inflater.inflate(R.layout.fragment_demographics, container, false);
	    }
	}
	
}
