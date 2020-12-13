package com.example.babajidemustapha.survey.shared.utils

import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

/**
 * Created by Babajide Mustapha on 10/19/2017.
 */
/**
 * This class produces Sha1 hashes based on a given String
 * @author lupin
 */
object Sha1 {
    fun getHash(str: String): String {
        var digest: MessageDigest? = null
        var input: ByteArray? = null
        try {
            digest = MessageDigest.getInstance("SHA-1")
            digest.reset()
            input = digest.digest(str.toByteArray(Charset.forName("UTF-8")))
        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        }
        return convertToHex(input)
    }

    fun getHash(data: ByteArray?): String {
        var digest: MessageDigest? = null
        var input: ByteArray? = null
        try {
            digest = MessageDigest.getInstance("SHA-1")
            digest.reset()
            input = digest.digest(data)
        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        }
        return convertToHex(input)
    }

    private fun convertToHex(data: ByteArray?): String {
        val buf = StringBuffer()
        for (i in data!!.indices) {
            var halfbyte: Int = data[i] ushr 4 and 0x0F
            var two_halfs = 0
            do {
                if (0 <= halfbyte && halfbyte <= 9) buf.append(('0'.toInt() + halfbyte).toChar()) else buf.append(('a'.toInt() + (halfbyte - 10)).toChar())
                halfbyte = data[i] and 0x0F
            } while (two_halfs++ < 1)
        }
        return buf.toString()
    }
}