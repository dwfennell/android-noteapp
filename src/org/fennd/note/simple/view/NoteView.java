/*
 * Copyright 2012 Dustin Fennell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fennd.note.simple.view;

import java.io.IOException;

import org.fennd.note.simple.R;
import org.fennd.note.simple.controller.NoteController;
import org.fennd.note.simple.model.Note;
import org.fennd.note.simple.view.DeleteNoteDialog.DeleteDialogListener;
import org.fennd.note.simple.view.NewNoteDialog.NewNoteDialogListener;

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
	
	private final static String ERROR_MESSAGE_CREATE = "Memory Error: Note could note be created.";
	private final static String ERROR_MESSAGE_LOAD = "Memory Error: Could not load note.";

	private Note activeNote;
	private SharedPreferences noteState;
	private NoteController noteControl = new NoteController(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		noteState = getPreferences(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// Load note.
		Bundle extras = getIntent().getExtras();
		if (extras != null
				&& extras.containsKey(SelectNoteView.EXTRA_NOTE_SELECTED)) {
			// Load note based on users selection in SelectNoteView.
			int selectedIndex = extras.getInt(SelectNoteView.EXTRA_NOTE_SELECTED);
			activeNote = noteControl.loadNote(selectedIndex);

		} else {
			// Restore last active note.
			String activeFilename = noteState.getString("lastActive", "");
			activeNote = noteControl.loadNote(activeFilename);
		}
		
		updateWidgetText();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// Save note.
		try {
			saveCurrentNote();
		} catch (IOException e) {
			handleException(e, ERROR_MESSAGE_CREATE);
		}
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
		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		String noteTitle = titleWidget.getText().toString();
		activeNote.setNoteTitle(noteTitle);
		
		noteControl.updateNoteName(activeNote);
		
		// Start note selection activity.
		Intent intent = new Intent(this, SelectNoteView.class);
		intent.putExtra(EXTRA_NOTELIST, noteControl.getNoteList());
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
		try {
			saveCurrentNote();
		} catch (IOException e) {
			handleException(e, ERROR_MESSAGE_CREATE);
		}

		String newNoteTitle = dialog.getNewTitle();

		if ("".equals(newNoteTitle)) {
			newNoteTitle = getUntitledName();
		}
		String filename = getNewFilename();
		activeNote = new Note(newNoteTitle, "", filename);

		updateWidgetText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fennd.note.simple.DeleteNoteDialog.DeleteDialogListener#
	 * onDeleteDialogPositiveClick(org.fennd.note.simple.DeleteNoteDialog)
	 */
	public void onDeleteDialogPositiveClick(DeleteNoteDialog dialog) {
		activeNote = noteControl.delete(activeNote);		
		updateWidgetText();
	}

	private void saveCurrentNote() throws IOException {
		// Save file name for later restoration.
		noteState.edit().putString("lastActive", activeNote.getFilename())
				.commit();
		
		// Fetch updated text.
		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		EditText bodyWidget = (EditText) findViewById(R.id.note_body);
		String noteTitle = titleWidget.getText().toString();
		String noteBody = bodyWidget.getText().toString();
		activeNote.setNoteTitle(noteTitle);
		activeNote.setNoteBody(noteBody);

		noteControl.saveNote(activeNote);
	}

	private void updateWidgetText() {
		EditText titleWidget = (EditText) findViewById(R.id.note_title);
		EditText bodyWidget = (EditText) findViewById(R.id.note_body);
		titleWidget.setText(activeNote.getNoteTitle());
		bodyWidget.setText(activeNote.getNoteBody());
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

	private void handleException(Exception e, String message) {
		// TODO: Inform user of exception... or something.
	}
}