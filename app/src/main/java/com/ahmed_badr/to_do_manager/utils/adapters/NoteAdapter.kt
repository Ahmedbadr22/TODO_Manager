package com.ahmed_badr.to_do_manager.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ahmed_badr.to_do_manager.R
import com.ahmed_badr.to_do_manager.data.models.Note
import com.ahmed_badr.to_do_manager.databinding.ItemNoteBinding
import com.ahmed_badr.to_do_manager.ui.elements.fragments.NoteListFragmentDirections

class NoteAdapter(val data: MutableList<Note>) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = data[position]
        holder.setNote(note)
        holder.binding.cardViewNote.setOnClickListener {
            val navigationAction = NoteListFragmentDirections.actionNoteListFragmentToNoteViewFragment(note)
            holder.itemView.findNavController().navigate(navigationAction)
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setNote(note: Note) {
            binding.apply {
                textViewNoteTitle.text = note.title
                textViewNoteDescription.text = note.description

                if (note.isUpdatedNote) {
                    textViewNoteDate.text = note.dateOfUpdate
                    textViewNoteTime.text = note.timeOfUpdate
                    textViewDateLabel.text = itemView.context.getString(R.string.updated_in)
                } else {
                    textViewNoteDate.text = note.dateOfCreation
                    textViewNoteTime.text = note.timeOfCreation
                    textViewDateLabel.text = itemView.context.getString(R.string.created_in)
                }
            }
        }
    }
}