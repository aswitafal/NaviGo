package id.navigo.app.data;

import android.database.Cursor;
import android.os.CancellationSignal;
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
import java.lang.Integer;
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
public final class MouDao_Impl implements MouDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MouAnalysis> __insertionAdapterOfMouAnalysis;

  private final EntityDeletionOrUpdateAdapter<MouAnalysis> __deletionAdapterOfMouAnalysis;

  public MouDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMouAnalysis = new EntityInsertionAdapter<MouAnalysis>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `mou_analyses` (`id`,`title`,`createdAt`,`source`,`score`,`level`,`summary`,`risksJson`,`limited`,`endDay`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MouAnalysis entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindLong(3, entity.getCreatedAt());
        statement.bindString(4, entity.getSource());
        statement.bindLong(5, entity.getScore());
        statement.bindString(6, entity.getLevel());
        statement.bindString(7, entity.getSummary());
        statement.bindString(8, entity.getRisksJson());
        final int _tmp = entity.getLimited() ? 1 : 0;
        statement.bindLong(9, _tmp);
        if (entity.getEndDay() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getEndDay());
        }
      }
    };
    this.__deletionAdapterOfMouAnalysis = new EntityDeletionOrUpdateAdapter<MouAnalysis>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `mou_analyses` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MouAnalysis entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final MouAnalysis a, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMouAnalysis.insertAndReturnId(a);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MouAnalysis a, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMouAnalysis.handle(a);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MouAnalysis>> all() {
    final String _sql = "SELECT * FROM mou_analyses ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"mou_analyses"}, new Callable<List<MouAnalysis>>() {
      @Override
      @NonNull
      public List<MouAnalysis> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "level");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfRisksJson = CursorUtil.getColumnIndexOrThrow(_cursor, "risksJson");
          final int _cursorIndexOfLimited = CursorUtil.getColumnIndexOrThrow(_cursor, "limited");
          final int _cursorIndexOfEndDay = CursorUtil.getColumnIndexOrThrow(_cursor, "endDay");
          final List<MouAnalysis> _result = new ArrayList<MouAnalysis>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MouAnalysis _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final String _tmpLevel;
            _tmpLevel = _cursor.getString(_cursorIndexOfLevel);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpRisksJson;
            _tmpRisksJson = _cursor.getString(_cursorIndexOfRisksJson);
            final boolean _tmpLimited;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfLimited);
            _tmpLimited = _tmp != 0;
            final Long _tmpEndDay;
            if (_cursor.isNull(_cursorIndexOfEndDay)) {
              _tmpEndDay = null;
            } else {
              _tmpEndDay = _cursor.getLong(_cursorIndexOfEndDay);
            }
            _item = new MouAnalysis(_tmpId,_tmpTitle,_tmpCreatedAt,_tmpSource,_tmpScore,_tmpLevel,_tmpSummary,_tmpRisksJson,_tmpLimited,_tmpEndDay);
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

  @Override
  public Object countSince(final long since, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM mou_analyses WHERE createdAt>=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
