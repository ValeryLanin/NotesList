package com.example.valery.notes

import android.app.AlertDialog
import android.app.SearchManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()

    var mSharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSharedPref = this.getSharedPreferences("My_Data", android.content.Context.MODE_PRIVATE)

        val mSorting = mSharedPref!!.getString("Sort", "newest")
        when(mSorting) {
            "newest" -> loadQueryNewest("%")
            "oldest" -> loadQueryOldest("%")
            "ascending" -> loadQueryAscending("%")
            "descending" -> loadQueryDescending("%")
        }
    }

    override fun onResume() {
        super.onResume()
        val mSorting = mSharedPref!!.getString("Sort", "newest")
        when(mSorting) {
            "newest" -> loadQueryNewest("%")
            "oldest" -> loadQueryOldest("%")
            "ascending" -> loadQueryAscending("%")
            "descending" -> loadQueryDescending("%")
        }
    }

    private fun loadQueryAscending(title: String) {

        var dbManager = DBManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Title")
        listNotes.clear()
        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(ID, Title, Description))

            } while (cursor.moveToNext())
        }

        var myNotesAdapter = MyNotesAdapter(this, listNotes)

        notesLv.adapter = myNotesAdapter

        var total = notesLv.count

        val mActionBar = supportActionBar
        if (mActionBar != null) {
            mActionBar.subtitle = "You have $total note(s) list..."
        }
    }

    private fun loadQueryDescending(title: String) {

        var dbManager = DBManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Title")
        listNotes.clear()
        if (cursor.moveToLast()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(ID, Title, Description))

            } while (cursor.moveToPrevious())
        }

        var myNotesAdapter = MyNotesAdapter(this, listNotes)

        notesLv.adapter = myNotesAdapter

        var total = notesLv.count

        val mActionBar = supportActionBar
        if (mActionBar != null) {
            mActionBar.subtitle = "You have $total note(s) list..."
        }
    }

    private fun loadQueryNewest(title: String) {

        var dbManager = DBManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "ID like ?", selectionArgs, "ID")
        listNotes.clear()
        if (cursor.moveToLast()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(ID, Title, Description))

            } while (cursor.moveToPrevious())
        }

        var myNotesAdapter = MyNotesAdapter(this, listNotes)

        notesLv.adapter = myNotesAdapter

        var total = notesLv.count

        val mActionBar = supportActionBar
        if (mActionBar != null) {
            mActionBar.subtitle = "You have $total note(s) list..."
        }
    }

    private fun loadQueryOldest(title: String) {

        var dbManager = DBManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "ID like ?", selectionArgs, "ID")
        listNotes.clear()
        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))
                listNotes.add(Note(ID, Title, Description))

            } while (cursor.moveToNext())
        }

        var myNotesAdapter = MyNotesAdapter(this, listNotes)

        notesLv.adapter = myNotesAdapter

        var total = notesLv.count

        val mActionBar = supportActionBar
        if (mActionBar != null) {
            mActionBar.subtitle = "You have $total note(s) list..."
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val sv: SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadQueryAscending("%$query%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadQueryAscending("%$newText%")
                return false
            }
        });

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.addNoteBtn->{
                    startActivity(Intent(this, AddNoteActivity::class.java))
                }
                R.id.action_sort->{
                    showSortDialog()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showSortDialog() {
        val sortOptions = arrayOf("Newest", "Oldest", "Title(Ascending)", "Title(Descending)")
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Sort by")
        mBuilder.setIcon(R.drawable.ic_action_sort)
        mBuilder.setSingleChoiceItems(sortOptions, -1) {
            dialogInterface, i ->
            if (i == 0) {
                Toast.makeText(this, "Newest", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "newest")
                editor.apply()
                loadQueryNewest("%")
            }
            if (i == 1) {
                Toast.makeText(this, "Oldest", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "oldest")
                editor.apply()
                loadQueryOldest("%")
            }
            if (i == 2) {
                Toast.makeText(this, "Title(Ascending)", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "ascending")
                editor.apply()
                loadQueryAscending("%")
            }
            if (i == 3) {
                Toast.makeText(this, "Title(Descending)", Toast.LENGTH_SHORT).show()
                val editor = mSharedPref!!.edit()
                editor.putString("Sort", "descending")
                editor.apply()
                loadQueryDescending("%")
            }
            dialogInterface.dismiss()
        }

        val mDialog  = mBuilder.create()
        mDialog.show()
    }

    inner class MyNotesAdapter: BaseAdapter {
        var listNoteAdapter = ArrayList<Note>()
        var context: Context? = null

        constructor(context: Context?, listNoteArray: ArrayList<Note>) : super() {
            this.listNoteAdapter = listNoteArray
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var myView = layoutInflater.inflate(R.layout.row, null)
            var myNote = listNoteAdapter[position]
            myView.titleTv.text = myNote.nodeName
            myView.descTv.text = myNote.nodeDesc

            myView.deleteBtn.setOnClickListener {
                var dbManager = DBManager(this.context!!)
                val selectionArgs = arrayOf(myNote.nodeID.toString())
                dbManager.delete("ID=?", selectionArgs)
                loadQueryAscending("%")
            }

            myView.editBtn.setOnClickListener {
                GoToUpdateFun(myNote)
            }

            myView.copyBtn.setOnClickListener {
                val title = myView.titleTv.text.toString()
                val desc = myView.descTv.text.toString()
                val s = title + "\n" + desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = s
                Toast.makeText(this@MainActivity, "Copied...", Toast.LENGTH_LONG).show()
            }

            myView.shareBtn.setOnClickListener {
                val title = myView.titleTv.text.toString()
                val desc = myView.descTv.text.toString()
                val s  = title + "\n" + desc
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, s)
                startActivity(Intent.createChooser(shareIntent, s))
            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return listNoteAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNoteAdapter.size
        }

    }

    private fun GoToUpdateFun(myNote: Note) {
        var intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("ID", myNote.nodeID)
        intent.putExtra("name", myNote.nodeName)
        intent.putExtra("des", myNote.nodeDesc)
        startActivity(intent)
    }
}
