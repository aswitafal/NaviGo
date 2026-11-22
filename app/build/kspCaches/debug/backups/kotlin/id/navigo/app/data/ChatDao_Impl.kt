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
public class ChatDao_Impl(
  __db: RoomDatabase,
) : ChatDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfChatSession: EntityInsertAdapter<ChatSession>

  private val __insertAdapterOfChatMessage: EntityInsertAdapter<ChatMessage>

  private val __deleteAdapterOfChatSession: EntityDeleteOrUpdateAdapter<ChatSession>

  private val __updateAdapterOfChatSession: EntityDeleteOrUpdateAdapter<ChatSession>
  init {
    this.__db = __db
    this.__insertAdapterOfChatSession = object : EntityInsertAdapter<ChatSession>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `chat_sessions` (`id`,`title`,`lastText`,`msgCount`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ChatSession) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.lastText)
        statement.bindLong(4, entity.msgCount.toLong())
        statement.bindLong(5, entity.updatedAt)
      }
    }
    this.__insertAdapterOfChatMessage = object : EntityInsertAdapter<ChatMessage>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `chat_messages` (`id`,`sessionId`,`role`,`text`,`citation`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ChatMessage) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.sessionId)
        statement.bindText(3, entity.role)
        statement.bindText(4, entity.text)
        val _tmpCitation: String? = entity.citation
        if (_tmpCitation == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpCitation)
        }
        statement.bindLong(6, entity.createdAt)
      }
    }
    this.__deleteAdapterOfChatSession = object : EntityDeleteOrUpdateAdapter<ChatSession>() {
      protected override fun createQuery(): String = "DELETE FROM `chat_sessions` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ChatSession) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfChatSession = object : EntityDeleteOrUpdateAdapter<ChatSession>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `chat_sessions` SET `id` = ?,`title` = ?,`lastText` = ?,`msgCount` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ChatSession) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.lastText)
        statement.bindLong(4, entity.msgCount.toLong())
        statement.bindLong(5, entity.updatedAt)
        statement.bindLong(6, entity.id)
      }
    }
  }

  public override suspend fun insertSession(s: ChatSession): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfChatSession.insertAndReturnId(_connection, s)
    _result
  }

  public override suspend fun insertMessage(m: ChatMessage): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfChatMessage.insertAndReturnId(_connection, m)
    _result
  }

  public override suspend fun deleteSession(s: ChatSession): Unit = performSuspending(__db, false,
      true) { _connection ->
    __deleteAdapterOfChatSession.handle(_connection, s)
  }

  public override suspend fun updateSession(s: ChatSession): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfChatSession.handle(_connection, s)
  }

  public override fun sessions(): Flow<List<ChatSession>> {
    val _sql: String = "SELECT * FROM chat_sessions ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("chat_sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfLastText: Int = getColumnIndexOrThrow(_stmt, "lastText")
        val _columnIndexOfMsgCount: Int = getColumnIndexOrThrow(_stmt, "msgCount")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<ChatSession> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatSession
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpLastText: String
          _tmpLastText = _stmt.getText(_columnIndexOfLastText)
          val _tmpMsgCount: Int
          _tmpMsgCount = _stmt.getLong(_columnIndexOfMsgCount).toInt()
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = ChatSession(_tmpId,_tmpTitle,_tmpLastText,_tmpMsgCount,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun messages(sid: Long): Flow<List<ChatMessage>> {
    val _sql: String = "SELECT * FROM chat_messages WHERE sessionId=? ORDER BY id"
    return createFlow(__db, false, arrayOf("chat_messages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "sessionId")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _columnIndexOfCitation: Int = getColumnIndexOrThrow(_stmt, "citation")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ChatMessage> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatMessage
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSessionId: Long
          _tmpSessionId = _stmt.getLong(_columnIndexOfSessionId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          val _tmpCitation: String?
          if (_stmt.isNull(_columnIndexOfCitation)) {
            _tmpCitation = null
          } else {
            _tmpCitation = _stmt.getText(_columnIndexOfCitation)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = ChatMessage(_tmpId,_tmpSessionId,_tmpRole,_tmpText,_tmpCitation,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun lastMessages(sid: Long, n: Int): List<ChatMessage> {
    val _sql: String = "SELECT * FROM chat_messages WHERE sessionId=? ORDER BY id DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
        _argIndex = 2
        _stmt.bindLong(_argIndex, n.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "sessionId")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _columnIndexOfCitation: Int = getColumnIndexOrThrow(_stmt, "citation")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ChatMessage> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatMessage
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpSessionId: Long
          _tmpSessionId = _stmt.getLong(_columnIndexOfSessionId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          val _tmpCitation: String?
          if (_stmt.isNull(_columnIndexOfCitation)) {
            _tmpCitation = null
          } else {
            _tmpCitation = _stmt.getText(_columnIndexOfCitation)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = ChatMessage(_tmpId,_tmpSessionId,_tmpRole,_tmpText,_tmpCitation,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun session(id: Long): ChatSession? {
    val _sql: String = "SELECT * FROM chat_sessions WHERE id=?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfLastText: Int = getColumnIndexOrThrow(_stmt, "lastText")
        val _columnIndexOfMsgCount: Int = getColumnIndexOrThrow(_stmt, "msgCount")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: ChatSession?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpLastText: String
          _tmpLastText = _stmt.getText(_columnIndexOfLastText)
          val _tmpMsgCount: Int
          _tmpMsgCount = _stmt.getLong(_columnIndexOfMsgCount).toInt()
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = ChatSession(_tmpId,_tmpTitle,_tmpLastText,_tmpMsgCount,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteMessages(sid: Long) {
    val _sql: String = "DELETE FROM chat_messages WHERE sessionId=?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sid)
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
