package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Folder(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val color: String = "#FF2E63",
    val icon: String = "book",
    val description: String = "",
    val itemCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
