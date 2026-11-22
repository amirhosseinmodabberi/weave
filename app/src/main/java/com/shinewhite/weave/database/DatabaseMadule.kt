package com.shinewhite.weave.database

import android.content.Context
import androidx.room.Room
import com.shinewhite.weave.database.dao.NoteDao
import com.shinewhite.weave.database.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeaveDatabase =
        Room.databaseBuilder(context, WeaveDatabase::class.java, "weave.db").build()

    @Provides
    fun provideTaskDao(database: WeaveDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideNoteDao(database: WeaveDatabase): NoteDao = database.noteDao()
}