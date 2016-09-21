package com.example.guifeng.myworld;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MySqliteHelper extends SQLiteOpenHelper{

	public MySqliteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists schedule("
				+ "title varchar,"
				+ "content varchar,"
				+ "date varchar,"
				+ "time varchar)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	
	public void insertData(String title, String content, Calendar date) {
		String dateString = (date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH));
		String timeString;
		if(date.get(Calendar.MINUTE) >= 10 && date.get(Calendar.HOUR) < 10)
			timeString = "0" + date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE);
		else if(date.get(Calendar.MINUTE) < 10 && date.get(Calendar.HOUR) >= 10)
			timeString = date.get(Calendar.HOUR) + ":" + "0" + date.get(Calendar.MINUTE);
		else if(date.get(Calendar.MINUTE) < 10 && date.get(Calendar.MINUTE) < 10)
			timeString = "0" + date.get(Calendar.HOUR) + ":" + "0" + date.get(Calendar.MINUTE);
		else
			timeString = date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE);
		SQLiteDatabase db = getWritableDatabase();
		//db.execSQL("insert into schedule(title,content,date) values(title,content,dateString)");
		ContentValues contentValues = new ContentValues();
		contentValues.put("title", title);
		contentValues.put("content", content);
		contentValues.put("date", dateString);
		contentValues.put("time", timeString);
		db.insert("schedule", null, contentValues);
		db.close();
	}
	
//	public ArrayList<HashMap<String, String>> queryData() {
//		SQLiteDatabase db = getReadableDatabase();
//		Cursor cursor = db.query("schedule", null, null, null, null, null, "date asc");
//		int titleIndex = cursor.getColumnIndex("title");
//		int contentIndex = cursor.getColumnIndex("content");
//		int dateIndex = cursor.getColumnIndex("date");
//		int timeIndex = cursor.getColumnIndex("time");
//		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String,String>>();
//		
//		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
//			HashMap<String, String> note = new HashMap<String, String>();
//			note.put("title", cursor.getString(titleIndex));
//			note.put("content", cursor.getString(contentIndex));
//			note.put("date", cursor.getString(dateIndex));
//			note.put("time", cursor.getString(timeIndex));
//			items.add(note);
//		}
//		cursor.close();
//		db.close();
//		
//		return items;
//	}
	
	public ArrayList<HashMap<String, String>> queryData(Calendar date) {
		String dateString = (date.get(Calendar.YEAR) + 1900) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("schedule", null, "date=?", new String[]{dateString}, null, null, "time asc");
		int titleIndex = cursor.getColumnIndex("title");
		int contentIndex = cursor.getColumnIndex("content");
		int dateIndex = cursor.getColumnIndex("date");
		int timeIndex = cursor.getColumnIndex("time");
		ArrayList<HashMap<String, String>> items;
		items = new ArrayList<>();

		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
			HashMap<String, String> note = new HashMap<>();
			note.put("title", cursor.getString(titleIndex));
			note.put("content", cursor.getString(contentIndex));
			note.put("date", cursor.getString(dateIndex));
			note.put("time", cursor.getString(timeIndex));
			items.add(note);
		}
		cursor.close();
		db.close();
		
		return items;
	}
	
	public void deleteData(Calendar date) {
		String dateString = (date.get(Calendar.YEAR) + 1900) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
		String timeString;
		if(date.get(Calendar.MINUTE) >= 10 && date.get(Calendar.HOUR) < 10)
			timeString = "0" + date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE);
		else if(date.get(Calendar.MINUTE) < 10 && date.get(Calendar.HOUR) >= 10)
			timeString = date.get(Calendar.HOUR) + ":" + "0" + date.get(Calendar.MINUTE);
		else if(date.get(Calendar.MINUTE) < 10 && date.get(Calendar.HOUR) < 10)
			timeString = "0" + date.get(Calendar.HOUR) + ":" + "0" + date.get(Calendar.MINUTE);
		else
			timeString = date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE);
		
		SQLiteDatabase db = getWritableDatabase();
		
		db.delete("schedule", "date=? AND time=?", new String[]{dateString, timeString});
		db.close();
	}
	
	public void updateData(String title, String content, Calendar date) {
		String dateString = (date.get(Calendar.YEAR) + 1900) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
		String timeString;
		if(date.get(Calendar.MINUTE) >= 10 && date.get(Calendar.HOUR) < 10)
			timeString = "0" + date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE);
		else if(date.get(Calendar.MINUTE) < 10 && date.get(Calendar.HOUR) >= 10)
			timeString = date.get(Calendar.HOUR) + ":" + "0" + date.get(Calendar.MINUTE);
		else if(date.get(Calendar.MINUTE) < 10 && date.get(Calendar.HOUR) < 10)
			timeString = "0" + date.get(Calendar.HOUR) + ":" + "0" + date.get(Calendar.MINUTE);
		else
			timeString = date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE);
		
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("title", title);
		values.put("content", content);
		
		db.update("schedule", values, "date=? AND time=?", new String[]{dateString, timeString});
		db.close();
	}
}