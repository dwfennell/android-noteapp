package org.fennd.note.simple;

import java.util.Collection;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class ChangeNoteView extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_note_view);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Collection<String> noteNameList = NoteView.getNoteNameList();
        
        ListView noteDisplay = (ListView) findViewById(R.id.note_list_view);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_change_note_view, menu);
        return true;
    }

// TODO: This is disabled for now until I can get the ABS lib working. 
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
