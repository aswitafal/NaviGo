package id.navigo.app.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ContractDao_Impl implements ContractDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Contract> __insertionAdapterOfContract;

  private final EntityDeletionOrUpdateAdapter<Contract> __deletionAdapterOfContract;

  private final EntityDeletionOrUpdateAdapter<Contract> __updateAdapterOfContract;

  public ContractDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfContract = new EntityInsertionAdapter<Contract>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `contracts` (`id`,`title`,`party`,`startDay`,`endDay`,`reminders`,`notified`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Contract entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getParty());
        statement.bindLong(4, entity.getStartDay());
        statement.bindLong(5, entity.getEndDay());
        statement.bindString(6, entity.getReminders());
        statement.bindString(7, entity.getNotified());
      }
    };
    this.__deletionAdapterOfContract = new EntityDeletionOrUpdateAdapter<Contract>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `contracts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Contract entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfContract = new EntityDeletionOrUpdateAdapter<Contract>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `contracts` SET `id` = ?,`title` = ?,`party` = ?,`startDay` = ?,`endDay` = ?,`reminders` = ?,`notified` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Contract entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getParty());
        statement.bindLong(4, entity.getStartDay());
        statement.bindLong(5, entity.getEndDay());
        statement.bindString(6, entity.getReminders());
        statement.bindString(7, entity.getNotified());
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Contract c, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfContract.insertAndReturnId(c);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Contract c, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfContract.handle(c);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Contract c, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfContract.handle(c);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Contract>> all() {
    final String _sql = "SELECT * FROM contracts ORDER BY endDay ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contracts"}, new Callable<List<Contract>>() {
      @Override
      @NonNull
      public List<Contract> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfParty = CursorUtil.getColumnIndexOrThrow(_cursor, "party");
          final int _cursorIndexOfStartDay = CursorUtil.getColumnIndexOrThrow(_cursor, "startDay");
          final int _cursorIndexOfEndDay = CursorUtil.getColumnIndexOrThrow(_cursor, "endDay");
          final int _cursorIndexOfReminders = CursorUtil.getColumnIndexOrThrow(_cursor, "reminders");
          final int _cursorIndexOfNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "notified");
          final List<Contract> _result = new ArrayList<Contract>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Contract _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpParty;
            _tmpParty = _cursor.getString(_cursorIndexOfParty);
            final long _tmpStartDay;
            _tmpStartDay = _cursor.getLong(_cursorIndexOfStartDay);
            final long _tmpEndDay;
            _tmpEndDay = _cursor.getLong(_cursorIndexOfEndDay);
            final String _tmpReminders;
            _tmpReminders = _cursor.getString(_cursorIndexOfReminders);
            final String _tmpNotified;
            _tmpNotified = _cursor.getString(_cursorIndexOfNotified);
            _item = new Contract(_tmpId,_tmpTitle,_tmpParty,_tmpStartDay,_tmpEndDay,_tmpReminders,_tmpNotified);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
