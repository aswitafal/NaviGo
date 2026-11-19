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
public final class DocDao_Impl implements DocDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GeneratedDoc> __insertionAdapterOfGeneratedDoc;

  private final EntityDeletionOrUpdateAdapter<GeneratedDoc> __deletionAdapterOfGeneratedDoc;

  public DocDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGeneratedDoc = new EntityInsertionAdapter<GeneratedDoc>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `documents` (`id`,`fileName`,`templateName`,`path`,`sizeKb`,`pages`,`createdAt`,`partyName`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GeneratedDoc entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getFileName());
        statement.bindString(3, entity.getTemplateName());
        statement.bindString(4, entity.getPath());
        statement.bindLong(5, entity.getSizeKb());
        statement.bindLong(6, entity.getPages());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindString(8, entity.getPartyName());
      }
    };
    this.__deletionAdapterOfGeneratedDoc = new EntityDeletionOrUpdateAdapter<GeneratedDoc>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `documents` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GeneratedDoc entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final GeneratedDoc d, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGeneratedDoc.insertAndReturnId(d);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final GeneratedDoc d, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfGeneratedDoc.handle(d);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<GeneratedDoc>> all() {
    final String _sql = "SELECT * FROM documents ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"documents"}, new Callable<List<GeneratedDoc>>() {
      @Override
      @NonNull
      public List<GeneratedDoc> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfTemplateName = CursorUtil.getColumnIndexOrThrow(_cursor, "templateName");
          final int _cursorIndexOfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "path");
          final int _cursorIndexOfSizeKb = CursorUtil.getColumnIndexOrThrow(_cursor, "sizeKb");
          final int _cursorIndexOfPages = CursorUtil.getColumnIndexOrThrow(_cursor, "pages");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfPartyName = CursorUtil.getColumnIndexOrThrow(_cursor, "partyName");
          final List<GeneratedDoc> _result = new ArrayList<GeneratedDoc>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GeneratedDoc _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final String _tmpTemplateName;
            _tmpTemplateName = _cursor.getString(_cursorIndexOfTemplateName);
            final String _tmpPath;
            _tmpPath = _cursor.getString(_cursorIndexOfPath);
            final int _tmpSizeKb;
            _tmpSizeKb = _cursor.getInt(_cursorIndexOfSizeKb);
            final int _tmpPages;
            _tmpPages = _cursor.getInt(_cursorIndexOfPages);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpPartyName;
            _tmpPartyName = _cursor.getString(_cursorIndexOfPartyName);
            _item = new GeneratedDoc(_tmpId,_tmpFileName,_tmpTemplateName,_tmpPath,_tmpSizeKb,_tmpPages,_tmpCreatedAt,_tmpPartyName);
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
