package com.example.myfitness.ui

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myfitness.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var imageProfile: ImageView
    private lateinit var textName: TextView
    private lateinit var textBodyType: TextView
    private lateinit var textDesiredWeight: TextView
    private lateinit var textGender: TextView
    private lateinit var textWeight: TextView
    private lateinit var textHeight: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var chatButton: FloatingActionButton
    private lateinit var chatPopup: View
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatTextView: TextView
    private lateinit var imageButton: Button
    private lateinit var videoButton: Button
    private lateinit var closePopupButton: Button
    private lateinit var textDateOfBirth: TextView
    private lateinit var SeeActivity: Button
    private lateinit var textViewType: TextView


    private val PICK_IMAGE_REQUEST = 1
    private val PICK_VIDEO_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Initialize views
        imageProfile = findViewById(R.id.imageProfile)
        textName = findViewById(R.id.textName)
        textBodyType = findViewById(R.id.textBodyType)
        textDesiredWeight = findViewById(R.id.textDesiredWeight)
        textGender = findViewById(R.id.textGender)
        textWeight = findViewById(R.id.textWeight)
        textHeight = findViewById(R.id.textHeight)

        // Initialize chat components
        chatButton = findViewById(R.id.chatButton)
        chatPopup = findViewById(R.id.chatPopup)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        chatTextView = findViewById(R.id.chatTextView)
        imageButton = findViewById(R.id.imageButton)
        videoButton = findViewById(R.id.videoButton)
        closePopupButton = findViewById(R.id.closePopupButton)
        textDateOfBirth = findViewById(R.id.textDateOfBirth)
        SeeActivity = findViewById(R.id.trainingResultsButton)

        SeeActivity()

        // Load user details
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Retrieve and display user details
                        textName.text = "Имя: ${document.getString("name")}"
                        textBodyType.text = "Тип тела: ${document.getString("bodyType")}"
                        textDesiredWeight.text =
                            "Желаемый вес: ${document.getDouble("desiredWeight")}"
                        textGender.text = "Пол: ${document.getString("gender")}"
                        textWeight.text = "Вес: ${document.getDouble("weight")}"
                        textHeight.text = "Рост: ${document.getDouble("height")}"

                        // Display date of birth (assuming it's stored as a map in Firestore)
                        val dateOfBirth = document.get("dateOfBirth") as? Map<String, Any>?
                        dateOfBirth?.let {
                            val day = it["day"]
                            val month = it["month"]
                            val year = it["year"]
                            textDateOfBirth.text = "Дата рождения: $day.$month.$year"
                        }

                        // Load profile image using Glide
                        val profileImageUrl = document.getString("profileImage")
                        profileImageUrl?.let {
                            Glide.with(this)
                                .load(it)
                                .circleCrop()
                                .into(imageProfile)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }

            // Retrieve and display current session details
            db.collection("users")
                .document(user.uid)
                .collection("trainingSessions")
                .document("currentSession")
                .get()
                .addOnSuccessListener { sessionDocument ->
                    if (sessionDocument.exists()) {
                        val sessionType = sessionDocument.getString("type")
                        val sessionTimestamp = sessionDocument.getLong("timestamp")

                        // Display session details
                        sessionType?.let {
                            // Assuming you have a TextView for this in your layout
                            textBodyType.text = "Тип тела: $sessionType"
                        }

                        // Display timestamp (convert to readable format if necessary)
                        sessionTimestamp?.let {
                            // Convert timestamp to readable date/time format as needed
                            // Example: val formattedTimestamp = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(it))
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
        SeeActivity.setOnClickListener({
            val intent = Intent(this, TrainingResultsActivity::class.java)
            startActivity(intent)
        })

        // Handle chat button click
        chatButton.setOnClickListener {
            chatPopup.visibility =
                if (chatPopup.visibility == View.GONE) View.VISIBLE else View.GONE
            if (chatPopup.visibility == View.VISIBLE) {
                loadChatMessages()
            }
        }

        // Handle close popup button click
        closePopupButton.setOnClickListener {
            chatPopup.visibility = View.GONE
        }

        // Handle send button click
        sendButton.setOnClickListener {
            sendMessage()
        }

        // Handle image button click
        imageButton.setOnClickListener {
            chooseImage()
        }

        // Handle video button click
        videoButton.setOnClickListener {
            chooseVideo()
        }
    }

    private fun loadChatMessages() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val messagesRef = db.collection("chats").document(user.uid).collection("messages")


            messagesRef.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Error fetching chat messages", e)
                    return@addSnapshotListener
                }

                val messages = snapshots?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)
                }

                val chatContent = StringBuilder()
                messages?.forEach { message ->
                    if (message != null) {
                        if (message.text.isNotBlank()) {
                            chatContent.append("${message.senderId}: ${message.text}\n")
                        }
                        if (message.imageUrl.isNotBlank()) {
                            chatContent.append("${message.senderId}: Image\n")
                        }
                        if (message.videoUrl.isNotBlank()) {
                            chatContent.append("${message.senderId}: Video\n")
                        }
                    }
                }

                chatTextView.text = chatContent.toString()
            }
        }
    }


    //    private fun sendMessage() {
