package com.example.android.finalinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by DK113N on 3/19/2018.
 */

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int INVENTORY = 100;

    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate(){
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(("Cannot query unknown URI " + uri));
            }
        return cursor;
        }

        @Override
                public String getType(Uri uri) {return null;}

                @Override
    public Uri insert(Uri uri, ContentValues contentValues){
        String name = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_STOCK_NAME);
        if (name == null){
            throw new IllegalArgumentException("Inventory requires a name");
        }

        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return insertInventory(uri, contentValues);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertInventory(Uri uri, ContentValues values){
        String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_STOCK_NAME);
        if (name == null){
            throw new IllegalArgumentException("Inventory requires a name");
        }

        Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_STOCK_PRICE);
        if (price == null){
            throw new IllegalArgumentException("Inventory requires a price");
        }

        Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_STOCK_QUANTITY);
        if (quantity == null){
            throw new IllegalArgumentException("Inventory requires a quantity");
        }

        String supplierName = values.getAsString(InventoryContract.InventoryEntry.COLUMN_STOCK_SUPPLIER_NAME);
        if (supplierName == null){
            throw new IllegalArgumentException("Inventory requires a supplier name");
        }

        String supplierNumber = values.getAsString(InventoryContract.InventoryEntry.COLUMN_STOCK_SUPPLER_NUMBER);
        if (supplierNumber.length() < 10 || supplierNumber.length() > 10){
            throw new IllegalArgumentException("Inventory requires a valid supplier number");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);

        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public  int delete(Uri uri, String selection, String[] selectionArgs){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
            break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,String[] selectionArgs){
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_STOCK_NAME)){
            String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_STOCK_NAME);
            if (name == null){
                throw new IllegalArgumentException("Inventory requires a name");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_STOCK_PRICE)){
            int price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_STOCK_PRICE);
            if (price < 0){
                throw new IllegalArgumentException("Inventory requires a valid price");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_STOCK_QUANTITY)){
            int quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_STOCK_QUANTITY);
            if (quantity < 0){
                throw new IllegalArgumentException("Inventory requires a valid quantity");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_STOCK_SUPPLIER_NAME)){
            String supplierName = values.getAsString(InventoryContract.InventoryEntry.COLUMN_STOCK_SUPPLIER_NAME);
            if (supplierName == null){
                throw new IllegalArgumentException("Inventory requires a supplier name");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_STOCK_SUPPLER_NUMBER)){
            String supplierNumber = values.getAsString(InventoryContract.InventoryEntry.COLUMN_STOCK_SUPPLER_NUMBER);
            if (supplierNumber == null){
                throw new IllegalArgumentException("Inventory requires a supplier number");
            }
        }

        if (values.size() == 0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
