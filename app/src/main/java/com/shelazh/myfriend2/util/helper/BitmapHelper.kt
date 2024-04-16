package com.shelazh.myfriend2.util.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Random

object BitmapHelper {
    fun encodeToBase64(image: Bitmap): String {
        var baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var b = baos.toByteArray()
        var temp: String? = null
        try {
            System.gc()
            temp = Base64.encodeToString(b, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            b = baos.toByteArray()
            temp = Base64.encodeToString(b, Base64.DEFAULT)
        }
        return temp ?: "null"
    }

    fun decodeBase64(context: Context, input: String): Bitmap? {
        val decodedByte = Base64.decode(input, 0)

        val sdCardDirectory = File(context.cacheDir, "")

        val rand = Random()

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        val randomNum = rand.nextInt((1000 - 0) + 1) + 0

        val nw = "IMG_$randomNum.txt"
        val image = File(sdCardDirectory, nw)

        // Encode the file as a PNG image.
        var outStream: FileOutputStream? = null
        try {
            outStream = FileOutputStream(image)
            outStream.write(input.toByteArray())
            outStream.flush()
            outStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return try {
            BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun resizeBitmap(bitmap: Bitmap, maxSize: Float): Bitmap {
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()

        if (w > h && w < maxSize) {
            return bitmap
        } else if (w < h && h < maxSize) {
            return bitmap
        }

        var nW = w
        var nH = h

        if (w > maxSize || h > maxSize) {
            if (w > h) {
                nW = maxSize
                nH = h / (w / maxSize)
            } else if (w < h) {
                nH = maxSize
                nW = w / (h / maxSize)
            } else {
                nW = w
                nH = h
            }
        }

        val inW = nW.toInt()
        val inH = nH.toInt()

        return Bitmap.createScaledBitmap(bitmap, inW, inH, false)
    }
}