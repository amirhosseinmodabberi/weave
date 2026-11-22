package com.shinewhite.weave.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shinewhite.weave.database.entity.NoteEntity

@Dao
interface NoteDao {
    @Insert
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE epochDay = :epochDay ORDER BY id")
    suspend fun getNotesForDay(epochDay: Long): List<NoteEntity>
}