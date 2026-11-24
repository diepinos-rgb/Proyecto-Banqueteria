package com.example.appbanqueteria.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appbanqueteria.data.local.dao.ReservacionDao
import com.example.appbanqueteria.data.local.dao.UsuarioDao
import com.example.appbanqueteria.data.local.entity.ReservacionEntity
import com.example.appbanqueteria.data.local.entity.UsuarioEntity

@Database(
    entities = [UsuarioEntity::class, ReservacionEntity::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun reservacionDao(): ReservacionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private fun insertarAdminPorDefecto(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                INSERT OR IGNORE INTO users (id, name, email, password, numberPhone, rol)
                VALUES (1, 'Administrador', 'administrador@gmail.com', '654321', '+56900000000', 'ADMIN')
                """.trimIndent()
            )
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myapp_database.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            insertarAdminPorDefecto(db)
                        }

                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            super.onDestructiveMigration(db)
                            insertarAdminPorDefecto(db)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}