package com.hsn.catetan.view.feature.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hsn.catetan.R
import com.hsn.catetan.data.db.NoteEntity
import kotlinx.android.synthetic.main.item_note.view.*

class NoteAdapter(val itemSelectedListener: (NoteEntity) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var notes: List<NoteEntity> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = notes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        NoteVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_note, parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(notes[position]) note@{
            with(holder.itemView) {
                tvTitleNote.text = title
                tvContentNote.text = content

                setOnClickListener { itemSelectedListener(this@note) }
            }
        }
    }
}

class NoteVH(itemView: View) : RecyclerView.ViewHolder(itemView)
