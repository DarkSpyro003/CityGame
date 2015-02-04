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
        public static final String COL_TITLE = "title";
        public static final String COL_COMPLETED = "completed";
        public static final String COL_SCORE = "score";
    }

    // TODO: Christina: Implement photo uri saving into local db
    public static final class Questions implements BaseColumns {
        public static final String TABLE_NAME = "questions";
        public static final String COL_QID = "qid";
        public static final String COL_GID = "gid";
        public static final String COL_TYPE = "questionType";
        public static final String COL_QUESTION = "question";
        public static final String COL_TEXT_ANSWER = "textAnswer";
        public static final String COL_MULTI_ANSWER = "multiAnswer";
        public static final String COL_PLACENAME = "placename";
        public static final String COL_EXTRAINFO = "extraInfo";
        public static final String COL_LOCALURL = "contentUrl";
        public static final String COL_CONTENT_TYPE = "contentType";
        public static final String COL_LATITUDE = "latitude";
        public static final String COL_LONGITUDE = "longitude";
        public static final String COL_ANSWERED = "answered";
        public static final String COL_ANSWERED_CORRECT = "answeredCorrect";
        public static final String COL_ANSWERED_CONTENT = "answeredContent";
    }

    public static final class QuestionMultiAnswer implements BaseColumns {
        public static final String TABLE_NAME = "MultiAnswers";
        public static final String COL_QID = "qid";
        public static final String COL_GID = "gid";
        public static final String COL_CID = "cid";
        public static final String COL_ANSWER = "answer";
    }
}
