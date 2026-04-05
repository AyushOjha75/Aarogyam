package com.aarogyam

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aarogyam.data.db.AppDatabase
import com.aarogyam.data.db.WeightLog
import com.aarogyam.data.db.WeightLogDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WeightLogDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: WeightLogDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.weightLogDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insert_and_getAllLogs() = runBlocking {
        val log1 = WeightLog(weightKg = 70.0, notes = "Morning", loggedAt = 1000L)
        val log2 = WeightLog(weightKg = 71.5, notes = null, loggedAt = 2000L)
        dao.insert(log1)
        dao.insert(log2)

        val logs = dao.getAllLogs().first()
        assertEquals(2, logs.size)
        // Ordered DESC by loggedAt
        assertEquals(71.5, logs[0].weightKg, 0.0001)
        assertEquals(70.0, logs[1].weightKg, 0.0001)
    }

    @Test
    fun getLatest_returnsLastInserted() = runBlocking {
        dao.insert(WeightLog(weightKg = 65.0, notes = null, loggedAt = 500L))
        dao.insert(WeightLog(weightKg = 72.0, notes = null, loggedAt = 1500L))

        val latest = dao.getLatest()
        assertNotNull(latest)
        assertEquals(72.0, latest!!.weightKg, 0.0001)
    }

    @Test
    fun getLatest_emptyDb_returnsNull() = runBlocking {
        val latest = dao.getLatest()
        assertNull(latest)
    }

    @Test
    fun getLastN_returnsCorrectCount() = runBlocking {
        repeat(5) { i ->
            dao.insert(WeightLog(weightKg = 70.0 + i, notes = null, loggedAt = i.toLong() * 1000))
        }
        val result = dao.getLastN(3)
        assertEquals(3, result.size)
    }

    @Test
    fun delete_removesLog() = runBlocking {
        val id = dao.insert(WeightLog(weightKg = 80.0, notes = null, loggedAt = 1000L))
        val log = dao.getLatest()!!
        dao.delete(log)

        val logs = dao.getAllLogs().first()
        assertEquals(0, logs.size)
    }

    @Test
    fun insert_persistsNotes() = runBlocking {
        dao.insert(WeightLog(weightKg = 68.0, notes = "After gym", loggedAt = 1000L))
        val latest = dao.getLatest()
        assertEquals("After gym", latest?.notes)
    }

    @Test
    fun insert_nullNotes_allowed() = runBlocking {
        dao.insert(WeightLog(weightKg = 68.0, notes = null, loggedAt = 1000L))
        val latest = dao.getLatest()
        assertNull(latest?.notes)
    }
}
