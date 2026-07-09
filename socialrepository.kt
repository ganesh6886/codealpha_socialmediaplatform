package com.example.project.data

import android.content.Context
import com.example.project.models.Comment
import com.example.project.models.Post
import com.example.project.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SocialRepository(context: Context) {
    private val prefs = context.getSharedPreferences("social_prefs", Context.MODE_PRIVATE)
    
    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val defaultUser = User(
        id = "me",
        username = prefs.getString("username", "alex_designer") ?: "alex_designer",
        profilePictureUrl = prefs.getString("profile_pic", "https://i.pravatar.cc/150?u=me") ?: "https://i.pravatar.cc/150?u=me",
        bio = prefs.getString("bio", "UI/UX Designer | Android Enthusiast 🚀") ?: "UI/UX Designer | Android Enthusiast 🚀",
        followersCount = 1250,
        followingCount = 840
    )

    private val _currentUserProfile = MutableStateFlow(defaultUser)
    val currentUserProfile: StateFlow<User> = _currentUserProfile.asStateFlow()

    private val users = listOf(
        User("1", "android_dev", "https://i.pravatar.cc/150?u=1", "Building the future of Android", 1500, 120),
        User("2", "kotlin_fan", "https://i.pravatar.cc/150?u=2", "Kotlin Multiplatform is life", 2100, 340),
        User("3", "compose_master", "https://i.pravatar.cc/150?u=3", "Declarative UI Expert", 8900, 50),
        User("4", "material_girl", "https://i.pravatar.cc/150?u=4", "Material 3 Specialist", 3200, 450)
    )

    private val _posts = MutableStateFlow(listOf(
        Post(
            id = "p1",
            author = users[0],
            content = "Just released a new library for Jetpack Compose! It simplifies complex animations. Check it out at the link in bio! 📦 #Android #Compose",
            imageUrl = "https://picsum.photos/seed/p1/800/800",
            timestamp = System.currentTimeMillis() - 3600000,
            likesCount = 1245,
            comments = listOf(
                Comment("c1", users[1], "This is exactly what I needed! Great job.", System.currentTimeMillis() - 1800000),
                Comment("c2", users[2], "Does it support shared element transitions?", System.currentTimeMillis() - 900000)
            )
        ),
        Post(
            id = "p2",
            author = users[2],
            content = "State management in Compose doesn't have to be hard. Remember to use derivedStateOf for performance! 🧠",
            imageUrl = "https://picsum.photos/seed/p2/800/800",
            timestamp = System.currentTimeMillis() - 7200000,
            likesCount = 890
        ),
        Post(
            id = "p3",
            author = users[3],
            content = "Loving the new dynamic color support in Android 14. It makes every app feel personal. 🌈",
            imageUrl = "https://picsum.photos/seed/p3/800/800",
            timestamp = System.currentTimeMillis() - 14400000,
            likesCount = 567
        )
    ))
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _followingOnly = MutableStateFlow(false)
    val followingOnly: StateFlow<Boolean> = _followingOnly.asStateFlow()

    fun updateProfile(username: String, bio: String, profilePictureUrl: String) {
        _currentUserProfile.update { 
            it.copy(username = username, bio = bio, profilePictureUrl = profilePictureUrl) 
        }
        prefs.edit().apply {
            putString("username", username)
            putString("bio", bio)
            putString("profile_pic", profilePictureUrl)
            apply()
        }
    }

    fun setFollowingFilter(enabled: Boolean) {
        _followingOnly.value = enabled
    }

    fun login(username: String, password: String, rememberMe: Boolean): Boolean {
        // Enforce 8 character password
        if (username.isNotEmpty() && password.length >= 8) {
            _isLoggedIn.value = true
            if (rememberMe) {
                prefs.edit().putBoolean("is_logged_in", true).apply()
            }
            updateProfile(username, _currentUserProfile.value.bio, _currentUserProfile.value.profilePictureUrl)
            // Simulated: Save to Google Smart Lock / Credential Manager
            saveToGoogle(username, password)
            return true
        }
        return false
    }

    fun signUp(username: String, email: String, password: String): Boolean {
        if (username.isNotEmpty() && email.contains("@") && password.length >= 8) {
            updateProfile(username, "New member!", "https://i.pravatar.cc/150?u=$username")
            _isLoggedIn.value = true
            prefs.edit().putBoolean("is_logged_in", true).apply()
            saveToGoogle(username, password)
            return true
        }
        return false
    }

    private fun saveToGoogle(u: String, p: String) {
        // Placeholder for Google Credential Manager API
        android.util.Log.d("SocialRepository", "Saving credentials to Google for user: $u")
    }

    fun googleLogin() {
        // Placeholder for Google Sign In
        _isLoggedIn.value = true
        prefs.edit().putBoolean("is_logged_in", true).apply()
    }

    fun logout() {
        _isLoggedIn.value = false
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }

    fun toggleLike(postId: String) {
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.id == postId) {
                    post.copy(
                        isLiked = !post.isLiked,
                        likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
                    )
                } else post
            }
        }
    }

    fun addComment(postId: String, text: String) {
        val newComment = Comment(
            id = System.currentTimeMillis().toString(),
            author = _currentUserProfile.value,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.id == postId) {
                    post.copy(comments = post.comments + newComment)
                } else post
            }
        }
    }

    fun toggleFollow(userId: String) {
        _posts.update { currentPosts ->
            currentPosts.map { post ->
                if (post.author.id == userId) {
                    val newFollowing = !post.author.isFollowing
                    post.copy(author = post.author.copy(isFollowing = newFollowing))
                } else post
            }
        }
    }

    fun searchUsers(query: String): List<User> {
        if (query.isEmpty()) return emptyList()
        return (users + _currentUserProfile.value).filter {
            it.username.contains(query, ignoreCase = true) 
        }
    }

    fun createPost(content: String, imageUrl: String?) {
        val newPost = Post(
            id = "p${System.currentTimeMillis()}",
            author = _currentUserProfile.value,
            content = content,
            imageUrl = imageUrl,
            timestamp = System.currentTimeMillis(),
            likesCount = 0
        )
        _posts.update { listOf(newPost) + it }
    }

    fun deletePost(postId: String) {
        _posts.update { currentPosts ->
            currentPosts.filter { it.id != postId }
        }
    }
}