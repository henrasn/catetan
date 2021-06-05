package com.hsn.catetan.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM Note ORDER BY id ")
    fun fetchAllNotes(): LiveData<List<NoteEntity>>

    @Insert(entity = NoteEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update(entity = NoteEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: NoteEntity)

    @Delete(entity = NoteEntity::class)
    suspend fun deleteNote(note: NoteEntity)
}