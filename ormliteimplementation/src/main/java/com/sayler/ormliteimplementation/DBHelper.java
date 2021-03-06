/**
 * Created by Lukasz Chromy on 10.01.14.
 * <p>

 */
package com.sayler.ormliteimplementation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sayler.ormliteimplementation.entity.Attachment;
import com.sayler.ormliteimplementation.entity.Day;

import java.io.File;
import java.sql.SQLException;

/**
 * @author Lukasz Chromy
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

  private static String TAG = "DBHelper";
  private static String DATABASE_NAME = "diary.db";
  private static int DATABASE_VERSION = 1;

  /**
   * change default db name
   *
   * @param databasePath
   */
  static void setDatabasePath(String databasePath) {
    DATABASE_NAME = databasePath;
  }

  static String getDatabasePath() {
    return DATABASE_NAME;
  }

  static boolean checkIfDatabaseFileExists() {
    File file = new File(getDatabasePath());
    return file.exists();
  }

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    setWriteAheadLoggingEnabled(false);
  }

    @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
    Log.d(TAG, "Creating database with path:  " + getDatabasePath());
    try {
      dropTables(connectionSource);
      createTables(connectionSource);
    } catch (SQLException e) {
      Log.d(TAG, "Cannot create table", e);
    }
    Log.d(TAG, "Database creation finished.");
  }

  private void createTables(ConnectionSource connectionSource) throws SQLException {
    TableUtils.createTableIfNotExists(connectionSource, Day.class);
    TableUtils.createTableIfNotExists(connectionSource, Attachment.class);
  }

  private void dropTables(ConnectionSource connectionSource) throws SQLException {
    TableUtils.dropTable(connectionSource, Day.class, true);
    TableUtils.dropTable(connectionSource, Attachment.class, true);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int currentVersion, int newVersion) {
    Log.d(TAG, "Upgrading database from version: " + currentVersion + "  to version: " + newVersion);
    onCreate(sqLiteDatabase, connectionSource);
    Log.d(TAG, "Upgrade finished.");
  }
}
