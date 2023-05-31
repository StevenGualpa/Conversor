package com.geek.converor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import java.io.File
import java.util.concurrent.ExecutorService
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.vision.text.TextRecognizer

class MainActivity : AppCompatActivity() {


    private val REQUEST_IMAGE_PICK = 101
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var recognizer: TextRecognizer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image_view)
        textView = findViewById(R.id.tv_extracted_text)
        val btnSelectImage: Button = findViewById(R.id.btn_select_image)

        btnSelectImage.setOnClickListener {
            selectImageFromGallery()
        }

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_PICK) {
            val imageUri = data?.data
            imageView.setImageURI(imageUri)

            // Convert image to bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            // Recognize text from bitmap
            recognizeTextFromImage(bitmap)
        }
    }

    private fun recognizeTextFromImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Display recognized text
                textView.text = visionText.text
            }
            .addOnFailureListener { e ->
                // Handle error
                e.printStackTrace()
            }
    }


}