package com.sudokaizen.notepad.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.sudokaizen.notepad.R;
import com.sudokaizen.notepad.database.NoteRepository;
import com.sudokaizen.notepad.database.NoteEntry;
import com.sudokaizen.notepad.database.UserEntity;
import com.sudokaizen.notepad.viewmodel.CreateNoteViewModel;

import static com.sudokaizen.notepad.ui.NotesListActivity.NOTE_ID;

public class CreateNoteActivity extends AppCompatActivity {

    private NoteRepository mNoteRepository;
    private TextInputEditText etNote;
    private boolean isNewNote = true;
    private String editNoteId;
    private UserEntity user;
    private String userId;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        TextInputLayout til = findViewById(R.id.til_create_note);

        til.setBoxStrokeColor(android.R.color.transparent);

        etNote = findViewById(R.id.et_create_note);
        mNoteRepository = NoteRepository.getInstance(CreateNoteActivity.this);
        setupViewModel();
    }

    private void setupViewModel() {
        CreateNoteViewModel createNoteViewModel = ViewModelProviders.of(this)
                .get(CreateNoteViewModel.class);
        createNoteViewModel.getUser();
        createNoteViewModel.mLiveNote.observe(this, new Observer<NoteEntry>() {
            @Override
            public void onChanged(@Nullable NoteEntry noteEntry) {
                if (noteEntry != null) {
                    etNote.setText(noteEntry.getContent());
                    editNoteId = noteEntry.getId();
                }
            }
        });

        createNoteViewModel.mLiveUser.observe(this, new Observer<UserEntity>() {
            @Override
            public void onChanged(@Nullable UserEntity userEntity) {
                user = userEntity;
                if (user != null) {
                    userId = user.getId();
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String noteId = extras.getString(NOTE_ID);
            createNoteViewModel.getNoteById(noteId);
            isNewNote = false;
            getSupportActionBar().setTitle("Edit note");
        }

    }

    private void onCompleteAction() {

        if (TextUtils.isEmpty(etNote.getText())) {
            Toast.makeText(CreateNoteActivity.this,
                    "You didn't add any note", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        NoteEntry noteEntry = new NoteEntry();
        noteEntry.setContent(etNote.getText().toString());
        noteEntry.setTimestamp(System.currentTimeMillis());

        if (isNewNote) {
            saveNote(noteEntry);
        } else {
            noteEntry.setId(editNoteId);
            updateNote(noteEntry);
        }
    }

    private void saveNote(NoteEntry noteEntry) {
        mNoteRepository.insertNote(userId, noteEntry);
        Toast.makeText(CreateNoteActivity.this, "Note saved", Toast.LENGTH_SHORT)
                .show();
    }

    private void updateNote(NoteEntry noteEntry) {
        mNoteRepository.updateNote(userId, noteEntry);
        Toast.makeText(CreateNoteActivity.this, "Note updated", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onPause() {
        onCompleteAction();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
