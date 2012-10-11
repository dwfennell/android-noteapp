package org.fennd.note.simple;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class NoteView extends Activity {

	private Note activeNote;
	private SharedPreferences noteState;

	// TODO:Code activity button press responses.
	// TODO: Improve efficiency/robustness of persistence.

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

		saveCurrentNote();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_note, menu);
		return true;
	}

	public void newButtonClick(View view) {
		// TODO: Open new file dialog, wait for user to enter note name, then
		// create empty note.
	}

	public void noteListButtonClick(View view) {
		// TODO: Display list of existing notes (new activity?).
	}

	public void settingsButtonClick(View view) {
		// TODO: Display settings / user preferences.
		// Does this even need to be a button? Replace with delete button?
	}

	private void restoreLastNote() {
		if (noteState.getBoolean("aNoteExists", false)) {
			// A note exists in the system.

			// TODO: Try to avoid io here, when possible.
			// TODO: handle io failures.
			String activeNoteFileName = noteState.getString("lastActive", "");
			activeNote = fetchNote(activeNoteFileName);
		} else {
			// No notes have been created yet.
			String noteTitle = getUntitledName(1);
			String noteBody = "";

			activeNote = new Note(noteTitle, noteBody, getNewFileName());

			// Update note existence flag.
			Editor stateEditor = noteState.edit();
			stateEditor.putBoolean("aNoteExists", true);
			stateEditor.commit();
		}

		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		titleWidget.setText(activeNote.getNoteTitle());

		EditText bodyWidget = (EditText) findViewById(R.id.note_body);
		bodyWidget.setText(activeNote.getNoteBody());
	}

	private void saveCurrentNote() {
		// Save file name for later restoration.
		noteState.edit().putString("lastActive", activeNote.getFilename())
				.commit();

		// Fetch note title and content.
		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		String noteTitle = titleWidget.getText().toString();
		EditText bodyWidget = (EditText) findViewById(R.id.note_body);
		String noteBody = bodyWidget.getText().toString();

		activeNote.setNoteTitle(noteTitle);
		activeNote.setNoteBody(noteBody);

		// Serialize Note object and store it.
		try {
			FileOutputStream outputStream = openFileOutput(
					activeNote.getFilename(), Context.MODE_PRIVATE);
			ObjectOutputStream serializedOutput = new ObjectOutputStream(
					outputStream);
			serializedOutput.writeObject(activeNote);

			outputStream.close();
			serializedOutput.close();
		} catch (IOException e) {
			// TODO: Exception.
			e.printStackTrace();
		}
	}

	private Note fetchNote(String noteFilename) {
		Note note = new Note(noteFilename);

		try {
			FileInputStream inputStream = openFileInput(noteFilename);
			ObjectInputStream serializedInput = new ObjectInputStream(
					inputStream);

			// There will only be one note in this file.
			note = (Note) serializedInput.readObject();

			inputStream.close();
			serializedInput.close();
		} catch (IOException e) {
			// TODO Exception.
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Exception.
			e.printStackTrace();
		}

		return note;
	}

	private String getNewFileName() {
		int fileNum = noteState.getInt("fileNum", 0);

		fileNum++;
		noteState.edit().putInt("fileNum", fileNum);

		return "NoteFile" + fileNum;
	}

	private String getUntitledName(Integer count) {
		return "Untitled Note " + count.toString();
	}

}
