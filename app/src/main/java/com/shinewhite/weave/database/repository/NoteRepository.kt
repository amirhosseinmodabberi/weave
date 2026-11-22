package com.shinewhite.weave.database.repository

import com.shinewhite.weave.database.dao.NoteDao
import com.shinewhite.weave.database.entity.NoteEntity
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    suspend fun getNotesForDay(epochDay: Long): Result<List<NoteEntity>> =
        runCatching { noteDao.getNotesForDay(epochDay) }

    suspend fun addNote(epochDay: Long, text: String): Result<Long> =
        runCatching { noteDao.insertNote(NoteEntity(epochDay = epochDay, text = text)) }

    suspend fun updateNote(note: NoteEntity): Result<Unit> =
        runCatching { noteDao.updateNote(note) }

    suspend fun deleteNote(note: NoteEntity): Result<Unit> =
        runCatching { noteDao.deleteNote(note) }
}