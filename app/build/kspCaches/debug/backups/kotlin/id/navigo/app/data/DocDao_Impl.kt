package id.navigo.app.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class DocDao_Impl(
  __db: RoomDatabase,
) : DocDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfGeneratedDoc: EntityInsertAdapter<GeneratedDoc>

  private val __deleteAdapterOfGeneratedDoc: EntityDeleteOrUpdateAdapter<GeneratedDoc>
  init {
    this.__db = __db
    this.__insertAdapterOfGeneratedDoc = object : EntityInsertAdapter<GeneratedDoc>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `documents` (`id`,`fileName`,`templateName`,`path`,`sizeKb`,`pages`,`createdAt`,`partyName`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: GeneratedDoc) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.fileName)
        statement.bindText(3, entity.templateName)
        statement.bindText(4, entity.path)
        statement.bindLong(5, entity.sizeKb.toLong())
        statement.bindLong(6, entity.pages.toLong())
        statement.bindLong(7, entity.createdAt)
        statement.bindText(8, entity.partyName)
      }
    }
    this.__deleteAdapterOfGeneratedDoc = object : EntityDeleteOrUpdateAdapter<GeneratedDoc>() {
      protected override fun createQuery(): String = "DELETE FROM `documents` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: GeneratedDoc) {
        statement.bindLong(1, entity.id)
      }
    }
  }

  public override suspend fun insert(d: GeneratedDoc): Long = performSuspending(__db, false, true) {
      _connection ->
    val _result: Long = __insertAdapterOfGeneratedDoc.insertAndReturnId(_connection, d)
    _result
  }

  public override suspend fun delete(d: GeneratedDoc): Unit = performSuspending(__db, false, true) {
      _connection ->
    __deleteAdapterOfGeneratedDoc.handle(_connection, d)
  }

  public override fun all(): Flow<List<GeneratedDoc>> {
    val _sql: String = "SELECT * FROM documents ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("documents")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfFileName: Int = getColumnIndexOrThrow(_stmt, "fileName")
        val _columnIndexOfTemplateName: Int = getColumnIndexOrThrow(_stmt, "templateName")
        val _columnIndexOfPath: Int = getColumnIndexOrThrow(_stmt, "path")
        val _columnIndexOfSizeKb: Int = getColumnIndexOrThrow(_stmt, "sizeKb")
        val _columnIndexOfPages: Int = getColumnIndexOrThrow(_stmt, "pages")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfPartyName: Int = getColumnIndexOrThrow(_stmt, "partyName")
        val _result: MutableList<GeneratedDoc> = mutableListOf()
        while (_stmt.step()) {
          val _item: GeneratedDoc
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpFileName: String
          _tmpFileName = _stmt.getText(_columnIndexOfFileName)
          val _tmpTemplateName: String
          _tmpTemplateName = _stmt.getText(_columnIndexOfTemplateName)
          val _tmpPath: String
          _tmpPath = _stmt.getText(_columnIndexOfPath)
          val _tmpSizeKb: Int
          _tmpSizeKb = _stmt.getLong(_columnIndexOfSizeKb).toInt()
          val _tmpPages: Int
          _tmpPages = _stmt.getLong(_columnIndexOfPages).toInt()
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpPartyName: String
          _tmpPartyName = _stmt.getText(_columnIndexOfPartyName)
          _item =
              GeneratedDoc(_tmpId,_tmpFileName,_tmpTemplateName,_tmpPath,_tmpSizeKb,_tmpPages,_tmpCreatedAt,_tmpPartyName)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
