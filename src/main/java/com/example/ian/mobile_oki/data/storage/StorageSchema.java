package com.example.ian.mobile_oki.data.storage;

import android.provider.BaseColumns;

/**
 * Formerly {@code StorageContract}, renamed in order to keep {@code Contract}
 * naming convention consistent.
 * <p/>
 * Created by Ian on 8/16/2017.
 */

final class StorageSchema {
    private StorageSchema(){}

    public static class CharacterOkiSetups implements BaseColumns {
        // BaseColumns causes primary key "_ID" to be inherited

        // TABLE_NAME is the character code
        public static final String COLUMN_NAME_KD_MOVE = "kd_move";
        public static final String COLUMN_NAME_OKI1_MOVE = "oki_move_1";
        public static final String COLUMN_NAME_OKI1_ROW = "oki_row_1";
        public static final String COLUMN_NAME_OKI2_MOVE = "oki_move_2";
        public static final String COLUMN_NAME_OKI2_ROW = "oki_row_2";
        public static final String COLUMN_NAME_OKI3_MOVE = "oki_move_3";
        public static final String COLUMN_NAME_OKI3_ROW = "oki_row_3";
        public static final String COLUMN_NAME_OKI4_MOVE = "oki_move_4";
        public static final String COLUMN_NAME_OKI4_ROW = "oki_row_4";
        public static final String COLUMN_NAME_OKI5_MOVE = "oki_move_5";
        public static final String COLUMN_NAME_OKI5_ROW = "oki_row_5";
        public static final String COLUMN_NAME_OKI6_MOVE = "oki_move_6";
        public static final String COLUMN_NAME_OKI6_ROW = "oki_row_6";
        public static final String COLUMN_NAME_OKI7_MOVE = "oki_move_7";
        public static final String COLUMN_NAME_OKI7_ROW = "oki_row_7";

        public static final String [] COLUMN_NAMES = {
                COLUMN_NAME_KD_MOVE,
                COLUMN_NAME_OKI1_MOVE, COLUMN_NAME_OKI1_ROW,
                COLUMN_NAME_OKI2_MOVE, COLUMN_NAME_OKI2_ROW,
                COLUMN_NAME_OKI3_MOVE, COLUMN_NAME_OKI3_ROW,
                COLUMN_NAME_OKI4_MOVE, COLUMN_NAME_OKI4_ROW,
                COLUMN_NAME_OKI5_MOVE, COLUMN_NAME_OKI5_ROW,
                COLUMN_NAME_OKI6_MOVE, COLUMN_NAME_OKI6_ROW,
                COLUMN_NAME_OKI7_MOVE, COLUMN_NAME_OKI7_ROW
        };
    }
}
