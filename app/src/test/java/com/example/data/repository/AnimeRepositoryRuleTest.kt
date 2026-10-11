package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Creation
import com.example.data.model.Folder
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class AnimeRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createFolder_validPayload_returnsSuccess() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = AnimeRepository(firestore)

        val folder = Folder(
            name = "Bocetos Shonen",
            color = "#FF2E63",
            icon = "book",
            description = "Ideas para anime shonen",
            itemCount = 0L
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createFolder(folder)
        }
        assertTrue(result.isSuccess)
        val folderId = result.getOrThrow()
        assertTrue(folderId.isNotEmpty())
    }

    @Test
    fun observeFolders_authenticatedOwner_emitsRealtimeList() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = AnimeRepository(firestore)

        val folderName = "Test Folder ${UUID.randomUUID()}"
        val folder = Folder(
            name = folderName,
            color = "#00F0FF",
            icon = "sparkles",
            description = "",
            itemCount = 0L
        )
        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createFolder(folder)
        }
        val folderId = createResult.getOrThrow()

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeFolders(uid).first { list -> list.any { it.id == folderId } }
        }
        assertNotNull(emitted)
    }

    @Test
    fun createCreation_validPayload_createsDocument() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = AnimeRepository(firestore)

        val creation = Creation(
            title = "Ciber Samurai",
            prompt = "Samurai con katana de neon",
            genre = "seinen",
            style = "cyberpunk",
            mediaType = "image",
            aspectRatio = "16:9",
            tags = listOf("cyberpunk", "samurai")
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createCreation(creation)
        }
        assertTrue(result.isSuccess)
    }

    @Test
    fun crossUserAccess_bobCannotReadAliceCreations() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = AnimeRepository(firestore)
        val creation = Creation(
            title = "Secreto Alice",
            prompt = "Guerrero magico",
            genre = "shojo",
            style = "ghibli",
            mediaType = "image",
            aspectRatio = "1:1",
            tags = listOf("magia")
        )
        val creationId = aliceRepo.createCreation(creation).getOrThrow()

        // Switch to Bob
        signInTestUser(BOB_EMAIL)
        val bobRepo = AnimeRepository(firestore)

        val result = bobRepo.getCreationById(aliceUid, creationId)
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertTrue(ex is FirebaseFirestoreException && ex.code == FirebaseFirestoreException.Code.PERMISSION_DENIED)
    }

    @Test
    fun unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = AnimeRepository(firestore)
        val creation = Creation(
            title = "Test Creation",
            prompt = "Guerrero",
            genre = "shonen",
            style = "modern_digital",
            mediaType = "image",
            aspectRatio = "1:1"
        )
        val creationId = aliceRepo.createCreation(creation).getOrThrow()

        auth.signOut()
        val repository = AnimeRepository(firestore)
        val result = repository.getCreationById(aliceUid, creationId)
        assertTrue(result.isFailure)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_anime@test.com"
        const val BOB_EMAIL = "bob_anime@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}

