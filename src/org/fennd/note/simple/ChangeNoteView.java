package org.fennd.note.simple;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChangeNoteView extends Activity {
	public final static String EXTRA_NOTE_SELECTED = "org.fennd.note.simple.NOTENAME";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_note_view);
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		final ArrayList<String> noteNames = intent.getExtras()
				.getStringArrayList(NoteView.EXTRA_NOTELIST);

		ListView noteDisplay = (ListView) findViewById(R.id.note_list_view);

		// Respond to list item clicks.
		noteDisplay.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent newActIntent = new Intent(ChangeNoteView.this,
						NoteView.class);
				newActIntent.putExtra(EXTRA_NOTE_SELECTED, position);
				startActivity(newActIntent);
			}
		});

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				noteNames);
		noteDisplay.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_change_note_view, menu);
		return true;
	}

	// TODO: This is disabled for now until I can get the ABS lib working.
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case android.R.id.home:
	// NavUtils.navigateUpFromSameTask(this);
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

}
