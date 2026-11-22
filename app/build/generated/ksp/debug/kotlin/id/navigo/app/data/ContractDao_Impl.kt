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
public class ContractDao_Impl(
  __db: RoomDatabase,
) : ContractDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfContract: EntityInsertAdapter<Contract>

  private val __deleteAdapterOfContract: EntityDeleteOrUpdateAdapter<Contract>

  private val __updateAdapterOfContract: EntityDeleteOrUpdateAdapter<Contract>
  init {
    this.__db = __db
    this.__insertAdapterOfContract = object : EntityInsertAdapter<Contract>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `contracts` (`id`,`title`,`party`,`startDay`,`endDay`,`reminders`,`notified`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Contract) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.party)
        statement.bindLong(4, entity.startDay)
        statement.bindLong(5, entity.endDay)
        statement.bindText(6, entity.reminders)
        statement.bindText(7, entity.notified)
      }
    }
    this.__deleteAdapterOfContract = object : EntityDeleteOrUpdateAdapter<Contract>() {
      protected override fun createQuery(): String = "DELETE FROM `contracts` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Contract) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfContract = object : EntityDeleteOrUpdateAdapter<Contract>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `contracts` SET `id` = ?,`title` = ?,`party` = ?,`startDay` = ?,`endDay` = ?,`reminders` = ?,`notified` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Contract) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.party)
        statement.bindLong(4, entity.startDay)
        statement.bindLong(5, entity.endDay)
        statement.bindText(6, entity.reminders)
        statement.bindText(7, entity.notified)
        statement.bindLong(8, entity.id)
      }
    }
  }

  public override suspend fun insert(c: Contract): Long = performSuspending(__db, false, true) {
      _connection ->
    val _result: Long = __insertAdapterOfContract.insertAndReturnId(_connection, c)
    _result
  }

  public override suspend fun delete(c: Contract): Unit = performSuspending(__db, false, true) {
      _connection ->
    __deleteAdapterOfContract.handle(_connection, c)
  }

  public override suspend fun update(c: Contract): Unit = performSuspending(__db, false, true) {
      _connection ->
    __updateAdapterOfContract.handle(_connection, c)
  }

  public override fun all(): Flow<List<Contract>> {
    val _sql: String = "SELECT * FROM contracts ORDER BY endDay ASC"
    return createFlow(__db, false, arrayOf("contracts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfParty: Int = getColumnIndexOrThrow(_stmt, "party")
        val _columnIndexOfStartDay: Int = getColumnIndexOrThrow(_stmt, "startDay")
        val _columnIndexOfEndDay: Int = getColumnIndexOrThrow(_stmt, "endDay")
        val _columnIndexOfReminders: Int = getColumnIndexOrThrow(_stmt, "reminders")
        val _columnIndexOfNotified: Int = getColumnIndexOrThrow(_stmt, "notified")
        val _result: MutableList<Contract> = mutableListOf()
        while (_stmt.step()) {
          val _item: Contract
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpParty: String
          _tmpParty = _stmt.getText(_columnIndexOfParty)
          val _tmpStartDay: Long
          _tmpStartDay = _stmt.getLong(_columnIndexOfStartDay)
          val _tmpEndDay: Long
          _tmpEndDay = _stmt.getLong(_columnIndexOfEndDay)
          val _tmpReminders: String
          _tmpReminders = _stmt.getText(_columnIndexOfReminders)
          val _tmpNotified: String
          _tmpNotified = _stmt.getText(_columnIndexOfNotified)
          _item =
              Contract(_tmpId,_tmpTitle,_tmpParty,_tmpStartDay,_tmpEndDay,_tmpReminders,_tmpNotified)
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
