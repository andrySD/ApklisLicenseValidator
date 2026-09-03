package cu.uci.android.apklis_license_validator.signature_helpers

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive

/**
 * Re-serializes JSON the way CPython [json.dumps] does by default:
 * separators `(", ", ": ")`, insertion-order keys, `ensure_ascii=True`.
 *
 * Apklis 2.0 signs that form, not the compact HTTP body and not a
 * hand-built two-field object.
 */
object PythonJson {
    fun dumps(rawJson: String): String = dumps(JsonParser.parseString(rawJson))

    fun dumps(element: JsonElement): String = when {
        element.isJsonNull -> "null"
        element.isJsonPrimitive -> dumpsPrimitive(element.asJsonPrimitive)
        element.isJsonArray -> dumpsArray(element.asJsonArray)
        element.isJsonObject -> dumpsObject(element.asJsonObject)
        else -> "null"
    }

    private fun dumpsObject(obj: JsonObject): String {
        if (obj.size() == 0) return "{}"
        val builder = StringBuilder("{")
        var first = true
        for (entry in obj.entrySet()) {
            if (!first) builder.append(", ")
            first = false
            builder.append(quote(entry.key)).append(": ").append(dumps(entry.value))
        }
        return builder.append("}").toString()
    }

    private fun dumpsArray(array: JsonArray): String {
        if (array.size() == 0) return "[]"
        val builder = StringBuilder("[")
        var first = true
        for (item in array) {
            if (!first) builder.append(", ")
            first = false
            builder.append(dumps(item))
        }
        return builder.append("]").toString()
    }

    private fun dumpsPrimitive(primitive: JsonPrimitive): String = when {
        primitive.isBoolean -> primitive.asBoolean.toString()
        primitive.isNumber -> primitive.asString
        else -> quote(primitive.asString)
    }

    private fun quote(value: String): String {
        val builder = StringBuilder(value.length + 2).append('"')
        for (ch in value) {
            when (ch) {
                '"' -> builder.append("\\\"")
                '\\' -> builder.append("\\\\")
                '\b' -> builder.append("\\b")
                '\u000c' -> builder.append("\\f")
                '\n' -> builder.append("\\n")
                '\r' -> builder.append("\\r")
                '\t' -> builder.append("\\t")
                else -> if (ch.code < 0x20 || ch.code > 0x7e) {
                    builder.append("\\u").append(ch.code.toString(16).padStart(4, '0'))
                } else {
                    builder.append(ch)
                }
            }
        }
        return builder.append('"').toString()
    }
}
