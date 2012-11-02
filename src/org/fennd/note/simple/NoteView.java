package org.fennd.note.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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

	private final static String FILENAMES_FILE = "note_filenames.dat";
	private final static String NOTENAMES_FILE = "note_note_names.dat";

	private Note activeNote;
	private SharedPreferences noteState;

	private ArrayList<String> filenames;
	private ArrayList<String> noteNames;
	private int activeIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		noteState = getPreferences(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		filenames = fetchFilenames();
		noteNames = fetchNoteNames();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null
				&& extras.containsKey(ChangeNoteView.EXTRA_NOTE_SELECTED)) {
			// Restore note based on user selection.
			activeIndex = extras.getInt(ChangeNoteView.EXTRA_NOTE_SELECTED);
			String filename = filenames.get(activeIndex);
			activeNote = fetchNote(filename);
			updateWidgetText();
			
		} else {
			// Restore last active note.
			restoreActiveNote();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveCurrentNote();

		saveNoteLists();
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
		// Update note name list.
		EditText nameWidget = (EditText) findViewById(R.id.note_title);
		String noteName = nameWidget.getText().toString();
		noteNames.set(activeIndex, noteName);
		
		// Start note selection activity.
		Intent intent = new Intent(this, ChangeNoteView.class);
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

		if ("".equals(newNoteTitle)) {
			newNoteTitle = getUntitledName();
		}
		String filename = getNewFilename();
		activeNote = new Note(newNoteTitle, "", filename);
		
		// Update filename and noteName list.
		activeIndex = filenames.size();
		filenames.add(filename);
		noteNames.add(newNoteTitle);

		updateWidgetText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fennd.note.simple.DeleteNoteDialog.DeleteDialogListener#
	 * onDeleteDialogPositiveClick(org.fennd.note.simple.DeleteNoteDialog)
	 */
	public void onDeleteDialogPositiveClick(DeleteNoteDialog dialog) {
		filenames.remove(activeIndex);
		noteNames.remove(activeIndex);
		deleteFile(activeNote.getFilename());

		if (filenames.isEmpty()) {
			// Deleted last note, so create an untitled note.
			activeNote = createUntitledNote();
			updateWidgetText();

		} else {
			// Switch to the first note in the list.
			loadNote(filenames.get(0));
		}
		
		activeIndex = findActiveIndex();
	}

	private void restoreActiveNote() {
		String filename;
		if (filenames.isEmpty()) {
			// No notes have been created yet.
			activeNote = createUntitledNote();

		} else {
			// At least one note exists.
			filename = noteState.getString("lastActive", "");
			activeNote = fetchNote(filename);
		}

		activeIndex = findActiveIndex();
		updateWidgetText();
	}
	
	private void saveCurrentNote() {
		// Save file name for later restoration.
		noteState.edit().putString("lastActive", activeNote.getFilename())
				.commit();

		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		EditText bodyWidget = (EditText) findViewById(R.id.note_body);
		String noteTitle = titleWidget.getText().toString();
		String noteBody = bodyWidget.getText().toString();
		activeNote.setNoteTitle(noteTitle);
		activeNote.setNoteBody(noteBody);
		
		noteNames.set(activeIndex, noteTitle);
		
		saveObjectToFile(activeNote, activeNote.getFilename());
	}

	private void saveNoteLists() {
		saveObjectToFile(filenames, FILENAMES_FILE);
		saveObjectToFile(noteNames, NOTENAMES_FILE);
	}

	private void saveObjectToFile(Object object, String filename) {
		try {
			FileOutputStream outputStream = openFileOutput(filename,
					Context.MODE_PRIVATE);
			ObjectOutputStream serializedOutput = new ObjectOutputStream(
					outputStream);
			
			serializedOutput.writeObject(object);
			
			outputStream.close();
			serializedOutput.close();
		} catch (IOException e) {
			// TODO: Exception.
			e.printStackTrace();
		}
	}
	
	private void loadNote(String filename) {
		activeNote = fetchNote(filename);
		updateWidgetText();
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

	private ArrayList<String> fetchFilenames() {
		return fetchSerializedArrayList(FILENAMES_FILE);
	}
	
	private ArrayList<String> fetchNoteNames() {
		return fetchSerializedArrayList(NOTENAMES_FILE);
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> fetchSerializedArrayList(String filename) {
		File file = getBaseContext().getFileStreamPath(filename);
		if (!file.exists()) {
			return new ArrayList<String>();
		}

		try {
			FileInputStream inputStream = openFileInput(filename);
			ObjectInputStream sInput = new ObjectInputStream(inputStream);
			ArrayList<String> names = (ArrayList<String>) sInput.readObject();

			inputStream.close();
			sInput.close();
			return names;

		} catch (IOException e) {
			// TODO: Exception.
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Exception.
			e.printStackTrace();
		}

		// This line should never run.
		return new ArrayList<String>();
	}

	private void updateWidgetText() {
		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		EditText bodyWidget = (EditText) findViewById(R.id.note_body);
		titleWidget.setText(activeNote.getNoteTitle());
		bodyWidget.setText(activeNote.getNoteBody());
	}

	private Note createUntitledNote() {
		String noteTitle = getUntitledName();
		String noteBody = "";
		String filename = getNewFilename();

		filenames.add(filename);
		noteNames.add(noteTitle);
		
		return new Note(noteTitle, noteBody, filename);
	}

	private String getNewFilename() {
		int fileNum = noteState.getInt("fileNum", 0);
		fileNum++;
		noteState.edit().putInt("fileNum", fileNum).commit();

		return "NoteFile" + fileNum;
	}

	private String getUntitledName() {
		return getUntitledName(true);
	}

	private String getUntitledName(Boolean doIncrement) {
		int count = noteState.getInt("untitledCount", 1);
		if (doIncrement) {
			noteState.edit().putInt("untitledCount", count + 1).commit();
		}

		return "Untitled Note " + Integer.toString(count);
	}

	private int findActiveIndex() {
		String filename = activeNote.getFilename();
		
		for (int i = 0; i < filenames.size(); i++) {
			if (filename.equals(filenames.get(i))) {
				return i;
			}
		}
		return -1;
	}

}