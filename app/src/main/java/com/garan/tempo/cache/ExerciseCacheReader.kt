package com.garan.tempo.cache

import android.content.Context
import com.garan.counterpart.ExerciseCacheRecord
import java.io.File

class ExerciseCacheReader(val context: Context, val cacheFileId: String) {
    private val tempFileName = CACHE_FILE_TEMPLATE.format(cacheFileId)
    private val tempFile = File("${context.cacheDir.absolutePath}/$tempFileName")

    fun cacheFileExists() = tempFile.exists()

    fun getRecords(): Sequence<ExerciseCacheRecord> {
        if (!tempFile.exists()) {
            return sequenceOf()
        }
        val inputStream = tempFile.inputStream()
        val record = ExerciseCacheRecord.parseDelimitedFrom(inputStream)
        record?.let {
            return generateSequence(it) { ExerciseCacheRecord.parseDelimitedFrom(inputStream) }
        }
        return sequenceOf()
    }
}