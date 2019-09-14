package com.matsuda.chichibu.common

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object FileUtils {
    private const val TEMP_FILE_NAME = "temp_upload_image"

    fun createUploadFile(context: Context, bitmap: Bitmap): File {
        val file = File(File(context.externalCacheDir?.toURI()),
            TEMP_FILE_NAME
        )
        var fos: FileOutputStream? = null
        try {
            file.createNewFile()
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: IOException) {
            e.printStackTrace()
            // TODO handle error
        } finally {
            try {
                fos?.run {
                    flush()
                    close()
                }

            } catch (e: IOException) {
                e.printStackTrace()
                // TODO handle error
            }
        }
        return file
    }
}