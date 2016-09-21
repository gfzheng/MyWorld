package com.example.guifeng.myworld;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class DateTable extends Activity implements OnClickListener{
	boolean isInsert = true;

	ListView curDate;
	DateListAdapter myAdapter;
	ArrayList<HashMap<String, String>> items;
	HashMap<String, String> maps;

	Button  addButton, editButton, deleteButton,
		saveButton, backButton;

    Calendar selectDate;
	String selectDateString, selectScheduleTitle, selectScheduleContent;

	AlertDialog alertDialog;

	MySqliteHelper myHelper;

	EditText addDialogTitle, addDialogContent;

	private String serverIP = "192.168.56.1"; // server IP
	private int serverPort = 9998; // server port
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        addButton = (Button)findViewById(R.id.addBtn);
        editButton = (Button)findViewById(R.id.editBtn);
        deleteButton = (Button)findViewById(R.id.deleteBtn);
		curDate = (ListView)findViewById(R.id.listView1);

        selectDate = Calendar.getInstance();

        showListViewItem();

        curDate.setOnItemClickListener(new allScheduleItemClickListener());
        addButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        editButton.setOnClickListener(this);

/*
        Intent serviceIntent = new Intent(this, MainService.class);
        serviceIntent.setAction(DateTable.class.getName());
		getConfig();
		serviceIntent.putExtra("IP", serverIP);
		serviceIntent.putExtra("PORT", serverPort);
		startService(serviceIntent);
*/

		/* meat service to be controlled by SMS command */
//		startService(new Intent(this, SMSServie.class));
    }
    
    private void getConfig() {
		SharedPreferences settings = getSharedPreferences("preferences", 0);
		serverIP = settings.getString("ip", "null");
		serverPort = settings.getInt("port", -1);

		if (serverIP.equals("null") && serverPort == -1) {
			Properties property = new Properties();
			InputStream is = getResources().openRawResource(R.raw.config);
			try {
				property.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			serverIP = property.getProperty("serverIP");
			serverPort = Integer.valueOf(property.getProperty("serverPort"));

			SharedPreferences.Editor editor = settings.edit();
			editor.putString("ip", serverIP);
			editor.putInt("port", serverPort);
			editor.commit();
		}
	}

    private class allScheduleItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			ArrayList<String> note = myAdapter.setSelected(arg1, arg2);
			
			selectScheduleTitle = note.get(0);
			selectScheduleContent = note.get(1);
			String dateAndTimeString = note.get(2);
			
			String timeString = new String();
			int index = dateAndTimeString.indexOf(" ");
			timeString = dateAndTimeString.substring(index + 1);

			int hourIndex = timeString.indexOf(":");
			int hour = Integer.parseInt(timeString.substring(0, hourIndex));
			int minute = Integer.parseInt(timeString.substring(hourIndex + 1));
			selectDate.set(Calendar.HOUR, hour);
			selectDate.set(Calendar.MINUTE,minute);
		}
	}

	@Override
	public void onClick(View v) {
        if(v == addButton) {
			isInsert = true;
			new TimePickerDialog(this, timeSetListener, 12, 0, true).show();
		} else if(v == saveButton) {
			myHelper = new MySqliteHelper(this, "my.db", null, 1);
			String title = addDialogTitle.getText().toString();
			String content = addDialogContent.getText().toString();
			if(isInsert) {
				ArrayList<HashMap<String, String>> todayTable = myHelper.queryData(selectDate);
				System.out.println(todayTable.toString());
				boolean isExist = false;
				for(int i = 0; i < todayTable.size(); i++) {
					if((selectDate.get(Calendar.HOUR) == Integer.parseInt(todayTable.get(i).get("time").substring(0, 2))) &&
							selectDate.get(Calendar.MINUTE) == Integer.parseInt(todayTable.get(i).get("time").substring(3, 5))) {
						isExist = true;
						
						System.out.println(selectDate.get(Calendar.HOUR));
						System.out.println(Integer.parseInt(todayTable.get(i).get("time").substring(0, 2)));
						System.out.println(selectDate.get(Calendar.HOUR));
						System.out.println(Integer.parseInt(todayTable.get(i).get("time").substring(3, 5)));
					}
				}
				if(isExist) {
					Toast.makeText(DateTable.this, "You are busy in this monemt", Toast.LENGTH_SHORT).show();
				} else {
					myHelper.insertData(title, content, selectDate);
				}
			}
			else {
				myHelper.updateData(title, content, selectDate);
			}
			alertDialog.dismiss();
			showListViewItem();
		} else if(v == backButton) {
			alertDialog.dismiss();
		} else if(v == deleteButton) {
			myHelper = new MySqliteHelper(this, "my.db", null, 1);
			myHelper.deleteData(selectDate);
			items = new ArrayList<HashMap<String,String>>();
			items = myHelper.queryData(selectDate);
			
			setAdapter();
		} else if(v == editButton) {
			isInsert = false;
			showAddDialog();
		}
	}
	
	public class expandableChildClickListener implements OnChildClickListener {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			ExpandableListAdapter adapter = parent.getExpandableListAdapter();
			HashMap<String, String> hm = (HashMap<String, String>)adapter.getGroup(groupPosition);
			String selectedDateString = hm.get("father");
			System.out.println(selectedDateString);
			int year = Integer.parseInt(selectedDateString.substring(0, 4));
			System.out.println(year + "");
			int index = selectedDateString.lastIndexOf("-");
			int monthOfYear = Integer.parseInt(selectedDateString.substring(5, index));
			System.out.println(monthOfYear + "");
			int dayOfMonth = Integer.parseInt(selectedDateString.substring(index + 1));
			System.out.println(dayOfMonth + "");
			showListViewItem();
			return false;
		}

	}
	
	TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
		boolean isFirst = true;
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if (isFirst) {
				selectDate.set(Calendar.HOUR,hourOfDay);
				selectDate.set(Calendar.MINUTE,minute);
				showAddDialog();
				isFirst = false;
			} else {
				isFirst = true;
			}
		}
	};


	private void showAddDialog() {
		AlertDialog.Builder builder;
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);  
        
        View layout = inflater.inflate(R.layout.schedule_add_dialog, null); 
        addDialogContent = (EditText)layout.findViewById(R.id.scheduleBody);
        addDialogTitle = (EditText)layout.findViewById(R.id.scheduleTitle);
        backButton = (Button)layout.findViewById(R.id.scheduleBack);
		saveButton = (Button)layout.findViewById(R.id.scheduleSave);
		
		saveButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		
		if(isInsert == false) {
			addDialogTitle.setText(selectScheduleTitle);
			addDialogContent.setText(selectScheduleContent);
		}
        
        builder = new AlertDialog.Builder(this);  
        builder.setView(layout);
        
        alertDialog = builder.create();  
        alertDialog.show(); 
	}
	
	
	private void showListViewItem() {
		myHelper = new MySqliteHelper(this, "my.db", null, 1);
		items = new ArrayList<HashMap<String,String>>();
		items = myHelper.queryData(selectDate);
		
		setAdapter();
	}
	
	private void setAdapter() {
		myAdapter = new DateListAdapter(this, items);
		curDate.setAdapter(myAdapter);
	}
}