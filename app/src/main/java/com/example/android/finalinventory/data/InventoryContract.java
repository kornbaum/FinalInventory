package com.example.android.finalinventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DK113N on 3/19/2018.
 */

public final class InventoryContract {
    private InventoryContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.finalinventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "finalinventory";

    public static final class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_INVENTORY);

        public final static String TABLE_NAME = "finalinventory";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_STOCK_NAME = "name";

        public final static String COLUMN_STOCK_PRICE = "price";

        public final static String COLUMN_STOCK_QUANTITY ="quantity";

        public final static String COLUMN_STOCK_SUPPLIER_NAME = "supplier name";

        public final static String COLUMN_STOCK_SUPPLER_NUMBER = "supplier number";

    }
}
