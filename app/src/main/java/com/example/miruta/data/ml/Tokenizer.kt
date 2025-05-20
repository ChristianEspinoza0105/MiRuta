package com.example.miruta.data.ml

import org.json.JSONObject

class Tokenizer(
    private val wordIndex: Map<String, Int>,
    private val oovToken: Int = 1
) {
    fun textsToSequences(text: String): List<Int> {
        val tokens = text.lowercase().split(" ", "\n", "\t", ".", ",", "!", "?", ":", ";", "(", ")", "\"", "'")
            .filter { it.isNotBlank() }

        return tokens.map { word -> wordIndex[word] ?: oovToken }
    }

    companion object {
        fun fromJson(json: String): Tokenizer {
            val jsonObject = JSONObject(json)
            val config = jsonObject.getJSONObject("config")

            val wordIndexStr = config.getString("word_index")
            val wordIndexJson = JSONObject(wordIndexStr)

            val oovTokenStr = config.optString("oov_token", "<OOV>")
            val index = mutableMapOf<String, Int>()

            val keys = wordIndexJson.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = wordIndexJson.getInt(key)
                index[key] = value
            }

            val oovIndex = index[oovTokenStr] ?: 1
            return Tokenizer(index, oovIndex)
        }
    }
}