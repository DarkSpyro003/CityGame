package be.pxl.citygame.data.database;

import android.provider.BaseColumns;

/**
 * Created by Christina on 3/02/2015.
 */
public class GameDB {

    public static final String DB_NAME = "game_content_db";
    public static final int DB_VER = 1;

    public static final class Games implements BaseColumns {
        public static final String TABLE_NAME = "games";
        public static final String COL_GID = "gid";
        public static final String COL_USERNAME = "title";
        public static final String COL_COMPLETED = "completed";
        public static final String COL_SCORE = "score";
    }

    public static final class Questions implements BaseColumns {
        public static final String TABLE_NAME = "questions";
        public static final String COL_QID = "qid";
        public static final String COL_GID = "gid";
        public static final String COL_ANSWERED = "answered";
        public static final String COL_ANSWERED_CORRECT = "answeredCorrect";
    }
}
