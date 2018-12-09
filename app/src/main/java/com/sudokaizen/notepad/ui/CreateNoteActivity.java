package com.sudokaizen.notepad.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sudokaizen.notepad.R;
import com.sudokaizen.notepad.database.AppRepository;
import com.sudokaizen.notepad.database.NoteEntry;
import com.sudokaizen.notepad.viewmodel.CreateNoteViewModel;

import static com.sudokaizen.notepad.ui.MainActivity.NOTE_ID;

public class CreateNoteActivity extends AppCompatActivity {

    private AppRepository mAppRepository;
    private TextInputEditText etNote;
    private boolean isNewNote;
    private int editNoteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        TextInputLayout til = findViewById(R.id.til_create_note);

        til.setBoxStrokeColor(android.R.color.transparent);

        etNote = findViewById(R.id.et_create_note);
        mAppRepository = AppRepository.getInstance(CreateNoteActivity.this);
        setupViewModel();
    }

    private void setupViewModel() {
        CreateNoteViewModel createNoteViewModel = ViewModelProviders.of(this)
                .get(CreateNoteViewModel.class);
        createNoteViewModel.mLiveNote.observe(this, new Observer<NoteEntry>() {
            @Override
            public void onChanged(@Nullable NoteEntry noteEntry) {
                if (noteEntry != null) {
                    etNote.setText(noteEntry.getContent());
                    editNoteId = noteEntry.getId();
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int noteId = extras.getInt(NOTE_ID);
            createNoteViewModel.getNoteById(noteId);
            isNewNote = false;
        }

    }

    private void onCompleteAction() {

        if (etNote.getText().length() == 0) {
            Toast.makeText(CreateNoteActivity.this,
                    "You didn't add any note", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        NoteEntry noteEntry = new NoteEntry();
        noteEntry.setContent(etNote.getText().toString());
        noteEntry.setTimestamp(System.currentTimeMillis());

        if (isNewNote) {
            saveNote(noteEntry, "Note saved");
        } else {
            noteEntry.setId(editNoteId);
            saveNote(noteEntry, "Note updated");
        }
    }

    private void saveNote(NoteEntry noteEntry, String toastMsg) {
        mAppRepository.insertNote(noteEntry);
        Toast.makeText(CreateNoteActivity.this, toastMsg, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onPause() {
        onCompleteAction();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
//        onCompleteAction();
        finish();

    }
}
