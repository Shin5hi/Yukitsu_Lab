package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.CommunityCreation
import com.example.data.model.Creation
import com.example.data.model.Folder
import com.example.data.model.ThemeConfig
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AnimeRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- FOLDERS & CUADERNOS ---

    fun observeFolders(userId: String): Flow<List<Folder>> {
        val path = "users/$userId/folders"
        return db.collection("users").document(userId).collection("folders")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Folder::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun createFolder(folder: Folder): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/folders"
        return try {
            val docRef = db.collection("users").document(uid).collection("folders").document()
            val payload = mapOf(
                "userId" to uid,
                "name" to folder.name,
                "color" to folder.color,
                "icon" to folder.icon,
                "description" to folder.description,
                "itemCount" to 0L,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateFolder(folder: Folder): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/folders/${folder.id}"
        return try {
            val docRef = db.collection("users").document(uid).collection("folders").document(folder.id)
            val updates = mapOf(
                "name" to folder.name,
                "color" to folder.color,
                "icon" to folder.icon,
                "description" to folder.description,
                "itemCount" to folder.itemCount,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteFolder(userId: String, folderId: String): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/folders/$folderId"
        return try {
            db.collection("users").document(uid).collection("folders").document(folderId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // --- CREATIONS ---

    fun observeCreations(userId: String): Flow<List<Creation>> {
        val path = "users/$userId/creations"
        return db.collection("users").document(userId).collection("creations")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Creation::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun getCreationById(userId: String, creationId: String): Result<Creation?> {
        val path = "users/$userId/creations/$creationId"
        return try {
            val doc = db.collection("users").document(userId).collection("creations").document(creationId).get().await()
            Result.success(doc.toObject(Creation::class.java))
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    suspend fun createCreation(creation: Creation): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/creations"
        return try {
            val docRef = db.collection("users").document(uid).collection("creations").document()
            val payload = mutableMapOf<String, Any>(
                "userId" to uid,
                "title" to creation.title,
                "prompt" to creation.prompt,
                "genre" to creation.genre,
                "style" to creation.style,
                "mediaType" to creation.mediaType,
                "aspectRatio" to creation.aspectRatio,
                "tags" to creation.tags,
                "isFavorite" to creation.isFavorite,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (creation.enhancedPrompt.isNotBlank()) {
                payload["enhancedPrompt"] = creation.enhancedPrompt
            }
            if (creation.mediaUrl.isNotBlank()) {
                payload["mediaUrl"] = creation.mediaUrl
            }
            if (creation.folderId.isNotBlank()) {
                payload["folderId"] = creation.folderId
            }
            if (creation.customCss.isNotBlank()) {
                payload["customCss"] = creation.customCss
            }

            docRef.set(payload).await()

            // If assigned to a folder, increment folder itemCount
            if (creation.folderId.isNotBlank()) {
                try {
                    db.collection("users").document(uid).collection("folders").document(creation.folderId)
                        .update("itemCount", FieldValue.increment(1L), "updatedAt", FieldValue.serverTimestamp())
                        .await()
                } catch (_: Exception) {}
            }

            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateCreation(creation: Creation): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/creations/${creation.id}"
        return try {
            val docRef = db.collection("users").document(uid).collection("creations").document(creation.id)
            val updates = mutableMapOf<String, Any>(
                "title" to creation.title,
                "prompt" to creation.prompt,
                "genre" to creation.genre,
                "style" to creation.style,
                "mediaType" to creation.mediaType,
                "aspectRatio" to creation.aspectRatio,
                "tags" to creation.tags,
                "isFavorite" to creation.isFavorite,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (creation.enhancedPrompt.isNotBlank()) updates["enhancedPrompt"] = creation.enhancedPrompt
            if (creation.mediaUrl.isNotBlank()) updates["mediaUrl"] = creation.mediaUrl
            if (creation.folderId.isNotBlank()) updates["folderId"] = creation.folderId
            if (creation.customCss.isNotBlank()) updates["customCss"] = creation.customCss

            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteCreation(userId: String, creationId: String, folderId: String = ""): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/creations/$creationId"
        return try {
            db.collection("users").document(uid).collection("creations").document(creationId).delete().await()
            if (folderId.isNotBlank()) {
                try {
                    db.collection("users").document(uid).collection("folders").document(folderId)
                        .update("itemCount", FieldValue.increment(-1L), "updatedAt", FieldValue.serverTimestamp())
                        .await()
                } catch (_: Exception) {}
            }
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // --- THEMES ---

    fun observeThemes(userId: String): Flow<List<ThemeConfig>> {
        val path = "users/$userId/themes"
        return db.collection("users").document(userId).collection("themes")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(ThemeConfig::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun saveTheme(theme: ThemeConfig): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/themes"
        return try {
            val docRef = if (theme.id.isNotBlank()) {
                db.collection("users").document(uid).collection("themes").document(theme.id)
            } else {
                db.collection("users").document(uid).collection("themes").document()
            }
            val payload = mapOf(
                "userId" to uid,
                "name" to theme.name,
                "cssCode" to theme.cssCode,
                "isCurrentActive" to theme.isCurrentActive,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    // --- COMMUNITY GALLERY ---

    fun observeCommunityCreations(): Flow<List<CommunityCreation>> {
        val path = "community_creations"
        return db.collection("community_creations")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(CommunityCreation::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun shareToCommunity(creation: Creation, authorName: String): Result<String> {
        val uid = requireUserId()
        val path = "community_creations"
        return try {
            val docRef = db.collection("community_creations").document()
            val payload = mutableMapOf<String, Any>(
                "userId" to uid,
                "authorName" to authorName.ifBlank { "Yukitsu Creator" },
                "title" to creation.title,
                "prompt" to creation.prompt,
                "genre" to creation.genre,
                "style" to creation.style,
                "mediaType" to creation.mediaType,
                "aspectRatio" to creation.aspectRatio,
                "tags" to creation.tags,
                "likesCount" to 0L,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (creation.enhancedPrompt.isNotBlank()) payload["enhancedPrompt"] = creation.enhancedPrompt
            if (creation.mediaUrl.isNotBlank()) payload["mediaUrl"] = creation.mediaUrl
            if (creation.customCss.isNotBlank()) payload["customCss"] = creation.customCss

            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun toggleCommunityLike(creationId: String): Result<Unit> {
        val uid = requireUserId()
        val likeDocRef = db.collection("community_creations").document(creationId).collection("likes").document(uid)
        val creationDocRef = db.collection("community_creations").document(creationId)
        return try {
            val exists = likeDocRef.get().await().exists()
            if (exists) {
                likeDocRef.delete().await()
                creationDocRef.update("likesCount", FieldValue.increment(-1L), "updatedAt", FieldValue.serverTimestamp()).await()
            } else {
                likeDocRef.set(mapOf("userId" to uid, "createdAt" to FieldValue.serverTimestamp())).await()
                creationDocRef.update("likesCount", FieldValue.increment(1L), "updatedAt", FieldValue.serverTimestamp()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, creationDocRef.path)
            Result.failure(e)
        }
    }
}

