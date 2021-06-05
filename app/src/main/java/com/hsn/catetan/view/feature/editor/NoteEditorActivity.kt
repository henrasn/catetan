package com.hsn.catetan.view.feature.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hsn.catetan.R
import com.hsn.catetan.common.utils.EditorUtil
import com.hsn.catetan.common.utils.onChange
import com.hsn.catetan.data.db.NoteEntity
import com.hsn.catetan.data.state.SpanState
import com.hsn.catetan.data.state.StyleSpanState
import com.hsn.catetan.view.feature.NoteViewModel
import kotlinx.android.synthetic.main.activity_note_editor.*
import kotlinx.android.synthetic.main.dialog_confirmation.*

class NoteEditorActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOTE = "noteExtra"
    }

    private var note: NoteEntity? = null
    private var isContentChange = false
    private var isTitleChange = false
    private val noteViewModel: NoteViewModel by lazy {
        ViewModelProvider(this).get(NoteViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_editor)

        fetchIntentData()
        setupUi()
    }

    override fun onBackPressed() {
        if (isTitleChange or isContentChange) {
            showDiscardPopup()
        } else finish()
    }

    private fun fetchIntentData() {
        note = Gson().fromJson(intent.getStringExtra(EXTRA_NOTE) ?: "", NoteEntity::class.java)
        note?.run {
            val spanConfigs = contentSpanConfig.takeIf { it.isNotEmpty() }
                ?.let { config ->
                    Gson().fromJson<List<SpanState>>(
                        config,
                        object : TypeToken<List<SpanState>>() {}.type
                    )
                }
                ?: listOf<SpanState>()

            edtTitleNote.setText(title)
            EditorUtil.mappingSpans(spanConfigs, edtContentNote.apply { setText(content) })

            btnDeleteNote.visibility = View.VISIBLE
        }
    }

    private fun setupUi() {
        edtContentNote.onChange { isContentChange = true }
        edtTitleNote.onChange { isTitleChange = true }

        edtContentNote.state?.observe(this, Observer {
            when (it) {
                is StyleSpanState.BoldState -> btnBold.isActivated = it.isBold
                is StyleSpanState.ItalicState -> btnItalic.isActivated = it.isItalic
                is StyleSpanState.UnderlineState -> btnUnderline.isActivated = it.isUnderline
            }
        })

        btnNavBack.setOnClickListener { onBackPressed() }

        btnBold.setOnClickListener { if (edtContentNote.isFocused) edtContentNote.updateBold() }
        btnItalic.setOnClickListener { if (edtContentNote.isFocused) edtContentNote.updateItalic() }
        btnUnderline.setOnClickListener { if (edtContentNote.isFocused) edtContentNote.updateUnderline() }
        btnSort.setOnClickListener { if (edtContentNote.isFocused) edtContentNote.updateUnsortedList() }
        btnUnsorted.setOnClickListener { println() }

        btnDeleteNote.setOnClickListener { showDeletePopup() }
        btnSaveNote.setOnClickListener {
            val newNote = NoteEntity(
                edtTitleNote.text.toString(),
                edtContentNote.text.toString(),
                "",
                edtContentNote.getAllSpans()
            )
            note?.run { newNote.id = id }

            noteViewModel.saveNote(newNote)
            finish()
        }

    }

    private fun showDiscardPopup() {
        getPopupConfirmation(
            getString(R.string.changes), getString(
                R.string.question_changes
            )
        ).apply {
            btnPositive.setOnClickListener {
                dismiss()
                finish()
            }
        }.show()
    }

    private fun showDeletePopup() {
        getPopupConfirmation(
            getString(R.string.delete),
            "Apakah anda yakin untuk menghapus?"
        ).apply {
            btnPositive.setOnClickListener {
                note?.let { note -> noteViewModel.deleteNote(note) }
                dismiss()
                finish()
            }
        }.show()
    }

    private fun getPopupConfirmation(title: String, message: String) =
        BottomSheetDialog(this).apply {
            setCancelable(false)
            setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_confirmation, null))
            tvTitle.text = title
            tvMessage.text = message
            btnNegative.setOnClickListener { dismiss() }
        }
}