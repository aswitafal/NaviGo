package id.navigo.app.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _lawDao: Lazy<LawDao> = lazy {
    LawDao_Impl(this)
  }

  private val _mouDao: Lazy<MouDao> = lazy {
    MouDao_Impl(this)
  }

  private val _chatDao: Lazy<ChatDao> = lazy {
    ChatDao_Impl(this)
  }

  private val _docDao: Lazy<DocDao> = lazy {
    DocDao_Impl(this)
  }

  private val _contractDao: Lazy<ContractDao> = lazy {
    ContractDao_Impl(this)
  }

  private val _templateDao: Lazy<TemplateDao> = lazy {
    TemplateDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "bf01df3962d8edb962c128a90a93aae0", "79e78610bb563a96d8ccfdf63e2dc20a") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `laws` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `number` TEXT NOT NULL, `category` TEXT NOT NULL, `topic` TEXT NOT NULL, `description` TEXT NOT NULL, `active` INTEGER NOT NULL, `aiSummary` TEXT, `remoteId` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `articles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `lawId` INTEGER NOT NULL, `number` TEXT NOT NULL, `text` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `mou_analyses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `source` TEXT NOT NULL, `score` INTEGER NOT NULL, `level` TEXT NOT NULL, `summary` TEXT NOT NULL, `risksJson` TEXT NOT NULL, `limited` INTEGER NOT NULL, `endDay` INTEGER)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `chat_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `lastText` TEXT NOT NULL, `msgCount` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `role` TEXT NOT NULL, `text` TEXT NOT NULL, `citation` TEXT, `createdAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `documents` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fileName` TEXT NOT NULL, `templateName` TEXT NOT NULL, `path` TEXT NOT NULL, `sizeKb` INTEGER NOT NULL, `pages` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `partyName` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `contracts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `party` TEXT NOT NULL, `startDay` INTEGER NOT NULL, `endDay` INTEGER NOT NULL, `reminders` TEXT NOT NULL, `notified` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `templates` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `body` TEXT NOT NULL, `fields` TEXT NOT NULL, `version` INTEGER NOT NULL, `active` INTEGER NOT NULL, `usedCount` INTEGER NOT NULL, `remoteId` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'bf01df3962d8edb962c128a90a93aae0')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `laws`")
        connection.execSQL("DROP TABLE IF EXISTS `articles`")
        connection.execSQL("DROP TABLE IF EXISTS `mou_analyses`")
        connection.execSQL("DROP TABLE IF EXISTS `chat_sessions`")
        connection.execSQL("DROP TABLE IF EXISTS `chat_messages`")
        connection.execSQL("DROP TABLE IF EXISTS `documents`")
        connection.execSQL("DROP TABLE IF EXISTS `contracts`")
        connection.execSQL("DROP TABLE IF EXISTS `templates`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsLaws: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLaws.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLaws.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLaws.put("number", TableInfo.Column("number", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLaws.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLaws.put("topic", TableInfo.Column("topic", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLaws.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLaws.put("active", TableInfo.Column("active", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLaws.put("aiSummary", TableInfo.Column("aiSummary", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLaws.put("remoteId", TableInfo.Column("remoteId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLaws: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesLaws: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoLaws: TableInfo = TableInfo("laws", _columnsLaws, _foreignKeysLaws, _indicesLaws)
        val _existingLaws: TableInfo = read(connection, "laws")
        if (!_infoLaws.equals(_existingLaws)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |laws(id.navigo.app.data.Law).
              | Expected:
              |""".trimMargin() + _infoLaws + """
              |
              | Found:
              |""".trimMargin() + _existingLaws)
        }
        val _columnsArticles: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsArticles.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("lawId", TableInfo.Column("lawId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("number", TableInfo.Column("number", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArticles.put("text", TableInfo.Column("text", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysArticles: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesArticles: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoArticles: TableInfo = TableInfo("articles", _columnsArticles, _foreignKeysArticles,
            _indicesArticles)
        val _existingArticles: TableInfo = read(connection, "articles")
        if (!_infoArticles.equals(_existingArticles)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |articles(id.navigo.app.data.Article).
              | Expected:
              |""".trimMargin() + _infoArticles + """
              |
              | Found:
              |""".trimMargin() + _existingArticles)
        }
        val _columnsMouAnalyses: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMouAnalyses.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMouAnalyses.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMouAnalyses.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMouAnalyses.put("source", TableInfo.Column("source", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMouAnalyses.put("score", TableInfo.Column("score", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMouAnalyses.put("level", TableInfo.Column("level", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMouAnalyses.put("summary", TableInfo.Column("summary", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMouAnalyses.put("risksJson", TableInfo.Column("risksJson", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMouAnalyses.put("limited", TableInfo.Column("limited", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMouAnalyses.put("endDay", TableInfo.Column("endDay", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMouAnalyses: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMouAnalyses: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMouAnalyses: TableInfo = TableInfo("mou_analyses", _columnsMouAnalyses,
            _foreignKeysMouAnalyses, _indicesMouAnalyses)
        val _existingMouAnalyses: TableInfo = read(connection, "mou_analyses")
        if (!_infoMouAnalyses.equals(_existingMouAnalyses)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |mou_analyses(id.navigo.app.data.MouAnalysis).
              | Expected:
              |""".trimMargin() + _infoMouAnalyses + """
              |
              | Found:
              |""".trimMargin() + _existingMouAnalyses)
        }
        val _columnsChatSessions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsChatSessions.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatSessions.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatSessions.put("lastText", TableInfo.Column("lastText", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatSessions.put("msgCount", TableInfo.Column("msgCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatSessions.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChatSessions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesChatSessions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoChatSessions: TableInfo = TableInfo("chat_sessions", _columnsChatSessions,
            _foreignKeysChatSessions, _indicesChatSessions)
        val _existingChatSessions: TableInfo = read(connection, "chat_sessions")
        if (!_infoChatSessions.equals(_existingChatSessions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |chat_sessions(id.navigo.app.data.ChatSession).
              | Expected:
              |""".trimMargin() + _infoChatSessions + """
              |
              | Found:
              |""".trimMargin() + _existingChatSessions)
        }
        val _columnsChatMessages: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsChatMessages.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("sessionId", TableInfo.Column("sessionId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("role", TableInfo.Column("role", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("text", TableInfo.Column("text", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("citation", TableInfo.Column("citation", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChatMessages: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesChatMessages: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoChatMessages: TableInfo = TableInfo("chat_messages", _columnsChatMessages,
            _foreignKeysChatMessages, _indicesChatMessages)
        val _existingChatMessages: TableInfo = read(connection, "chat_messages")
        if (!_infoChatMessages.equals(_existingChatMessages)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |chat_messages(id.navigo.app.data.ChatMessage).
              | Expected:
              |""".trimMargin() + _infoChatMessages + """
              |
              | Found:
              |""".trimMargin() + _existingChatMessages)
        }
        val _columnsDocuments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDocuments.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("fileName", TableInfo.Column("fileName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("templateName", TableInfo.Column("templateName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("path", TableInfo.Column("path", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("sizeKb", TableInfo.Column("sizeKb", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("pages", TableInfo.Column("pages", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDocuments.put("partyName", TableInfo.Column("partyName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDocuments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDocuments: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDocuments: TableInfo = TableInfo("documents", _columnsDocuments,
            _foreignKeysDocuments, _indicesDocuments)
        val _existingDocuments: TableInfo = read(connection, "documents")
        if (!_infoDocuments.equals(_existingDocuments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |documents(id.navigo.app.data.GeneratedDoc).
              | Expected:
              |""".trimMargin() + _infoDocuments + """
              |
              | Found:
              |""".trimMargin() + _existingDocuments)
        }
        val _columnsContracts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsContracts.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("party", TableInfo.Column("party", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("startDay", TableInfo.Column("startDay", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("endDay", TableInfo.Column("endDay", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("reminders", TableInfo.Column("reminders", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsContracts.put("notified", TableInfo.Column("notified", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysContracts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesContracts: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoContracts: TableInfo = TableInfo("contracts", _columnsContracts,
            _foreignKeysContracts, _indicesContracts)
        val _existingContracts: TableInfo = read(connection, "contracts")
        if (!_infoContracts.equals(_existingContracts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |contracts(id.navigo.app.data.Contract).
              | Expected:
              |""".trimMargin() + _infoContracts + """
              |
              | Found:
              |""".trimMargin() + _existingContracts)
        }
        val _columnsTemplates: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTemplates.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTemplates.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTemplates.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTemplates.put("body", TableInfo.Column("body", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTemplates.put("fields", TableInfo.Column("fields", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTemplates.put("version", TableInfo.Column("version", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTemplates.put("active", TableInfo.Column("active", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTemplates.put("usedCount", TableInfo.Column("usedCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTemplates.put("remoteId", TableInfo.Column("remoteId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTemplates: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTemplates: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTemplates: TableInfo = TableInfo("templates", _columnsTemplates,
            _foreignKeysTemplates, _indicesTemplates)
        val _existingTemplates: TableInfo = read(connection, "templates")
        if (!_infoTemplates.equals(_existingTemplates)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |templates(id.navigo.app.data.DocTemplate).
              | Expected:
              |""".trimMargin() + _infoTemplates + """
              |
              | Found:
              |""".trimMargin() + _existingTemplates)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "laws", "articles",
        "mou_analyses", "chat_sessions", "chat_messages", "documents", "contracts", "templates")
  }

  public override fun clearAllTables() {
    super.performClear(false, "laws", "articles", "mou_analyses", "chat_sessions", "chat_messages",
        "documents", "contracts", "templates")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(LawDao::class, LawDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MouDao::class, MouDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ChatDao::class, ChatDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DocDao::class, DocDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ContractDao::class, ContractDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TemplateDao::class, TemplateDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun lawDao(): LawDao = _lawDao.value

  public override fun mouDao(): MouDao = _mouDao.value

  public override fun chatDao(): ChatDao = _chatDao.value

  public override fun docDao(): DocDao = _docDao.value

  public override fun contractDao(): ContractDao = _contractDao.value

  public override fun templateDao(): TemplateDao = _templateDao.value
}
