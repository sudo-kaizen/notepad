package com.sudokaizen.notepad.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.sudokaizen.notepad.database.AppRepository;
import com.sudokaizen.notepad.database.NoteEntry;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private List<NoteEntry> notes;

    private AppRepository mAppRepo;



    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mAppRepo = AppRepository.getInstance(application.getApplicationContext());
    }

    public void insertNote(NoteEntry note){
        mAppRepo.insertNote(note);
    }
}