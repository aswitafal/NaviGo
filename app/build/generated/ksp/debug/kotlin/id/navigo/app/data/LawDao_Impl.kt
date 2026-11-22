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
public class LawDao_Impl(
  __db: RoomDatabase,
) : LawDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfLaw: EntityInsertAdapter<Law>

  private val __insertAdapterOfArticle: EntityInsertAdapter<Article>

  private val __deleteAdapterOfLaw: EntityDeleteOrUpdateAdapter<Law>

  private val __updateAdapterOfLaw: EntityDeleteOrUpdateAdapter<Law>
  init {
    this.__db = __db
    this.__insertAdapterOfLaw = object : EntityInsertAdapter<Law>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `laws` (`id`,`title`,`number`,`category`,`topic`,`description`,`active`,`aiSummary`,`remoteId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Law) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.number)
        statement.bindText(4, entity.category)
        statement.bindText(5, entity.topic)
        statement.bindText(6, entity.description)
        val _tmp: Int = if (entity.active) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpAiSummary: String? = entity.aiSummary
        if (_tmpAiSummary == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpAiSummary)
        }
        statement.bindText(9, entity.remoteId)
      }
    }
    this.__insertAdapterOfArticle = object : EntityInsertAdapter<Article>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `articles` (`id`,`lawId`,`number`,`text`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Article) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.lawId)
        statement.bindText(3, entity.number)
        statement.bindText(4, entity.text)
      }
    }
    this.__deleteAdapterOfLaw = object : EntityDeleteOrUpdateAdapter<Law>() {
      protected override fun createQuery(): String = "DELETE FROM `laws` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Law) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfLaw = object : EntityDeleteOrUpdateAdapter<Law>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `laws` SET `id` = ?,`title` = ?,`number` = ?,`category` = ?,`topic` = ?,`description` = ?,`active` = ?,`aiSummary` = ?,`remoteId` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Law) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.number)
        statement.bindText(4, entity.category)
        statement.bindText(5, entity.topic)
        statement.bindText(6, entity.description)
        val _tmp: Int = if (entity.active) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpAiSummary: String? = entity.aiSummary
        if (_tmpAiSummary == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpAiSummary)
        }
        statement.bindText(9, entity.remoteId)
        statement.bindLong(10, entity.id)
      }
    }
  }

  public override suspend fun insertLaw(l: Law): Long = performSuspending(__db, false, true) {
      _connection ->
    val _result: Long = __insertAdapterOfLaw.insertAndReturnId(_connection, l)
    _result
  }

  public override suspend fun insertArticles(a: List<Article>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfArticle.insert(_connection, a)
  }

  public override suspend fun deleteLaw(l: Law): Unit = performSuspending(__db, false, true) {
      _connection ->
    __deleteAdapterOfLaw.handle(_connection, l)
  }

  public override suspend fun updateLaw(l: Law): Unit = performSuspending(__db, false, true) {
      _connection ->
    __updateAdapterOfLaw.handle(_connection, l)
  }

  public override suspend fun laws(): List<Law> {
    val _sql: String = "SELECT * FROM laws ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTopic: Int = getColumnIndexOrThrow(_stmt, "topic")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfAiSummary: Int = getColumnIndexOrThrow(_stmt, "aiSummary")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remoteId")
        val _result: MutableList<Law> = mutableListOf()
        while (_stmt.step()) {
          val _item: Law
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTopic: String
          _tmpTopic = _stmt.getText(_columnIndexOfTopic)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpAiSummary: String?
          if (_stmt.isNull(_columnIndexOfAiSummary)) {
            _tmpAiSummary = null
          } else {
            _tmpAiSummary = _stmt.getText(_columnIndexOfAiSummary)
          }
          val _tmpRemoteId: String
          _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          _item =
              Law(_tmpId,_tmpTitle,_tmpNumber,_tmpCategory,_tmpTopic,_tmpDescription,_tmpActive,_tmpAiSummary,_tmpRemoteId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun lawsFlow(): Flow<List<Law>> {
    val _sql: String = "SELECT * FROM laws ORDER BY id"
    return createFlow(__db, false, arrayOf("laws")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTopic: Int = getColumnIndexOrThrow(_stmt, "topic")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfAiSummary: Int = getColumnIndexOrThrow(_stmt, "aiSummary")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remoteId")
        val _result: MutableList<Law> = mutableListOf()
        while (_stmt.step()) {
          val _item: Law
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTopic: String
          _tmpTopic = _stmt.getText(_columnIndexOfTopic)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpAiSummary: String?
          if (_stmt.isNull(_columnIndexOfAiSummary)) {
            _tmpAiSummary = null
          } else {
            _tmpAiSummary = _stmt.getText(_columnIndexOfAiSummary)
          }
          val _tmpRemoteId: String
          _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          _item =
              Law(_tmpId,_tmpTitle,_tmpNumber,_tmpCategory,_tmpTopic,_tmpDescription,_tmpActive,_tmpAiSummary,_tmpRemoteId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun allArticles(): List<Article> {
    val _sql: String = "SELECT * FROM articles"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLawId: Int = getColumnIndexOrThrow(_stmt, "lawId")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _result: MutableList<Article> = mutableListOf()
        while (_stmt.step()) {
          val _item: Article
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpLawId: Long
          _tmpLawId = _stmt.getLong(_columnIndexOfLawId)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          _item = Article(_tmpId,_tmpLawId,_tmpNumber,_tmpText)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun articlesOf(id: Long): List<Article> {
    val _sql: String = "SELECT * FROM articles WHERE lawId=? ORDER BY id"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLawId: Int = getColumnIndexOrThrow(_stmt, "lawId")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _result: MutableList<Article> = mutableListOf()
        while (_stmt.step()) {
          val _item: Article
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpLawId: Long
          _tmpLawId = _stmt.getLong(_columnIndexOfLawId)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          _item = Article(_tmpId,_tmpLawId,_tmpNumber,_tmpText)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM laws"
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

  public override suspend fun byRemote(r: String): Law? {
    val _sql: String = "SELECT * FROM laws WHERE remoteId=? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, r)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfTopic: Int = getColumnIndexOrThrow(_stmt, "topic")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfActive: Int = getColumnIndexOrThrow(_stmt, "active")
        val _columnIndexOfAiSummary: Int = getColumnIndexOrThrow(_stmt, "aiSummary")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remoteId")
        val _result: Law?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpTopic: String
          _tmpTopic = _stmt.getText(_columnIndexOfTopic)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfActive).toInt()
          _tmpActive = _tmp != 0
          val _tmpAiSummary: String?
          if (_stmt.isNull(_columnIndexOfAiSummary)) {
            _tmpAiSummary = null
          } else {
            _tmpAiSummary = _stmt.getText(_columnIndexOfAiSummary)
          }
          val _tmpRemoteId: String
          _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          _result =
              Law(_tmpId,_tmpTitle,_tmpNumber,_tmpCategory,_tmpTopic,_tmpDescription,_tmpActive,_tmpAiSummary,_tmpRemoteId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteArticlesOf(id: Long) {
    val _sql: String = "DELETE FROM articles WHERE lawId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