//        val messageText = messageEditText.text.toString().trim()
//        if (messageText.isEmpty()) {
//            return
//        }
//
//        val currentUser = auth.currentUser
//        currentUser?.let { user ->
//            // Assuming "Coach" is the sender identifier or name
//            val coachIdentifier = "User"
//
//            val message = Message(
//                senderId = coachIdentifier,  // Use Coach identifier here
//                text = messageText,
//                timestamp = System.currentTimeMillis()
//            )
//
//            db.collection("chats").document(user.uid).collection("messages")
//                .add(message)
//                .addOnSuccessListener {
//                    messageEditText.text.clear()
//                }
//                .addOnFailureListener { e ->
//                    // Handle failure
//                    Log.e(TAG, "Error sending message", e)
//                }
//        }
private fun sendMessage() {
    val messageText = messageEditText.text.toString().trim()
    if (messageText.isEmpty()) {
        return
    }

    val currentUser = auth.currentUser
    currentUser?.let { user ->
        val message = Message(
            senderId = user.uid,
            text = messageText,
            timestamp = System.currentTimeMillis()
        )

        db.collection("chats")
            .document(user.uid)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                messageEditText.text.clear()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error sending message", e)
            }
    }
}






    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun chooseVideo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    private fun sendImage(imageUri: Uri) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val ref = FirebaseStorage.getInstance().reference
                .child("images/${user.uid}/${System.currentTimeMillis()}.jpg")

            ref.putFile(imageUri)
                .addOnSuccessListener { _ ->
                    // Image uploaded successfully
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        // Use the download URL (uri) as needed
                        Log.d(TAG, "Image uploaded successfully: $uri")
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()

                        // Optionally, save the image URL to Firestore for later retrieval
                        val message = Message(
                            senderId = user.uid,
                            imageUrl = uri.toString(), // Save the image URL in your Message model
                            timestamp = System.currentTimeMillis()
                        )
                        db.collection("chats").document(user.uid).collection("messages")
                            .add(message)
                            .addOnSuccessListener {
                                // Successfully saved message with image to Firestore
                                messageEditText.text.clear()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Failed to save message with image: ${e.message}")
                                Toast.makeText(this, "Failed to save message with image: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Failed to get download URL: ${e.message}")
                        Toast.makeText(this, "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    if (e is StorageException && e.errorCode == -13010) {
                        // Handle "Object does not exist at location" error (404)
                        Log.e(TAG, "Object does not exist at location: ${e.message}")
                        createDefaultObject()
                    } else {
                        Log.e(TAG, "Failed to upload image: ${e.message}")
                        Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } ?: run {
            Log.e(TAG, "User is not authenticated")
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createDefaultObject() {
        // Implement logic to create default object "agSocket(104)" with specified attributes
        Log.d(TAG, "Creating default object agSocket(104)")
        // Example:
        val statsTag = 0xffffffff
        val statsUid = -1
        // Perform actions to create the object or handle the situation accordingly
    }




    private fun sendVideo(videoUri: Uri) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val ref = storage.reference.child("videos/${user.uid}/${System.currentTimeMillis()}.mp4")
            ref.putFile(videoUri)
                .addOnSuccessListener { uploadTask ->
                    // Video uploaded successfully, get the download URL
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        // Create a message object with video URL and other details
                        val message = Message(
                            senderId = user.uid,
                            videoUrl = uri.toString(),
                            timestamp = System.currentTimeMillis()
                        )
                        // Save message object to Firestore
                        db.collection("chats").document(user.uid).collection("messages")
                            .add(message)
                            .addOnSuccessListener {
                                // Message added successfully
                            }
                            .addOnFailureListener { e ->
                                // Handle failure
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle video upload failure
                }
        }
    }
    private fun SeeActivity(){

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val imageUri = data?.data
                    imageUri?.let {
                        sendImage(it)
                    }
                }
                PICK_VIDEO_REQUEST -> {
                    val videoUri = data?.data
                    videoUri?.let {
                        sendVideo(it)
                    }
                }
            }
        }
    }


    data class Message(
        val senderId: String = "",
        val text: String = "",
        val imageUrl: String = "",
        val videoUrl: String = "",
        val timestamp: Long = 0
    )
}
