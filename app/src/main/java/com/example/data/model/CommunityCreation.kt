package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class CommunityCreation(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val title: String = "",
    val prompt: String = "",
    val enhancedPrompt: String = "",
    val genre: String = "shonen",
    val style: String = "modern_digital",
    val mediaType: String = "image",
    val mediaUrl: String = "",
    val aspectRatio: String = "1:1",
    val tags: List<String> = emptyList(),
    val customCss: String = "",
    val likesCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
