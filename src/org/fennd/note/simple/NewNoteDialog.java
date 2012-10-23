package org.fennd.note.simple;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class NewNoteDialog extends DialogFragment {

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface NewNoteDialogListener {
		public void onNewNoteDialogPositiveClick(DialogFragment dialog);
	}

	static NewNoteDialogListener mListener;

	/*
	 * Call this to instantiate a new NewNoteDialog.
	 * 
	 * @param activity The activity hosting the dialog, which must implement the
	 * NoticeDialogListener to receive event callbacks.
	 * 
	 * @returns A new instance of NewNoteDialog.
	 * 
	 * @throws ClassCastException if the host activity does not implement
	 * NoticeDialogListener.
	 */
	public static NewNoteDialog newInstance(Activity activity) {
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events with
			// it.
			mListener = (NewNoteDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception.
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
		NewNoteDialog frag = new NewNoteDialog();
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setPositiveButton("Create",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						// See "onNewNoteDialogPositiveClick" method in NoteView
						// activity.
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								// TODO: Just close dialog?

							}
						}).setMessage(R.layout.new_note_dialog);

		return builder.create();
	}
}
