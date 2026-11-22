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
public class MouDao_Impl(
  __db: RoomDatabase,
) : MouDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMouAnalysis: EntityInsertAdapter<MouAnalysis>

  private val __deleteAdapterOfMouAnalysis: EntityDeleteOrUpdateAdapter<MouAnalysis>
  init {
    this.__db = __db
    this.__insertAdapterOfMouAnalysis = object : EntityInsertAdapter<MouAnalysis>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `mou_analyses` (`id`,`title`,`createdAt`,`source`,`score`,`level`,`summary`,`risksJson`,`limited`,`endDay`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MouAnalysis) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindLong(3, entity.createdAt)
        statement.bindText(4, entity.source)
        statement.bindLong(5, entity.score.toLong())
        statement.bindText(6, entity.level)
        statement.bindText(7, entity.summary)
        statement.bindText(8, entity.risksJson)
        val _tmp: Int = if (entity.limited) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        val _tmpEndDay: Long? = entity.endDay
        if (_tmpEndDay == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpEndDay)
        }
      }
    }
    this.__deleteAdapterOfMouAnalysis = object : EntityDeleteOrUpdateAdapter<MouAnalysis>() {
      protected override fun createQuery(): String = "DELETE FROM `mou_analyses` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MouAnalysis) {
        statement.bindLong(1, entity.id)
      }
    }
  }

  public override suspend fun insert(a: MouAnalysis): Long = performSuspending(__db, false, true) {
      _connection ->
    val _result: Long = __insertAdapterOfMouAnalysis.insertAndReturnId(_connection, a)
    _result
  }

  public override suspend fun delete(a: MouAnalysis): Unit = performSuspending(__db, false, true) {
      _connection ->
    __deleteAdapterOfMouAnalysis.handle(_connection, a)
  }

  public override fun all(): Flow<List<MouAnalysis>> {
    val _sql: String = "SELECT * FROM mou_analyses ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("mou_analyses")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _columnIndexOfScore: Int = getColumnIndexOrThrow(_stmt, "score")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfSummary: Int = getColumnIndexOrThrow(_stmt, "summary")
        val _columnIndexOfRisksJson: Int = getColumnIndexOrThrow(_stmt, "risksJson")
        val _columnIndexOfLimited: Int = getColumnIndexOrThrow(_stmt, "limited")
        val _columnIndexOfEndDay: Int = getColumnIndexOrThrow(_stmt, "endDay")
        val _result: MutableList<MouAnalysis> = mutableListOf()
        while (_stmt.step()) {
          val _item: MouAnalysis
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpSource: String
          _tmpSource = _stmt.getText(_columnIndexOfSource)
          val _tmpScore: Int
          _tmpScore = _stmt.getLong(_columnIndexOfScore).toInt()
          val _tmpLevel: String
          _tmpLevel = _stmt.getText(_columnIndexOfLevel)
          val _tmpSummary: String
          _tmpSummary = _stmt.getText(_columnIndexOfSummary)
          val _tmpRisksJson: String
          _tmpRisksJson = _stmt.getText(_columnIndexOfRisksJson)
          val _tmpLimited: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfLimited).toInt()
          _tmpLimited = _tmp != 0
          val _tmpEndDay: Long?
          if (_stmt.isNull(_columnIndexOfEndDay)) {
            _tmpEndDay = null
          } else {
            _tmpEndDay = _stmt.getLong(_columnIndexOfEndDay)
          }
          _item =
              MouAnalysis(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpSource,_tmpScore,_tmpLevel,_tmpSummary,_tmpRisksJson,_tmpLimited,_tmpEndDay)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countSince(since: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM mou_analyses WHERE createdAt>=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
