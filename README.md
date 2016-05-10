# RecyclerWithCursorLoader
This is just a demo of a RecyclerView loading from a CursorLoader. I will document what each class is doing.

######MainActivity
- Launching Activity
- Implements `LoaderManager.LoaderCallbacks<Cursor>`
- Houses the RecyclerView and gets everything going in the `onStart()` by calling `getSupportLoaderManager().restartLoader(1, null, this);`
- Sets the `AdapterListener` on the `RecyclerAdapter` so the activity can handle the clicks.

######BaseCursorAdapter
- This extends the `RecyclerView.Adapter<VH>`
- This class handles the `Cursor` and implements a `DataSetObserver` for keeping up with most data changes.
  - The DataSetObserver uses the Uri from `onCreateLoader(int id, Bundle args)` in the `MainActivity` to keep up with data changes.

######RecyclerAdapter
- extends the `BaseCursorAdapter`
- contains the `RecyclerView.ViewHolder`
  - `AdapterListener` is an `interface` for making call backs to the `MainActivity` when items in the RecyclerView are clicked.
    - Both onClick and onLongClick
  - `RecyclerViewHolder` extends the `RecyclerView.ViewHolder`
  
######.model.MyObject
- POJO class that is used for handling the items and their data.
- Constructors that either takes a `Cursor` object which the data will be read from, or the `long` id, `String` name, `long` dateTime
- getters for the objects data. These are called from the `RecyclerAdapter.RecyclerViewHolder`
- Method `save(Context)`. This will check the local SQLite db before editing. If the record already exists then it will update the record other wise it will insert a new record.
- Method `delete(Context)`. This will delete this object from the local SQLite db.

######.db.DBHelper
- This just implements the `SQLiteHelper`
- This is where the local SQLite db is created and can be updated in the `onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)`

######.db.TblMyObject
> This is just my personal preference. I like to have a POJO for each of my SQLite tables. This way most of the logic for the tables can be in separate classes. You also don't have to remember all the String column names in the each table as you can just add `public static final String`s for each of the column names.

- Variables
  - TABLE_NAME - is the table name. I like to use the Class name but it can be anything.
  - BASE_CONTENT_URI - Is a Uri that will be used in the `UriMatcher`. This is just a base/root Uri so to pull multiple items from the given table.
  - `String createTbl` is a String that will create this table when `createTable(@NonNull SQLiteDatabase db)` from the `DBHelper`
  - `interface CursorAllObjects` I like to use this for each of the Cursors that I might create in the app. 

######.db.MyProvider
- This extends the `ContentProvider` and implements all of the methods involved.
- Variables
  - `AUTHORITY` - This is an important one. This is what allows your ContentProvider to be called and must be unique to your app.
  - The `uriMatcher` returns an `int` value based on the Uri that is compared in the matcher. I use switch statements in the different methods of the ContentProvider to determine if there is anything extra to do with the SQL statement.
  - The `query` method is your select statement, and insert, update, and delete are self-explanatory.
  - The insert, update, and delete use the `db.beginTransaction();` and `db.setTransactionSuccessful();` so that if something goes wrong during the sql execute then the data will not be saved and everything between the beginTransaction and the setTransactionSuccessful will be rolled back to a state before the beginTransaction started.
  - In the update, insert, and delete method you will notice that at the end I call `getContext().getContentResolver().notifyChange(uri, null)` or `cr.notifyChange(uri, null)`. This is how the `DataSetObserver`in the `BaseCursorAdapter` know when something changes in the table. Because the Uri that we used to query all the objects was `content://com.smurph.recyclerwithloader.db/TblMyObject` in the `MainActivity` we also are using the same Uri to update, delete, and insert. So when the one of those methods executes the ContentResolver.notifyChange(uri, null) is called. That Uri is the same Uri used to populate the list. This tells the `AdapterDataSetObserver` in the `BaseCursorAdapter` to `notifyDataSetChanged();`. This makes the RecyclerView update its items.

######Things to keep in mind
1. Always try to include the _id in queries. This can be very useful. Also in the `BaseCursorAdapter` I had called `setHasStableIds(true);` in the `init(@Nullable Cursor cursor)` method. If the _id was not in the query returned from the CursorLoader then this would error out because there would not be anys ids to make the items unique.
