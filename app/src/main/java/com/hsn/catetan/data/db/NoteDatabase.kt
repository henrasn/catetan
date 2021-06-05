package com.hsn.catetan.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var Instance: NoteDatabase? = null

        fun getDbClient(context: Context): NoteDatabase {
            return Instance ?: synchronized(this) {
                Instance ?: Room.databaseBuilder(context, NoteDatabase::class.java, "NOTE_DB")
                    .build().also { Instance = it }
            }
        }
    }
}