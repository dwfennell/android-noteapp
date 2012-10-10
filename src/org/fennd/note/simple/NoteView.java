package org.fennd.note.simple;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class NoteView extends Activity {

	private String activeNoteName;
	private String activeNoteBody;
	private SharedPreferences noteState;

	// TODO: Respond to activity button presses.
	// TODO: Respond (gracefully?) to title changes.
	// TODO: validate note names?
	// TODO: Improve efficiency/robustness of persistence.
	// TODO: Note renaming isn't working correctly.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);

		noteState = getPreferences(0);
	}

	@Override
	protected void onResume() {
		super.onResume();

		restoreLastNote();
	}

	@Override
	protected void onPause() {
		super.onPause();

		noteState.edit().putString("lastActive", activeNoteName).commit();
		saveCurrentNote();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_note, menu);
		return true;
	}

	public void newButtonClick(View view) {
		System.out.print("button clicked");
	}

	public void noteListButtonClick(View view) {
		System.out.print("button clicked");
	}

	public void settingsButtonClick(View view) {
		System.out.print("button clicked");
	}

	private void restoreLastNote() {
		int numberOfNotes = noteState.getInt("numberOfNotes", 0);

		if (numberOfNotes == 0) {
			// No notes have been created yet.

			noteState.edit().putInt("untitledCount", 1).commit();
			activeNoteName = getUntitledName(1);
			activeNoteBody = "";

			// Increment note count.
			// TODO: Do we even need to keep a count? Would a boolean do here?
			Editor stateEditor = noteState.edit();
			stateEditor.putInt("numberOfNotes",
					noteState.getInt("numberOfNotes", 0) + 1);
			stateEditor.commit();

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

		EditText bodyWidget = (EditText) findViewById(R.id.note_body);
		activeNoteBody = bodyWidget.getText().toString();

		if (!noteTitle.equals(activeNoteName)) {
			// Note title has been changed.

			deleteFile(activeNoteName);
			activeNoteName = noteTitle;
		}

		try {
			FileOutputStream fos = openFileOutput(activeNoteName,
					Context.MODE_PRIVATE);
			fos.write(activeNoteBody.getBytes());
			fos.close();

		} catch (IOException e) {
			// TODO: Exception.
			e.printStackTrace();
		}
	}

	private String fetchNoteBody(String name) {
		final Set<String> files = new HashSet<String>(Arrays.asList(fileList()));

		if (!files.contains(name)) {
			// File does not exist.
			return "";
		}

		try {
			FileInputStream fis = openFileInput(activeNoteName);
			byte[] input = new byte[fis.available()];
			while (fis.read(input) != -1) {
			}

			return new String(input);

		} catch (IOException e) {
			// TODO exception.
			e.printStackTrace();
		}

		return "";
	}

	private String getUntitledName(Integer count) {
		return "Untitled Note " + count.toString();
	}

}
