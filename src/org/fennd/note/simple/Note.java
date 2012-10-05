package org.fennd.note.simple;

import android.app.Activity;
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
		// TODO save note to file. Possible issue with re-titling notes.
		
		
	}

	private void createNewNote(String name) {
		// Increment note count.
		int num = noteState.getInt("numberOfNotes", 0);
		num++;
		noteState.edit().putInt("numberOfNotes", num).commit();
		
	}
	
	private String fetchNoteBody(String name) {
		
		//TODO: Load note text to file, if it exists.
		
		return "";
	}
	
	private String getUntitledName(Integer count) {
		return "Untitled Note " + count.toString();
	}
	
}
