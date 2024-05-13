package com.example.carwardagency.ui.theme.screens.services

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.android.volley.toolbox.ImageRequest
import com.example.carwardagency.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


@Composable
fun AddMechanicsScreen(modifier: NavHostController) {
    val context:Context

    LazyColumn(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()) {
        item {
            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
            ){
               Text(text = "Add Mechanic")
                var photoUri: Uri? by remember { mutableStateOf(null) }
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    photoUri = uri
                }

                var mechanicName by rememberSaveable {
                    mutableStateOf("")
                }


                var mechanicEmail by rememberSaveable {
                    mutableStateOf("")
                }

                var location by rememberSaveable {
                    mutableStateOf("")
                }

                var phone by rememberSaveable {
                    mutableStateOf("")
                }



                OutlinedTextField(
                    value = mechanicName,
                    onValueChange = { mechanicName = it },
                    label = { Text(text = "Name") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(text = "Phone") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(text = "Location") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = mechanicEmail,
                    onValueChange = { mechanicEmail = it },
                    label = { Text(text = "Email") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )






                if (photoUri != null) {

                    val painter = rememberAsyncImagePainter(
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(data = photoUri)
                            .build()
                    )

                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .width(150.dp)
                            .height(150.dp)
                            .border(1.dp, Color.Gray),
                        contentScale = ContentScale.Crop,

                        )
                } else {

                    OutlinedButton(
                        onClick = {
                            launcher.launch(
                                PickVisualMediaRequest(
                                    //Here we request only photos. Change this to .ImageAndVideo if you want videos too.
                                    //Or use .VideoOnly if you only want videos.
                                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    ) {
                        Text(text = stringResource(id = R.string.select_image))
                    }
                }


                OutlinedButton(onClick = {

                    if (photoUri != null) {


                        photoUri?.let {

                            uploadImageToFirebaseStorage(
                                it,
                                mechanicName,
                                mechanicEmail,
                                location,
                                phone,
                                context.toString()

                            )

                            mechanicName = ""
                            mechanicEmail = ""
                            location = ""
                            phone = ""
                            photoUri = null

                        }
                    }else if (mechanicEmail == ""){
                        Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
                    }
                    else if(mechanicName == ""){

                        Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show()
                    }

                    else {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                    }



                }) {

                    Text(text = stringResource(id = R.string.save_data))


                }











            }
        }
    }
}









fun uploadImageToFirebaseStorage(
    imageUri: Uri,
    mechanicName: String,
    mechanicEmail: String,
    location: String,
    phone: String,
    context: String

) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("images/${UUID.randomUUID()}")

    val uploadTask = imageRef.putFile(imageUri)
    uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
            task.exception?.let {
                throw it
            }
        }
        imageRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            saveToFirestore(
                downloadUri.toString(),
                mechanicName,
                mechanicEmail,
                location,
                phone,
                context,


                )

        } else {


            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Failed to upload image: ${task.exception?.message}")
                .setPositiveButton("OK") { _, _ ->
                    // Optional: Add actions when OK is clicked


                }
                .show()


        }
    }
}


fun saveToFirestore(
    imageUrl: String,
    mechanicName: String,
    mechanicEmail: String,
    location: String,
    phone: String,
    context: String,



    ) {


    val db = Firebase.firestore
    val imageInfo = hashMapOf(
        "imageUrl" to imageUrl,
        "mechanicName" to mechanicName,
        "mechanicEmail" to mechanicEmail,
        "location" to location,
        "phone" to phone



    )


    db.collection("Mechanics")
        .add(imageInfo)
        .addOnSuccessListener { documentReference ->


            // Show success dialog
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setTitle("Success")
                .setMessage("Data saved successfully!")
                .setPositiveButton("OK") { _, _ ->
                    // Optional: Add actions when OK is clicked
                }
                .setIcon(R.drawable.towtruck_img)
                .setCancelable(false)

            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            // Customize the dialog style (optional)
            val alertDialogStyle = alertDialog.window?.attributes
            alertDialog.window?.attributes = alertDialogStyle
        }
        .addOnFailureListener {



            AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Failed to save data")
                .setPositiveButton("OK") { _, _ ->
                    // Optional: Add actions when OK is clicked



                }
                .show()


        }
}






@Preview(showBackground = true)
@Composable
fun PreviewLight() {
    AddMechanicsScreen(rememberNavController())
}

