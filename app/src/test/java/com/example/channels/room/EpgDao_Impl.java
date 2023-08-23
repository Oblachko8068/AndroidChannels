package com.example.channels.room;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;

import com.example.channels.model.room.EpgDao;
import com.example.channels.model.room.EpgDbEntity;

import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class EpgDao_Impl implements EpgDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EpgDbEntity> __insertionAdapterOfEpgDbEntity;

  public EpgDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEpgDbEntity = new EntityInsertionAdapter<EpgDbEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `epgs` (`channel_id`,`id`,`time_start`,`time_stop`,`title`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @Nullable final EpgDbEntity entity) {
        statement.bindLong(1, entity.getChannelID());
        statement.bindLong(2, entity.getId());
        statement.bindLong(3, entity.getTimestart());
        statement.bindLong(4, entity.getTimestop());
        if (entity.getTitle() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getTitle());
        }
      }
    };
  }

  @Override
  public void createEpg(final EpgDbEntity epgDbEntity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfEpgDbEntity.insert(epgDbEntity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<EpgDbEntity> getEpgListAll() {
    final String _sql = "SELECT * FROM epgs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfChannelID = CursorUtil.getColumnIndexOrThrow(_cursor, "channel_id");
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTimestart = CursorUtil.getColumnIndexOrThrow(_cursor, "time_start");
      final int _cursorIndexOfTimestop = CursorUtil.getColumnIndexOrThrow(_cursor, "time_stop");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final List<EpgDbEntity> _result = new ArrayList<EpgDbEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final EpgDbEntity _item;
        final int _tmpChannelID;
        _tmpChannelID = _cursor.getInt(_cursorIndexOfChannelID);
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        final long _tmpTimestart;
        _tmpTimestart = _cursor.getLong(_cursorIndexOfTimestart);
        final long _tmpTimestop;
        _tmpTimestop = _cursor.getLong(_cursorIndexOfTimestop);
        final String _tmpTitle;
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _tmpTitle = null;
        } else {
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        }
        _item = new EpgDbEntity(_tmpChannelID,_tmpId,_tmpTimestart,_tmpTimestop,_tmpTitle);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<List<EpgDbEntity>> getEpgListAllTestVersion() {
    final String _sql = "SELECT * FROM epgs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"epgs"}, false, new Callable<List<EpgDbEntity>>() {
      @Override
      @Nullable
      public List<EpgDbEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfChannelID = CursorUtil.getColumnIndexOrThrow(_cursor, "channel_id");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestart = CursorUtil.getColumnIndexOrThrow(_cursor, "time_start");
          final int _cursorIndexOfTimestop = CursorUtil.getColumnIndexOrThrow(_cursor, "time_stop");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final List<EpgDbEntity> _result = new ArrayList<EpgDbEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EpgDbEntity _item;
            final int _tmpChannelID;
            _tmpChannelID = _cursor.getInt(_cursorIndexOfChannelID);
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestart;
            _tmpTimestart = _cursor.getLong(_cursorIndexOfTimestart);
            final long _tmpTimestop;
            _tmpTimestop = _cursor.getLong(_cursorIndexOfTimestop);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            _item = new EpgDbEntity(_tmpChannelID,_tmpId,_tmpTimestart,_tmpTimestop,_tmpTitle);
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
