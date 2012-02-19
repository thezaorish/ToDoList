package com.zaorish.android.todo.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskDao {

	private static final String DATABASE_NAME = "todo.db";
	private static final int DATABASE_VERSION = 1;

	private static final String TABLE_NAME = "task";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_PRIORITY = "priority";

	protected static final String TABLE_CREATE = "create table " + TABLE_NAME + " (" +
													COLUMN_ID + " integer primary key autoincrement, " +
													COLUMN_NAME + " text not null, " +
													COLUMN_DESCRIPTION + " text not null, " +
													COLUMN_PRIORITY + " integer);";

	private SQLiteDatabase database;

	public TaskDao(Context context) {
		ToDoSQLiteOpenHelper sqlHelper = new ToDoSQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		database = sqlHelper.getWritableDatabase();
	}

	public long create(String name, String description, int priority) {
		ContentValues initialValues = configureContentValues(name, description, priority);
		return database.insert(TABLE_NAME, null, initialValues);
	}

	public Cursor get(long rowId) {
		String[] columns = configureColumns();
		Cursor mCursor = database.query(true, TABLE_NAME, columns, COLUMN_ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getAll() {
		String[] columns = configureColumns();
		return database.query(TABLE_NAME, columns, null, null, null, null, null);
	}

	public boolean update(long id, String name, String description, int priority) {
		ContentValues initialValues = configureContentValues(name, description, priority);
		return database.update(TABLE_NAME, initialValues, COLUMN_ID + "=" + id, null) > 0;
	}

	public boolean deleteNote(long taskId) {
		return database.delete(TABLE_NAME, COLUMN_ID + "=" + taskId, null) > 0;
	}

	private ContentValues configureContentValues(String name, String description, int priority) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLUMN_NAME, name);
		initialValues.put(COLUMN_DESCRIPTION, description);
		initialValues.put(COLUMN_PRIORITY, priority);
		return initialValues;
	}

	private String[] configureColumns() {
		return new String[] { COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_PRIORITY };
	}

}