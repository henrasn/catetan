package com.hsn.catetan.view.feature.notes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.hsn.catetan.R
import com.hsn.catetan.common.utils.px
import com.hsn.catetan.data.db.NoteEntity
import com.hsn.catetan.view.component.LineDivider
import com.hsn.catetan.view.component.SpaceDecorator
import com.hsn.catetan.view.feature.NoteViewModel
import com.hsn.catetan.view.feature.editor.NoteEditorActivity
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class NotesActivity : AppCompatActivity() {

    private val requestEditNote = 1
    private var noteAdapter =
        NoteAdapter(::gotoNoteEditor)
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var noteVM: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        noteVM = ViewModelProvider(this).get(NoteViewModel::class.java)
        setupUi()
    }

    private fun setupUi() {
        val lineDivider =
            LineDivider(
                this,
                LineDivider.VERTICAL,
                10.px()
            ).apply { bottomMargin = 8.px() }

        val spaceDecorator =
            SpaceDecorator(
                16.px(),
                LinearLayoutManager.VERTICAL,
                16.px(),
                16.px(),
                16.px()
            )

        with(rvNotes) {
            layoutManager = LinearLayoutManager(this@NotesActivity)
            setHasFixedSize(true)
            addItemDecoration(lineDivider)
            addItemDecoration(spaceDecorator)
            adapter = noteAdapter
        }

        noteVM.getNotes()?.observe(this, Observer { notes ->
            noteAdapter.notes = notes
        })

        fabAddNote.setOnClickListener { gotoNoteEditor() }
    }

    private fun gotoNoteEditor(note: NoteEntity? = null) {
        startActivity(Intent(this, NoteEditorActivity::class.java)
            .apply { putExtra(NoteEditorActivity.EXTRA_NOTE, Gson().toJson(note)) }
        )
    }
}