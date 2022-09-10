package com.ahmed_badr.to_do_manager.ui.elements.fragments

import android.annotation.SuppressLint
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
import com.ahmed_badr.to_do_manager.databinding.FragmentNoteEditBinding
import com.ahmed_badr.to_do_manager.ui.view_models.NoteViewModel
import com.ahmed_badr.to_do_manager.utils.response.ResultStatus
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class NoteEditFragment : Fragment() {
    private val args: NoteEditFragmentArgs by navArgs()

    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        note = args.note
    }

    private lateinit var binding: FragmentNoteEditBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteEditBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private val noteViewModel : NoteViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindNoteData()

        binding.apply {
            buttonSave.setOnClickListener {
                val updatedTitle = editTextNoteTitle.text.toString()
                val updatedDescription = editTextNoteDescription.text.toString()

                if(updatedTitle.isEmpty()) {
                    val titleEmptyMessage = getString(R.string.title_cant_be_empty)
                    showSnackBar(titleEmptyMessage)
                    return@setOnClickListener
                }

                if (!note.isUpdatedNote)  note.isUpdatedNote = true

                progressBarSaveLoading.visibility = View.VISIBLE

                freezeUiActions()

                note.title = updatedTitle
                note.description = updatedDescription
                note.dateOfUpdate = getCurrentDate()
                note.timeOfUpdate = getCurrentTime()

                noteViewModel.updateNote(note)
            }

            imageButtonBackToNoteView.setOnClickListener {
                navigateToNoteViewFragment()
            }
        }

        noteViewModel.noteResultStatus.observe(viewLifecycleOwner) { resultStatus ->
            when (resultStatus) {
                is ResultStatus.Success -> {
                    val noteUpdateMessage = getString(R.string.note_success_update_message)
                    showSnackBar(noteUpdateMessage)
                    navigateToNoteViewFragment()
                    noteViewModel.resetNoteStatus()
                }
                is ResultStatus.Error -> {
                    val noteErrorUpdateMessage = resultStatus.message.toString()
                    showSnackBar(noteErrorUpdateMessage)
                }
            }

            binding.progressBarSaveLoading.visibility = View.GONE
            freezeUiActions()
        }
    }

    private fun freezeUiActions() {
        binding.apply {
            imageButtonBackToNoteView.isClickable = !imageButtonBackToNoteView.isClickable
            buttonSave.isClickable = !buttonSave.isClickable
        }
    }

    private fun navigateToNoteViewFragment() {
        val navAction = NoteEditFragmentDirections.actionNoteEditFragmentToNoteViewFragment(note)
        findNavController().navigate(navAction)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun bindNoteData() {
        binding.apply {
            editTextNoteTitle.setText(note.title)
            editTextNoteDescription.setText(note.description)
        }
    }

    /**
     * Get current Time for egypt time zone
     * @return String
     */
    private fun getCurrentTime(): String {
        val tz = TimeZone.getTimeZone(getString(R.string.egypt_time_zone))
        val c = Calendar.getInstance(tz)
        val hours = String.format("%02d", c.get(Calendar.HOUR))
        val minutes = String.format("%02d", c.get(Calendar.MINUTE))
        return "$hours:$minutes"
    }

    /**
     * Get current Date
     * @return String
     */
    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate() : String {
        val currentDateObject = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        return formatter.format(currentDateObject)
    }
}