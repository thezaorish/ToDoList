package com.zaorish.android.todo;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.zaorish.android.R;
import com.zaorish.android.todo.dataaccess.TaskDao;

/**
 * Displays the existing tasks, allowing the ability to create new task or delete existing task
 */
public class ToDoListActivity extends ListActivity {

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private static final int ADD_TASK = Menu.FIRST;
	private static final int DELETE_TASK = Menu.FIRST + 1;

	private TaskDao taskDao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_listing);

		taskDao = new TaskDao(this);
		displayTasks();
	}
	private void displayTasks() {
		Cursor tasksCursor = taskDao.getAll();
		startManagingCursor(tasksCursor);

		String[] fromTaskNames = new String[] { TaskDao.COLUMN_NAME };
		int[] inTaskNames = new int[] { R.id.taskNameListing };

		SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.task_row, tasksCursor, fromTaskNames, inTaskNames);
		setListAdapter(notes);

		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, ADD_TASK, 0, R.string.add_task);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ADD_TASK:
			createTask();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}
	private void createTask() {
		Intent intent = new Intent(this, ToDoDetailsActivity.class);
		startActivityForResult(intent, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		Intent intent = new Intent(this, ToDoDetailsActivity.class);
		intent.putExtra(TaskDao.COLUMN_ID, id);
		startActivityForResult(intent, ACTIVITY_EDIT);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_TASK, 0, R.string.delete_task);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_TASK:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			taskDao.deleteNote(info.id);
			displayTasks();
			return true;
		}
		return super.onContextItemSelected(item);
	}

}
