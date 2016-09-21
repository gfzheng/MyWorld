package com.example.guifeng.myworld;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DateListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, String>> listItems;
	private LayoutInflater layoutContainer;
	private View selectedView;
	private int selected;
	public class DateItem {
		public TextView date;
		public TextView title;
		public TextView content;
	}
	
	public DateListAdapter (Context context, ArrayList<HashMap<String, String>> listItems) {
		this.context = context;
		layoutContainer = LayoutInflater.from(this.context);
		this.listItems = listItems;
		selectedView = null;
		selected = 0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = layoutContainer.inflate(R.layout.datelistitem, null);
		DateItem listItemView = new DateItem ();
		
		listItemView.date = (TextView)convertView.findViewById (R.id.time);
		listItemView.title = (TextView)convertView.findViewById (R.id.itemtitle);
		listItemView.content = (TextView)convertView.findViewById (R.id.itemcontent);
		//listItemView.content.setText("This text view is now actived!! Here is the contents of this item!");
		
		HashMap<String, String> appInfo = listItems.get(position);  
        if (appInfo != null) {  
            String title = (String)appInfo.get(new String("title"));    
            String content = (String)appInfo.get(new String("content"));
            //String date = (String)appInfo.get(new String("date"));
            String time = (String)appInfo.get(new String("time"));
            //listItemView.date.setText(date + " " + time);
            listItemView.date.setText(time);
            listItemView.title.setText(title);
            listItemView.content.setText(content);
        }
		
		convertView.setTag(listItemView);
		if (selected == position) {
			listItemView.content.setMaxHeight(99999);
			selectedView = convertView;
		}
		
		return convertView;
	}

	public ArrayList<String> setSelected (View convertView, int position) {
		if (selectedView != null) {
			DateItem oldItemView = (DateItem)selectedView.getTag();
			oldItemView.content.setMaxHeight(0);
		}
		DateItem listItemView = (DateItem)convertView.getTag();
		listItemView.content.setMaxHeight(99999);
		selectedView = convertView;
		selected = position;
		
		String title = listItemView.title.getText().toString();
		String content = listItemView.content.getText().toString();
		String date = listItemView.date.getText().toString();
		
		ArrayList<String> note = new ArrayList<String>();
		note.add(title);
		note.add(content);
		note.add(date);
		
		return note;
	}
}
