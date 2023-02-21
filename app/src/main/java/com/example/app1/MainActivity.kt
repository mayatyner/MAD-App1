package com.example.app1

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var fName: EditText
    lateinit var mName: EditText
    lateinit var lName: EditText
    private lateinit var imageV: ImageView
    private var nextPage: Intent? = null
    private var fileString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pictureButton = findViewById<Button>(R.id.pictureButton)
        val submitButton = findViewById<Button>(R.id.submitButton)

        pictureButton.setOnClickListener {
            takeProfile()
        }

        submitButton.setOnClickListener {
            nextPage = Intent(this, LoginSuccess::class.java) //uhhhh maybe this will work???
            fName = findViewById(R.id.editFName)
            mName = findViewById(R.id.editMName)
            lName = findViewById(R.id.editLName)

            var first = fName.text.toString()
            var middle = mName.text.toString()
            var last = lName.text.toString()

            if ((first != "") && (middle != "") && (last != "")) {
                try {
                    nextPage!!.putExtra("FN_DATA", first)
                    nextPage!!.putExtra("LN_DATA", last)
                    startActivity(nextPage)
                }
                catch(e: ActivityNotFoundException) {
                    submitButton.text = "Could not submit"
                }
            }
            else {
                var warning: TextView = findViewById<TextView>(R.id.warningMessage)
                warning.text = "Please fill in the spaces above prior to submitting"
            }

        }
        imageV = findViewById(R.id.imageView)
        if (savedInstanceState != null) {
            fileString = savedInstanceState.getString("THUMB_PATH")
            val thumbNail = BitmapFactory.decodeFile(fileString)
            if (thumbNail != null) {
                imageV!!.setImageBitmap(thumbNail)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("THUMB_PATH", fileString)
    }

    private fun takeProfile() {
        val takePictureIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            cameraActivity.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
        }
        return
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val fname = "Thumbnail$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(this, "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if(result.resultCode == RESULT_OK) {
            imageV = findViewById<View>(R.id.imageView) as ImageView
            //val extras = result.data!!.extras
            //val thumbnailImage = extras!!["data"] as Bitmap?

            if (Build.VERSION.SDK_INT >= 33) {
                val thumbnailImage = result.data!!.getParcelableExtra("data", Bitmap::class.java)
                if (isExternalStorageWritable) {
                    fileString = saveImage(thumbnailImage)
                    imageV!!.setImageBitmap(thumbnailImage)
                }
            }
            else{
                val thumbnailImage = result.data!!.getParcelableExtra<Bitmap>("data")
                imageV!!.setImageBitmap(thumbnailImage)
            }


        }
    }
}

