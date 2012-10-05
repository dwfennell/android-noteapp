package org.fennd.note.simple;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;

public class Note extends Activity {

	private String activeNoteName;
	private String activeNoteBody;
	private SharedPreferences noteState;

	// TODO: Respond to activity button presses.
	// TODO: Respond (gracefully?) to title changes.
	// TODO: Note body io.
	// TODO: validate note names?

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);

		noteState = getPreferences(0);
		restoreLastNote();
	}

	@Override
	protected void onResume() {
		super.onResume();

		restoreLastNote();
	}

	@Override
	protected void onPause() {
		super.onPause();

		noteState.edit().putString("lastActive", activeNoteName);
		saveCurrentNote();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_note, menu);
		return true;
	}

	private void restoreLastNote() {

		if (noteState.getInt("numberOfNotes", 0) == 0) {
			// No notes have been created yet.

			noteState.edit().putInt("untitledCount", 1).commit();
			activeNoteName = getUntitledName(1);
			activeNoteBody = "";

			createNewNote(activeNoteName);

		} else {
			activeNoteName = noteState.getString("lastActive", "");
			activeNoteBody = fetchNoteBody(activeNoteName);
		}

		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		titleWidget.setText(activeNoteName);

		EditText bodyWidget = (EditText) findViewById(R.id.note_body);
		bodyWidget.setText(activeNoteBody);
	}

	private void saveCurrentNote() {

		// Deal with changes to the note title.
		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		String noteTitle = titleWidget.getText().toString();

		if (!noteTitle.equals(activeNoteName)) {
			// Note title has been changed.
			// TODO handle note title changes here?
		}

		try {
			FileOutputStream fos = openFileOutput(activeNoteName,
					Context.MODE_PRIVATE);
			fos.write(activeNoteBody.getBytes());
			fos.close();

		} catch (IOException e) {
			// TODO: Find out what should be done here.

			e.printStackTrace();
		}

	}

	private void createNewNote(String name) {
		// Increment note count.
		int num = noteState.getInt("numberOfNotes", 0);
		num++;
		noteState.edit().putInt("numberOfNotes", num).commit();

	}

	private String fetchNoteBody(String name) {

		// TODO: Load note text from file, if it exists.

		return "";
	}

	private String getUntitledName(Integer count) {
		return "Untitled Note " + count.toString();
	}

}
