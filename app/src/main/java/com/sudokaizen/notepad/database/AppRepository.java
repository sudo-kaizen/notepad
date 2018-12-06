package com.sudokaizen.notepad.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.sudokaizen.notepad.AppExecutors;

import java.util.List;

public class AppRepository {

    private static AppRepository instance;
    private NoteDatabase noteDb;
    private List<NoteEntry> notes;
    private AppExecutors mAppExecutors;

    public static AppRepository getInstance(Context context) {
        if (instance ==null){
            instance = new AppRepository(context);
        }

        return instance;
    }

    private AppRepository(Context context) {
        noteDb = NoteDatabase.getInstance(context);
        mAppExecutors = new AppExecutors();
    }

    public List<NoteEntry> getAllNotes() {
        return noteDb.noteDao().getAllNotes();
    }

    public void insertNote(final NoteEntry note){
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                noteDb.noteDao().insertNote(note);
                System.out.println("NoteAdded");
            }
        });
    }

}
