package com.ahmed_badr.to_do_manager.ui.elements.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ahmed_badr.to_do_manager.R
import com.ahmed_badr.to_do_manager.data.models.Note
import com.ahmed_badr.to_do_manager.databinding.FragmentNoteViewBinding
import com.ahmed_badr.to_do_manager.ui.view_models.NoteViewModel
import com.ahmed_badr.to_do_manager.utils.response.EmptyResult
import com.google.android.material.snackbar.Snackbar

class NoteViewFragment : Fragment() {
    private val args : NoteViewFragmentArgs by navArgs()
    private lateinit var note : Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        note = args.note
    }

    private lateinit var binding: FragmentNoteViewBinding

    private val noteViewModel : NoteViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bind all note data to the view
        bindNoteData(note)

        binding.apply {
            buttonBackToNoteList.setOnClickListener {
                navigateToNoteList()
            }

            buttonDelete.setOnClickListener {
                val noteId = note.id
                if (noteId != null) {
                    binding.progressBarNoteDeletion.visibility = View.VISIBLE
                    noteViewModel.deleteNote(noteId)
                    changeUiStatus()
                }
            }

            // on button edit clicked
            buttonEdit.setOnClickListener {
                navigateToNoteEditFragment()
            }
        }

        noteViewModel.noteDeleteStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is EmptyResult.Success -> {
                    val successMessage = getString(R.string.note_deleted_successfully)
                    showSnackBar(successMessage)
                    noteViewModel.listNotes()
                    navigateToNoteList()
                }
                is EmptyResult.Error -> {
                    val errorMessage = result.message.toString()
                    showSnackBar(errorMessage)
                }
            }
            changeUiStatus()
            binding.progressBarNoteDeletion.visibility = View.GONE
        }
    }

    // Show Snack Bar
    private fun showSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    // this function change ui status if the buttons is clickable ti turn unClickable and Otherwise
    private fun changeUiStatus() {
        binding.apply {
            buttonBackToNoteList.isClickable = !buttonBackToNoteList.isClickable
            buttonDelete.isClickable = !buttonDelete.isClickable
            buttonEdit.isClickable = !buttonEdit.isClickable
        }
    }

    // navigate to Note List Fragment
    private fun navigateToNoteList() {
        val navAction = NoteViewFragmentDirections.actionNoteViewFragmentToNoteListFragment()
        findNavController().navigate(navAction)
    }

    // navigate to Note Edit Fragment
    private fun navigateToNoteEditFragment() {
        val navAction = NoteViewFragmentDirections.actionNoteViewFragmentToNoteEditFragment(note)
        findNavController().navigate(navAction)
    }

    // bind the data to the view from the note object
    private fun bindNoteData(note: Note) {
        binding.apply {
            textViewNoteTitle.text = note.title
            textViewNoteDescription.text = note.description

            if (note.isUpdatedNote) {
                textViewNoteDate.text = note.dateOfUpdate
                textViewNoteTime.text = note.timeOfUpdate
                textViewDateLabel.text = getString(R.string.updated_in)
            } else {
                textViewNoteDate.text = note.dateOfCreation
                textViewNoteTime.text = note.timeOfCreation
                textViewDateLabel.text = getString(R.string.created_in)
            }
        }
    }
}