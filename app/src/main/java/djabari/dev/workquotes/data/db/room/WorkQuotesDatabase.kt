package djabari.dev.workquotes.data.db.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import djabari.dev.workquotes.data.db.room.dao.WorkQuotesDao
import djabari.dev.workquotes.data.db.room.entity.QuoteEntity
import djabari.dev.workquotes.data.db.room.entity.QuoteTagEntity
import djabari.dev.workquotes.data.db.room.typeconverter.StringListTypeConverter
import me.tatarka.inject.annotations.Inject

@TypeConverters(StringListTypeConverter::class)
@Database(
    version = 1,
    entities = [QuoteEntity::class, QuoteTagEntity::class],
    exportSchema = false
)
abstract class WorkQuotesDatabase : RoomDatabase() {
    abstract fun getWorkQuotesDao(): WorkQuotesDao
}

@Inject
class WorkQuotesDatabaseClient(private val context: Context) {
    val database: WorkQuotesDatabase by lazy {
        Room.databaseBuilder(
            context,
            WorkQuotesDatabase::class.java,
            "work_quotes_database.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    companion object {
        val MIGRATION_1_2 = Migration(1,2) { db ->
            db.execSQL(
                """
                    CREATE TABLE quote_tag_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    tag TEXT NOT NULL
                    )
                    """.trimIndent()
            )

            db.execSQL(
                """
                    INSERT INTO quote_tag_new (id, tag)
                    SELECT id, tag FROM quote_tag
                    """.trimIndent()
            )

            db.execSQL("DROP TABLE quote_tag")
            db.execSQL("ALTER TABLE quote_tag_new RENAME TO quote_tag")
        }
    }
}