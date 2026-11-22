package id.navigo.app.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class TemplateDao_Impl(
  __db: RoomDatabase,
) : TemplateDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDocTemplate: EntityInsertAdapter<DocTemplate>

  private val __deleteAdapterOfDocTemplate: EntityDeleteOrUpdateAdapter<DocTemplate>

  private val __updateAdapterOfDocTemplate: EntityDeleteOrUpdateAdapter<DocTemplate>
  init {
    this.__db = __db
    this.__insertAdapterOfDocTemplate = object : EntityInsertAdapter<DocTemplate>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `templates` (`id`,`name`,`description`,`body`,`fields`,`version`,`active`,`usedCount`,`remoteId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DocTemplate) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.description)
        statement.bindText(4, entity.body)
        statement.bindText(5, entity.fields)
        statement.bindLong(6, entity.version.toLong())
        val _tmp: Int = if (entity.active) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.usedCount.toLong())
        statement.bindText(9, entity.remoteId)
      }
    }
    this.__deleteAdapterOfDocTemplate = object : EntityDeleteOrUpdateAdapter<DocTemplate>() {
      protected override fun createQuery(): String = "DELETE FROM `templates` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DocTemplate) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfDocTemplate = object : EntityDeleteOrUpdateAdapter<DocTemplate>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `templates` SET `id` = ?,`name` = ?,`description` = ?,`body` = ?,`fields` = ?,`version` = ?,`active` = ?,`usedCount` = ?,`remoteId` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DocTemplate) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.description)
        statement.bindText(4, entity.body)
        statement.bindText(5, entity.fields)
        statement.bindLong(6, entity.version.toLong())
        val _tmp: Int = if (entity.active) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.usedCount.toLong())
        statement.bindText(9, entity.remoteId)
        statement.bindLong(10, entity.id)
      }
    }
  }

  public override suspend fun insert(t: DocTemplate): Long = performSuspending(__db, false, true) {
      _connection ->
    val _result: Long = __insertAdapterOfDocTemplate.insertAndReturnId(_connection, t)
    _result
  }

  public override suspend fun insertAll(t: List<DocTemplate>): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfDocTemplate.insert(_connection, t)
  }

  public override suspend fun delete(t: DocTemplate): Unit = performSuspending(__db, false, true) {
      _connection ->
    __deleteAdapterOfDocTemplate.handle(_connection, t)
  }

  public override suspend fun update(t: DocTemplate): Unit = performSuspending(__db, false, true) {
      _connection ->
    __updateAdapterOfDocTemplate.handle(_connection, t)
  }

  public override fun all(): Flow<List<DocTemplate>> {
    val _sql: String = "SELECT * FROM templates ORDER BY id"
    return createFlow(__db, false, arrayOf("templates")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfFields: Int = getColumnIndexOrThrow(_stmt, "fields")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfUsedCount: Int = getColumnIndexOrThrow(_stmt, "usedCount")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remoteId")
        val _result: MutableList<DocTemplate> = mutableListOf()
        while (_stmt.step()) {
          val _item: DocTemplate
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpFields: String
          _tmpFields = _stmt.getText(_columnIndexOfFields)
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpUsedCount: Int
          _tmpUsedCount = _stmt.getLong(_columnIndexOfUsedCount).toInt()
          val _tmpRemoteId: String
          _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          _item =
              DocTemplate(_tmpId,_tmpName,_tmpDescription,_tmpBody,_tmpFields,_tmpVersion,_tmpActive,_tmpUsedCount,_tmpRemoteId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun `get`(id: Long): DocTemplate? {
    val _sql: String = "SELECT * FROM templates WHERE id=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfFields: Int = getColumnIndexOrThrow(_stmt, "fields")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfUsedCount: Int = getColumnIndexOrThrow(_stmt, "usedCount")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remoteId")
        val _result: DocTemplate?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpFields: String
          _tmpFields = _stmt.getText(_columnIndexOfFields)
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpUsedCount: Int
          _tmpUsedCount = _stmt.getLong(_columnIndexOfUsedCount).toInt()
          val _tmpRemoteId: String
          _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          _result =
              DocTemplate(_tmpId,_tmpName,_tmpDescription,_tmpBody,_tmpFields,_tmpVersion,_tmpActive,_tmpUsedCount,_tmpRemoteId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM templates"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun byRemote(r: String): DocTemplate? {
    val _sql: String = "SELECT * FROM templates WHERE remoteId=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, r)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfFields: Int = getColumnIndexOrThrow(_stmt, "fields")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfUsedCount: Int = getColumnIndexOrThrow(_stmt, "usedCount")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remoteId")
        val _result: DocTemplate?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpFields: String
          _tmpFields = _stmt.getText(_columnIndexOfFields)
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpUsedCount: Int
          _tmpUsedCount = _stmt.getLong(_columnIndexOfUsedCount).toInt()
          val _tmpRemoteId: String
          _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          _result =
              DocTemplate(_tmpId,_tmpName,_tmpDescription,_tmpBody,_tmpFields,_tmpVersion,_tmpActive,_tmpUsedCount,_tmpRemoteId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun list(): List<DocTemplate> {
    val _sql: String = "SELECT * FROM templates"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfFields: Int = getColumnIndexOrThrow(_stmt, "fields")
        val _columnIndexOfVersion: Int = getColumnIndexOrThrow(_stmt, "version")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfUsedCount: Int = getColumnIndexOrThrow(_stmt, "usedCount")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remoteId")
        val _result: MutableList<DocTemplate> = mutableListOf()
        while (_stmt.step()) {
          val _item: DocTemplate
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpFields: String
          _tmpFields = _stmt.getText(_columnIndexOfFields)
          val _tmpVersion: Int
          _tmpVersion = _stmt.getLong(_columnIndexOfVersion).toInt()
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpUsedCount: Int
          _tmpUsedCount = _stmt.getLong(_columnIndexOfUsedCount).toInt()
          val _tmpRemoteId: String
          _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          _item =
              DocTemplate(_tmpId,_tmpName,_tmpDescription,_tmpBody,_tmpFields,_tmpVersion,_tmpActive,_tmpUsedCount,_tmpRemoteId)
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
