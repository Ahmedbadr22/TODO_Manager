package com.ahmed_badr.to_do_manager.ui.elements.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmed_badr.to_do_manager.R
import com.ahmed_badr.to_do_manager.databinding.FragmentNoteListBinding
import com.ahmed_badr.to_do_manager.ui.elements.activity.AuthenticationActivity
import com.ahmed_badr.to_do_manager.ui.view_models.NoteViewModel
import com.ahmed_badr.to_do_manager.utils.adapters.NoteAdapter
import com.ahmed_badr.to_do_manager.utils.response.ResultStatus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class NoteListFragment : Fragment() {
    private lateinit var binding: FragmentNoteListBinding

    private val noteViewModel : NoteViewModel by activityViewModels()
    private lateinit var authenticatedUserHelloMessage : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // list all the notes
        noteViewModel.listNotes()
        val user = Firebase.auth.currentUser!!
        authenticatedUserHelloMessage = "Hello, ${user.displayName}"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel.notesResultStatus.observe(viewLifecycleOwner) { resultStatus ->
            when (resultStatus) {
                is ResultStatus.Success -> {
                    val notesList = resultStatus.data
                    val notesListSize = notesList!!.size

                    if (notesListSize > 0) {
                        binding.apply {
                            notesRecyclerView.apply {
                                adapter = NoteAdapter(notesList)
                                layoutManager = LinearLayoutManager(requireContext())
                                hasFixedSize()
                            }

                            if (!notesRecyclerView.isVisible) {
                                binding.notesRecyclerView.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        binding.textViewNotingToShow.visibility = View.VISIBLE
                        binding.notesRecyclerView.visibility = View.INVISIBLE
                    }
                }
                is ResultStatus.Error -> {
                    val errorMessage = resultStatus.message.toString()
                    binding.textViewNotingToShow.text = errorMessage
                    binding.notesRecyclerView.visibility = View.INVISIBLE
                }
            }

            binding.progressBarNotesLoading.visibility = View.GONE
        }

        binding.apply {
            textViewAuthenticatedUserName.text = authenticatedUserHelloMessage

            addingNoteFloatingActionButton.setOnClickListener {
                navigateToAddNoteFragment()
            }

            imageButtonSignOut.setOnClickListener {
                signOut()
                navigateToAuthentication()
            }
        }
    }

    private fun navigateToAddNoteFragment() {
        val navAction = NoteListFragmentDirections.actionNoteListFragmentToAddNoteFragment()
        findNavController().navigate(navAction)
    }

    private fun signOut() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webClientId))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)

        googleSignInClient.signOut()
        Firebase.auth.signOut()
    }

    private fun navigateToAuthentication() {
        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        requireActivity().startActivity(intent)
        requireActivity().finish()
    }

}