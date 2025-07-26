package djabari.dev.workquotes.data.db.room.typeconverter

import androidx.room.TypeConverter

class StringListTypeConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromList(value: List<String>): String {
        return value.joinToString(",")
    }
}