package com.clarityforandroid.controllers;

import java.util.ArrayList;

import org.javatuples.Triplet;
import org.json.JSONException;
import org.json.JSONObject;

import com.clarityforandroid.R;
import com.clarityforandroid.helpers.Clarity_ApiCall;
import com.clarityforandroid.helpers.Clarity_DialogFactory;
import com.clarityforandroid.helpers.Clarity_ServerTask;
import com.clarityforandroid.helpers.Clarity_ServerTask.Clarity_ServerTaskError;
import com.clarityforandroid.helpers.Clarity_ServerTaskDelegate;
import com.clarityforandroid.helpers.Clarity_URLs;
import com.clarityforandroid.helpers.ZXing_IntentIntegrator;
import com.clarityforandroid.helpers.ZXing_IntentResult;
import com.clarityforandroid.helpers.Clarity_ApiCall.Clarity_ApiMethod;
import com.clarityforandroid.models.Clarity_PatientModel;
import com.clarityforandroid.models.Clarity_ProviderModel;
import com.clarityforandroid.models.Clarity_TicketModel;
import com.clarityforandroid.models.Clarity_TicketModel.Clarity_ServiceStatus;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The create patient activity.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class Clarity_CreatePatient extends Activity implements Clarity_ServerTaskDelegate {
	
	private static Clarity_ProviderModel provider;
	
	private static Clarity_PatientModel patient;
	private static Clarity_TicketModel ticket;
	private static Activity mActivity;
	
	private static InputMethodManager imm;
	
	private static final int MIN_LOAN_SIZE = 0;
	private static final int MAX_LOAN_SIZE = 1000;
	
	private static final int CAMERA_REQUEST_CODE = 1;
	
	private final String CLIENT_CREATE = Clarity_URLs.CLIENT_CREATE_UNSTABLE.getUrl();
	private final String TICKET_CREATE = Clarity_URLs.TICKET_CREATE_UNSTABLE.getUrl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get bundles
		provider = new Clarity_ProviderModel();
		Intent incomingIntent = this.getIntent();
		if (incomingIntent != null) {
			provider = incomingIntent.getExtras().getParcelable("provider_model");
		}
		
		// Get the input method manager so that I can put the keyboard away
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		// Create a patient
		patient = new Clarity_PatientModel();
		ticket = new Clarity_TicketModel();
		
		// Set up views
		this.setContentView(R.layout.activity_create_patient);
		mActivity = this;	
		
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
                .setTabListener(new TabListener<CameraFragment>(
                        this, "photo", CameraFragment.class));
		bar.addTab(tab);
		
		tab = bar.newTab()
                .setText(R.string.activity_create_patient_qrcode)
                .setTabListener(new TabListener<QrCodeFragment>(
                        this, "services", QrCodeFragment.class));
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
				getString(R.string.activity_create_patient_quit_assurance), "Yes", "No");
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.main_menu_action_bar_options_upload:
	            this.attemptPush();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public static class DemographicsFragment extends Fragment {
		
		// Acts as the "context" for this tab
		private ViewGroup viewContainer;
		
		private TextView firstNameView;
		private TextView middleNameView;
		private TextView lastNameView;
		private TextView locationView;
		private RadioGroup sexGroup;
		private DatePicker dob;
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	    	
	        // Inflate the layout for this fragment
	        viewContainer = (ViewGroup) inflater.inflate(R.layout.fragment_demographics, container, false);
	        
	        // Put the keyboard away
	        imm.hideSoftInputFromWindow(viewContainer.getWindowToken(), 0);
	        
	        // Fill in the user data from view
	        firstNameView = (TextView) viewContainer.findViewById(R.id.fragment_demographics_firstNameField);
	        middleNameView = (TextView) viewContainer.findViewById(R.id.fragment_demographics_middleNameField);
	        lastNameView = (TextView) viewContainer.findViewById(R.id.fragment_demographics_lastNameField);
	        locationView = (TextView) viewContainer.findViewById(R.id.fragment_demographics_locationField);
	        sexGroup = (RadioGroup) viewContainer.findViewById(R.id.fragment_demographics_genderGroup);
	        dob = (DatePicker) viewContainer.findViewById(R.id.fragment_demographics_dobPicker);
	        
	        // Use TextWatchers to listen for changes on text views
	        
	        firstNameView.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) { /*...*/ }
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) { /*...*/ }
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s == "") {
						patient.setNameFirst(null);
					} else {
						patient.setNameFirst(s.toString());
					}
				}
	        });
	        
	        middleNameView.addTextChangedListener(new TextWatcher() {
	        	@Override
				public void afterTextChanged(Editable s) { /*...*/ }
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) { /*...*/ }
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s == "") {
						patient.setNameMiddle(null);
					} else {
						patient.setNameMiddle(s.toString());
					}
				}
	        });
	        
	        lastNameView.addTextChangedListener(new TextWatcher() {
	        	@Override
				public void afterTextChanged(Editable s) { /*...*/ }
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) { /*...*/ }
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s == "") {
						patient.setNameLast(null);
					} else {
						patient.setNameLast(s.toString());
					}
				}
	        });
	        
	        locationView.addTextChangedListener(new TextWatcher() {
	        	@Override
				public void afterTextChanged(Editable s) { /*...*/ }
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) { /*...*/ }
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s == "") {
						patient.setLocation(null);
					} else {
						patient.setLocation(s.toString());
					}
				}
	        });
	        
	        // Listen to the radio group
	        sexGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					RadioButton checkedRadioButton = (RadioButton) sexGroup.findViewById(checkedId);
					patient.setSex(checkedRadioButton.getText().toString());
				}
	        });
	        
	        // Listen to the date picker
	        dob.init(1970, 0, 0, new OnDateChangedListener() {
				@Override
				public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					patient.setDateOfBirth(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
				}
	        });
	        
	        // Set this here to ensure that every patient gets a DOB, even if the picker wasn't touched
	        patient.setDateOfBirth(dob.getYear() + "-" + (dob.getMonth() + 1) + "-" + dob.getDayOfMonth());
	        
	        return viewContainer;
	    }
	}
	
	public static class CameraFragment extends Fragment {
		
		// Acts as the "context" for this tab
		private ViewGroup viewContainer;
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	    	
	        // Inflate the layout for this fragment
	        viewContainer = (ViewGroup) inflater.inflate(R.layout.fragment_camera, container, false);
	        
	        // Put the keyboard away
	        imm.hideSoftInputFromWindow(viewContainer.getWindowToken(), 0);
	        
	        // Set listeners
	        Button cameraButton = (Button) viewContainer.findViewById(R.id.fragment_camera_button);
	        cameraButton.setOnClickListener(new CameraButtonClickListener());
	        
	        // Check for user image
	        ImageView patientPic = (ImageView) viewContainer.findViewById(R.id.fragment_camera_image);

	        if (patient.picture() == null) {
				patientPic.setImageBitmap(BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.camera_white_128));
	        }
	        else {
	        	patientPic.setImageBitmap(patient.picture());
	        }
	        
	        return viewContainer;
	    }
	    
	    /**
		 * The listener that listens for clicks on the camera button.
		 * 
		 * @author Jonathan Ballands
		 * @version 1.0
		 */
		private class CameraButtonClickListener implements OnClickListener {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, Clarity_CreatePatient.CAMERA_REQUEST_CODE);
			}
		}
		
		/**
		 * The callback that gets called when the camera comes back.
		 * 
		 * @param requestCode The request code so that you may determine what dispatched the request.
		 * @param resultCode The result code so that you may determine if there was a problem.
		 * @param data The data that came back from the intent.
		 */
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			
			// Where did the result come from?
			if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
				
				// TODO: Bug in taking pictures of the patient...
				
				// MATH TIME D:
				Bitmap uncropped = (Bitmap) data.getExtras().get("data");
				Bitmap raw;
				if (uncropped.getHeight() >= uncropped.getWidth()) {
					final int startY = (uncropped.getHeight() - uncropped.getWidth()) / 2;
					raw = Bitmap.createBitmap(uncropped, 0, startY, uncropped.getWidth(), uncropped.getWidth());;
				}
				else {
					final int startX = (uncropped.getWidth() - uncropped.getHeight()) / 2;
					raw = Bitmap.createBitmap(uncropped, startX, 0, uncropped.getHeight(), uncropped.getHeight());
				}
				
				patient.setPicture(raw);
				
				// Set the picture
				ImageView patientPic = (ImageView) viewContainer.findViewById(R.id.fragment_camera_image);
				patientPic.setImageBitmap(patient.picture());
				
				// Show a toast to confirm
				Toast.makeText(mActivity, "Picture updated", Toast.LENGTH_SHORT).show();
			}
			
			else {
				Log.e("CameraFragment", "Unintelligable result");
			}
		}
	}
	
	public static class QrCodeFragment extends Fragment {
		
		private ViewGroup viewContainer;
		
		private TextView qrCodeId;
		
		private CheckBox leftLegCheckbox;
		private CheckBox leftShinCheckbox;
		private CheckBox rightLegCheckbox;
		private CheckBox rightShinCheckbox;
		private CheckBox leftArmCheckbox;
		private CheckBox rightArmCheckbox;
		private CheckBox wheelchairCheckbox;
		private CheckBox tricycleCheckbox;
		private CheckBox crutchesCheckbox;
		private CheckBox sewingCheckbox;
		private CheckBox teaCheckbox;
		
		private NumberPicker loanPicker;
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	    	
	        // Inflate the layout for this fragment
	        viewContainer = (ViewGroup) inflater.inflate(R.layout.fragment_qrcode, container, false);
	        
	        // Put the keyboard away
	        imm.hideSoftInputFromWindow(viewContainer.getWindowToken(), 0);
	        
	        // Set listeners
	        Button scanButton = (Button) viewContainer.findViewById(R.id.fragment_scan_button);
	        scanButton.setOnClickListener(new ScanButtonClickListener());
	        
	        // Find all views
	        qrCodeId = (TextView) viewContainer.findViewById(R.id.fragment_qrcode_data);
	        
	        leftLegCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_left_leg_checkbox);
	        leftShinCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_left_shin_checkbox);
	        rightLegCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_right_leg_checkbox);
	        rightShinCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_right_shin_checkbox);
	        leftArmCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_left_arm_checkbox);
	        rightArmCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_right_arm_checkbox);
	        wheelchairCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_wheelchair_checkbox);
	        tricycleCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_tricycle_checkbox);
	        crutchesCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_crutches_checkbox);
	        sewingCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_sewing_checkbox);
	        teaCheckbox = (CheckBox) viewContainer.findViewById(R.id.fragment_qrcode_tea_checkbox);
	        
	        loanPicker = (NumberPicker) viewContainer.findViewById(R.id.fragment_qrcode_loan_picker);
	        
	        // Show values for loan picker
	        loanPicker.setMinValue(MIN_LOAN_SIZE);
	        loanPicker.setMaxValue(MAX_LOAN_SIZE);
	        
	        // Set the picker so that it initializes right
	        loanPicker.setValue(ticket.loan());
	        
	        // Set the qr id so that it initializes right
	        if (patient.ticket() != null) {
		        qrCodeId.setText(patient.ticket());
	        }
	        
	        leftLegCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setLeftLeg(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setLeftLeg(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        leftShinCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setLeftShin(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setLeftShin(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        rightLegCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setRightLeg(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setRightLeg(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        rightShinCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setRightShin(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setRightShin(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        leftArmCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setLeftArm(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setLeftArm(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        rightArmCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setRightArm(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setRightArm(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        wheelchairCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setWheelchair(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setWheelchair(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        tricycleCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setTricycle(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setTricycle(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        crutchesCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setCrutches(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setCrutches(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        sewingCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setSewingMachine(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setSewingMachine(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        teaCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CheckBox) v).isChecked()) {
						ticket.setTeaStand(Clarity_ServiceStatus.STATUS_SELECTED);
					}
					else {
						ticket.setTeaStand(Clarity_ServiceStatus.STATUS_UNSELECTED);
					}
				}
	        });
	        
	        loanPicker.setOnValueChangedListener(new OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					ticket.setLoan(newVal);
				}
	        });
	        
	        return viewContainer;
	    }
	    
	    /**
		 * The listener that listens for clicks on the scan button.
		 * 
		 * @author Jonathan Ballands
		 * @version 1.0
		 */
		private class ScanButtonClickListener implements OnClickListener {
			
			@Override
			public void onClick(View v) {
				// If on emulator, emulate a QR code scan
				// DEBUG
			    if (android.os.Build.MODEL.contains("sdk")) {
					Toast.makeText(mActivity, "Clarity QR code injected", Toast.LENGTH_SHORT).show();
					patient.setTicket("clarity" + java.util.UUID.randomUUID().toString());
					
					// Update the view
					qrCodeId.setText(patient.ticket());
					qrCodeId.setTextSize(12f);
					
					return;
				}

				// No debug
				ZXing_IntentIntegrator integrator = new ZXing_IntentIntegrator(mActivity);
				integrator.initiateScan();
			}
		}
		
	}
	
	/**
	 * This method tries to push data to the cloud by performing some basic validity checking
	 * before actually performing a cloud push.
	 */
	private void attemptPush() {
		
		// First, do some data validation
		if ((patient.nameFirst() == null) || (patient.nameLast() == null) || (patient.sex() == null)) {
			// Display error
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_CreatePatient.this, "Bad Fields", 
            		Clarity_CreatePatient.this.getString(R.string.activity_create_patient_bad_fields));
			return;
		}
		else if (patient.ticket() == null) {
			// Display error
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_CreatePatient.this, "No Ticket Scanned", 
					Clarity_CreatePatient.this.getString(R.string.activity_create_patient_no_ticket));
				return;
		}
		
		// Everything looks good! Ask to confirm
		final ProgressDialog dialog = Clarity_DialogFactory.displayNewChoiceDialog(Clarity_CreatePatient.this, "Send to Cloud", 
				getString(R.string.activity_create_patient_push_reassurance), "Yes", "No");
		dialog.findViewById(R.id.affirmative_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				// Connect to the server
				Clarity_ApiCall call = new Clarity_ApiCall(CLIENT_CREATE);
				call.addParameter("name_prefix", patient.namePrefix());
				call.addParameter("name_first", patient.nameFirst());
				call.addParameter("name_middle", patient.nameMiddle());
				call.addParameter("name_last", patient.nameLast());
				call.addParameter("name_suffix", patient.nameSuffix());
				call.addParameter("sex", patient.sex().toLowerCase());
				call.addParameter("location", patient.location());
				call.addParameter("dateofbirth", patient.dateOfBirth());
				call.addParameter("token", provider.token());
				call.addParameter("binary", Clarity_ApiCall.encodeBitmapToBase64(patient.picture()));

				
				// Set up errors
				ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
				errs.add(new Triplet<Integer, String, String>(401, "Malformed Data", getString(R.string.malformed_data)));
				errs.add(new Triplet<Integer, String, String>(403, "Invalid session", getString(R.string.invalid_session)));
				errs.add(new Triplet<Integer, String, String>(500, "Internal Server Error", getString(R.string.generic_error_internal_server_error)));
				
				// Start logout process
				Clarity_ServerTask task = new Clarity_ServerTask(call, Clarity_ApiMethod.POST, getString(R.string.activity_create_patient_cp),
						errs, Clarity_CreatePatient.this, Clarity_CreatePatient.this);
				task.go();
			}
		});
		dialog.findViewById(R.id.negative_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	

	@Override
	public void processResults(Clarity_ApiCall c) {

		// Confirm with a toast and then finish
		if (c.getResponseCode() == 200) {

			// After creating the ticket, end
			if (c.getUrl() == TICKET_CREATE) {
				Toast confirmationToast = Toast.makeText(this, "Success", Toast.LENGTH_SHORT);
				confirmationToast.show();

				Intent intent = new Intent(this, Clarity_HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("provider_model", provider);
				startActivity(intent);
			}

			// Otherwise, actually create the ticket
			else {

				// Get the patient id to link the patient to
				JSONObject json;
				try {
					json = new JSONObject(c.getResponse());

					// Connect to the server
					Clarity_ApiCall call = new Clarity_ApiCall(TICKET_CREATE);
					call.addParameter("token", provider.token());
					call.addParameter("qrcode", patient.ticket());
					call.addParameter("client", json.getString("id"));
					call.addParameter("left_leg", String.valueOf(ticket.leftLeg().getCode()));
					call.addParameter("right_leg", String.valueOf(ticket.rightLeg().getCode()));
					call.addParameter("left_shin", String.valueOf(ticket.leftShin().getCode()));
					call.addParameter("right_shin", String.valueOf(ticket.rightShin().getCode()));
					call.addParameter("left_arm", String.valueOf(ticket.leftArm().getCode()));
					call.addParameter("right_arm", String.valueOf(ticket.rightArm().getCode()));
					call.addParameter("wheelchair", String.valueOf(ticket.wheelchair().getCode()));
					call.addParameter("crutches", String.valueOf(ticket.crutches().getCode()));
					call.addParameter("tricycle", String.valueOf(ticket.tricycle().getCode()));
					call.addParameter("sewing_machine", String.valueOf(ticket.sewingMachine().getCode()));
					call.addParameter("tea_stand", String.valueOf(ticket.teaStand().getCode()));
					call.addParameter("loan_amount", String.valueOf(ticket.loan()));
					call.addParameter("loan_status", String.valueOf(ticket.loanStatus().getCode()));

					// Set up errors
					ArrayList<Triplet<Integer, String, String>> errs = new ArrayList<Triplet<Integer, String, String>>();
					errs.add(new Triplet<Integer, String, String>(401, "Malformed Data", getString(R.string.malformed_data)));
					errs.add(new Triplet<Integer, String, String>(500, "Internal Server Error", getString(R.string.generic_error_internal_server_error)));

					// New task
					Clarity_ServerTask task = new Clarity_ServerTask(call,
							Clarity_ApiMethod.POST,
							getString(R.string.activity_create_patient_tl), errs,
							Clarity_CreatePatient.this, Clarity_CreatePatient.this);
							task.go();
				} 
				catch (JSONException e) {
					// JSON parse error
					Clarity_DialogFactory.displayNewErrorDialog(Clarity_CreatePatient.this,
							this.getString(R.string.error_title),
							this.getString(R.string.generic_error_generic));
					Log.d("Clarity_Login", "JSON parse exeception");
					return;
				}
			}
		}
	}

	@Override
	public void processError(Clarity_ServerTaskError result) {
		switch (result) {

		case NO_CONNECTION:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_CreatePatient.this, "No Internet Connection",
					Clarity_CreatePatient.this.getString(R.string.generic_error_no_internet));
			break;
	
		case REQUEST_TIMEOUT:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_CreatePatient.this, "Connection Timeout",
					Clarity_CreatePatient.this.getString(R.string.generic_error_timeout));
			break;
	
		case GENERIC_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_CreatePatient.this, "Unexpected Error",
					Clarity_CreatePatient.this.getString(R.string.generic_error_generic));
			break;
	
		case FATAL_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_CreatePatient.this, "Exceptional Error",
					Clarity_CreatePatient.this.getString(R.string.generic_error_generic));
			break;
	
		case INVALID_TOKEN_ERROR:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_CreatePatient.this, "Invalid Token",
					Clarity_CreatePatient.this.getString(R.string.invalid_token));
	
			// Boot back out to the login screen
			Intent intent = new Intent(Clarity_CreatePatient.this, Clarity_Login.class);
			startActivity(intent);
			finish();
	
			break;
	
		default:
			Clarity_DialogFactory.displayNewErrorDialog(Clarity_CreatePatient.this, "Unexpected Error",
					Clarity_CreatePatient.this.getString(R.string.generic_error_generic));
			break;
		}
	}
	
	/**
	 * The callback that gets called when the scanner comes back.
	 * 
	 * @param requestCode The request code so that you may determine what dispatched the request.
	 * @param resultCode The result code so that you may determine if there was a problem.
	 * @param data The data that came back from the intent.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		ZXing_IntentResult scanResult = ZXing_IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && scanResult != null) {

			// Get something out of the qr code
			String encoding = scanResult.getContents();

			// Valid clarity QR code?
			if (encoding.startsWith("clarity")) {

				// Valid
				
				// Find the qr code data text view and fill it with data
				TextView qrCodeId = (TextView) findViewById(R.id.fragment_qrcode_data);
				
				patient.setTicket(encoding);
				qrCodeId.setText(encoding);
				qrCodeId.setTextSize(12f);
				Toast.makeText(mActivity, "Ticket updated", Toast.LENGTH_SHORT).show();
			}
			else {
				// Invalid
				Clarity_DialogFactory.displayNewErrorDialog(mActivity, getString(R.string.bad_qr_title), getString(R.string.bad_qr_message));
			}
        }
	}
	
}
