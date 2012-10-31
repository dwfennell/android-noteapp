package org.fennd.note.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.fennd.note.simple.DeleteNoteDialog.DeleteDialogListener;
import org.fennd.note.simple.NewNoteDialog.NewNoteDialogListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class NoteView extends FragmentActivity implements
		NewNoteDialogListener, DeleteDialogListener {

	public final static String EXTRA_NOTELIST = "org.fennd.note.simple.NOTELIST";

	private Note activeNote;
	private SharedPreferences noteState;
	private LinkedHashMap<String, String> filenameToNoteName;

	private final static String NOTE_LIST_FILENAME = "note_list.dat";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);

		noteState = getPreferences(0);
	}

	@Override
	protected void onResume() {
		super.onResume();

		filenameToNoteName = fetchNoteList();

		Intent intent = getIntent();
		if (intent.getExtras() != null
				&& intent.getExtras()
						.containsKey(ChangeNoteView.EXTRA_NOTENAME)) {
			// Load a note based on note selection activity item click.
			restoreNote(intent.getExtras().getString(
					ChangeNoteView.EXTRA_NOTENAME));
		} else {
			restoreLastNote();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveCurrentNote();
		saveNoteList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_note, menu);
		return true;
	}

	public void newButtonClick(View view) {
		NewNoteDialog newDialog = NewNoteDialog.newInstance(this);
		newDialog.show(getSupportFragmentManager(), "NewNoteDialog");
	}

	public void deleteButtonClick(View view) {
		DeleteNoteDialog deleteDialog = DeleteNoteDialog.newInstance(this);
		deleteDialog.show(getSupportFragmentManager(), "DeleteNoteDialog");
	}

	public void noteListButtonClick(View view) {
		// Make sure current note's name is in list of note names.
		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		String noteTitle = titleWidget.getText().toString();
		filenameToNoteName.put(activeNote.getFilename(), noteTitle);

		// Setup intent to start new activity.
		Intent intent = new Intent(this, ChangeNoteView.class);
		ArrayList<String> noteNames = new ArrayList<String>(
				filenameToNoteName.values());
		intent.putExtra(EXTRA_NOTELIST, noteNames);

		startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fennd.note.simple.NewNoteDialog.NewNoteDialogListener#
	 * onDialogPositiveClick(android.support.v4.app.DialogFragment)
	 */
	public void onNewNoteDialogPositiveClick(NewNoteDialog dialog) {
		// Save previous note.
		saveCurrentNote();

		String newNoteTitle = dialog.getNewTitle();

		EditText noteTitle = (EditText) findViewById(R.id.note_title);
		EditText noteBody = (EditText) findViewById(R.id.note_body);
		noteTitle.setText(newNoteTitle);
		noteBody.setText("");

		activeNote = new Note(newNoteTitle, "", getNewFilename());
		saveCurrentNote();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fennd.note.simple.DeleteNoteDialog.DeleteDialogListener#
	 * onDeleteDialogPositiveClick(org.fennd.note.simple.DeleteNoteDialog)
	 */
	public void onDeleteDialogPositiveClick(DeleteNoteDialog dialog) {
		String filename = activeNote.getFilename();
		
		filenameToNoteName.remove(filename);
		deleteFile(filename);
		
		if (filenameToNoteName.isEmpty()) {
			createUntitledNote();
		} else {
			// Just switch to the first note in this set.
			for (String newfile : filenameToNoteName.keySet()) {
				loadNote(newfile);
				break;
			}
		}
	}

	private void restoreLastNote() {
		if (noteState.getBoolean("aNoteExists", false)) {
			// A note exists in the system.

			// TODO: Try to avoid io here, when possible.
			String activeNoteFileName = noteState.getString("lastActive", "");
			activeNote = fetchNote(activeNoteFileName);
		} else {
			// No notes have been created yet.
			
			createUntitledNote();
		}

		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		titleWidget.setText(activeNote.getNoteTitle());

		EditText bodyWidget = (EditText) findViewById(R.id.note_body);
		bodyWidget.setText(activeNote.getNoteBody());
	}

	private void restoreNote(String noteName) {
		String filename = getFilename(noteName);

		if (filename.equals(null)) {
			// Filename not found, restore last note.

			restoreLastNote();
		} else {
			activeNote = fetchNote(filename);

			EditText titleWidget = (EditText) findViewById(R.id.note_title);
			titleWidget.setText(activeNote.getNoteTitle());
			EditText bodyWidget = (EditText) findViewById(R.id.note_body);
			bodyWidget.setText(activeNote.getNoteBody());
		}
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

		filenameToNoteName.put(activeNote.getFilename(), noteTitle);

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

	private void saveNoteList() {
		try {
			FileOutputStream outputStream = openFileOutput(NOTE_LIST_FILENAME,
					Context.MODE_PRIVATE);
			ObjectOutputStream serializedOutput = new ObjectOutputStream(
					outputStream);

			serializedOutput.writeObject(filenameToNoteName);

			outputStream.close();
			serializedOutput.close();
		} catch (IOException e) {
			// TODO: Exception.
			e.printStackTrace();
		}
	}
	
	private void loadNote(String filename) {		
		activeNote = fetchNote(filename);

		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		titleWidget.setText(activeNote.getNoteTitle());
		EditText bodyWidget = (EditText) findViewById(R.id.note_body);
		bodyWidget.setText(activeNote.getNoteBody());
	}
	
	private Note fetchNote(String noteFilename) {
		Note note = new Note(noteFilename);

		try {
			FileInputStream inputStream = openFileInput(noteFilename);
			ObjectInputStream serializedInput = new ObjectInputStream(
					inputStream);

			// There will only be one note object in this file.
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

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, String> fetchNoteList() {
		File file = getBaseContext().getFileStreamPath(NOTE_LIST_FILENAME);
		if (file.exists()) {
			try {
				FileInputStream inputStream = openFileInput(NOTE_LIST_FILENAME);
				ObjectInputStream serializedInput = new ObjectInputStream(
						inputStream);
				LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) serializedInput
						.readObject();

				inputStream.close();
				serializedInput.close();
				return map;
			} catch (IOException e) {
				// TODO: Exception.
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Exception.
				e.printStackTrace();
			}
		}

		return new LinkedHashMap<String, String>();
	}

	private void createUntitledNote() {
		String noteTitle = getUntitledName(1);
		String noteBody = "";
		String filename = getNewFilename();
		activeNote = new Note(noteTitle, noteBody, filename);

		// Update note existence flag.
		noteState.edit().putBoolean("aNoteExists", true).commit();

		filenameToNoteName.put(filename, noteTitle);
		loadNote(filename);
	}
	
	private String getNewFilename() {
		int fileNum = noteState.getInt("fileNum", 0);
		fileNum++;
		noteState.edit().putInt("fileNum", fileNum).commit();

		return "NoteFile" + fileNum;
	}

	private String getUntitledName(Integer count) {
		return "Untitled Note " + count.toString();
	}

	private String getFilename(String noteName) {
		for (String filename : filenameToNoteName.keySet()) {
			if (filenameToNoteName.get(filename).equals(noteName)) {
				return filename;
			}
		}

		return null;
	}

}
