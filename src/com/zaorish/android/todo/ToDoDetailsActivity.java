package com.zaorish.android.todo;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.zaorish.android.R;
import com.zaorish.android.todo.dataaccess.TaskDao;

/**
 * Displays the details of the task, with the ability to save/update the task
 */
public class ToDoDetailsActivity extends Activity {

	private TaskDao taskDao;

	private EditText taskName;
	private EditText taskDescription;
	private RadioGroup priorityGroup;

	private Long taskId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_details);

		taskDao = new TaskDao(this);

		taskName = (EditText) findViewById(R.id.taskName);
		taskDescription = (EditText) findViewById(R.id.taskDescription);
		priorityGroup = (RadioGroup) findViewById(R.id.radioTaskPriority);

		taskId = savedInstanceState == null ? null : (Long) savedInstanceState.getSerializable(TaskDao.COLUMN_ID);
		if (taskId == null) {
			Bundle extras = getIntent().getExtras();
			taskId = extras != null ? extras.getLong(TaskDao.COLUMN_ID) : null;
		}

		bindTask();

		Button saveButton = (Button) findViewById(R.id.saveTask);
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveCurrentTask();

				setResult(RESULT_OK);
				finish();
			}
		});
	}

	private void bindTask() {
		if (taskId != null) {
			Cursor task = taskDao.get(taskId);
			startManagingCursor(task);

			taskName.setText(task.getString(task.getColumnIndexOrThrow(TaskDao.COLUMN_NAME)));
			taskDescription.setText(task.getString(task.getColumnIndexOrThrow(TaskDao.COLUMN_DESCRIPTION)));

			int priority = task.getInt(task.getColumnIndexOrThrow(TaskDao.COLUMN_PRIORITY));
			if (priority == 10) {
				priorityGroup.check(R.id.radioTaskHigh);
			}
			if (priority == 5) {
				priorityGroup.check(R.id.radioTaskLow);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		bindTask();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		saveCurrentTask();
		savedInstanceState.putSerializable(TaskDao.COLUMN_ID, taskId);
	}

	private void saveCurrentTask() {
		String name = taskName.getText().toString();
		String description = taskDescription.getText().toString();
		int selected = priorityGroup.getCheckedRadioButtonId();

		int priority = 0;
		switch (selected) {
		case R.id.radioTaskHigh:
			priority = 10;
			break;
		case R.id.radioTaskLow:
			priority = 5;
			break;
		default:
			break;
		}

		saveTask(selected, name, description, priority);
	}
	private void saveTask(int selected, String name, String description, int priority) {
		if (taskId == null) {
			long id = taskDao.create(name, description, priority);
			if (id > 0) {
				taskId = id;
			}
		} else {
			taskDao.update(taskId, name, description, priority);
		}
	}

}
