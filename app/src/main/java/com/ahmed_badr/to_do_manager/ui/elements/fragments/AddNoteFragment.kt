package com.ahmed_badr.to_do_manager.ui.elements.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ahmed_badr.to_do_manager.R
import com.ahmed_badr.to_do_manager.data.models.Note
import com.ahmed_badr.to_do_manager.databinding.FragmentAddNoteBinding
import com.ahmed_badr.to_do_manager.ui.view_models.NoteViewModel
import com.ahmed_badr.to_do_manager.utils.response.ResultStatus
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.TimeZone

class AddNoteFragment : Fragment() {
    private lateinit var binding: FragmentAddNoteBinding

    private val noteViewModel: NoteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNoteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private lateinit var note: Note
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // on user click back button
            buttonBackToNoteList.setOnClickListener {
                navigateToNoteListFragment()
            }

            // On Save Button Clicked
            buttonSave.setOnClickListener {
                applySaveNote()
            }
        }

        // Trigger any change in note result status
        noteViewModel.noteResultStatus.observe(viewLifecycleOwner) { resultStatus ->
            when (resultStatus) {
                is ResultStatus.Success -> {
                    val successMessage = getString(R.string.note_added_successfully)
                    showSnackBar(successMessage) // show success message
                    noteViewModel.listNotes() // update note list
                    noteViewModel.resetNoteStatus() // rest note result status
                    navigateToNoteViewFragment() // navigate to the view fragment to show note detail
                }
                is ResultStatus.Error -> {
                    val errorMessage = resultStatus.message.toString()
                    showSnackBar(errorMessage)
                }
            }
        }
    }

    // navigate to note list fragment
    private fun navigateToNoteListFragment() {
        val navAction = AddNoteFragmentDirections.actionAddNoteFragmentToNoteListFragment()
        findNavController().navigate(navAction)
    }

    // navigate to note view fragment
    private fun navigateToNoteViewFragment() {
        val navAction = AddNoteFragmentDirections.actionAddNoteFragmentToNoteViewFragment(note)
        findNavController().navigate(navAction)
    }

    /**
     * Save the updated note
     */
    private fun applySaveNote() {
        binding.apply {
            val title = editTextNoteTitle.text.toString()
            val description = editTextNoteDescription.text.toString()

            val currentTime = getCurrentTime()
            val currentDate = getCurrentDate()

            val currentUserUid = Firebase.auth.currentUser!!.uid

            if (title.isNotEmpty()) {
                note = Note(
                    title = title,
                    dateOfCreation = currentDate,
                    timeOfCreation = currentTime,
                    description = description,
                    addByUid = currentUserUid
                )
                noteViewModel.addNote(note)
            } else {
                val errorMessage = getString(R.string.title_cant_be_empty)
                showSnackBar(errorMessage)
            }
        }
    }

    /**
     * Show Snack bar with a message
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
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