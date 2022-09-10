package com.ahmed_badr.to_do_manager.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    var title: String? = null,
    val dateOfCreation: String? = null,
    val timeOfCreation: String? = null,
    var dateOfUpdate: String? = null,
    var timeOfUpdate: String? = null,
    var description: String? = null,
    val addByUid: String? = null,
    var id: String? = null,
    var isUpdatedNote: Boolean = false,
) : Parcelable
