package com.hsn.catetan

import android.app.Application
import com.hsn.catetan.data.db.NoteDatabase

class App : Application() {

    companion object {
        lateinit var db: NoteDatabase
    }

    override fun onCreate() {
        super.onCreate()
        db = NoteDatabase.getDbClient(this)
    }
}