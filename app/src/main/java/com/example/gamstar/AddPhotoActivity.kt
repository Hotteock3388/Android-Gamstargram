package com.example.gamstar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.gamstar.dataclass.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_add_photo.progress_bar
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.http.Url
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    val PICK_IMAGE_FROM_ALBUM = 0

    var photoUri: Uri? = null

    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //storage = FirebaseStorage.getInstance()
        storage = FirebaseStorage.getInstance()

        firestore = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        addphoto_image.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            //이미지 선택시
            if (resultCode == Activity.RESULT_OK) {
                //이미지뷰에 이미지 세팅
                //println(data?.data)
                photoUri = data?.data
                addphoto_image.setImageURI(data?.data)
            } else {
                finish()
            }

        }
    }


    fun contentUpload() {
        progress_bar.visibility = View.VISIBLE

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_.png"

        val storageRef = storage?.reference?.child("images")?.child(imageFileName)
        val uploadTask = storageRef?.putFile(photoUri!!)


        val storage : FirebaseStorage = FirebaseStorage.getInstance("gs://gamstar-d1100")
        val storageReference: StorageReference = storage!!.reference
        val spaceRef : StorageReference = storageRef!!.child("images/$imageFileName");


        uploadTask?.addOnFailureListener {
            progress_bar.visibility = View.GONE

            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
        }?.addOnSuccessListener { task ->

            progress_bar.visibility = View.GONE

            Toast.makeText(
                this, getString(R.string.upload_success),
                Toast.LENGTH_SHORT
            ).show()

            //var uri = storageRef?.downloadUrl.toString()
            //디비에 바인딩 할 위치 생성 및 컬렉션(테이블)에 데이터 집합 생성

            //var uri = storageRef!!.downloadUrl

            //storageRef.child(imageFileName).

            Log.d("PhotoUri", photoUri.toString())
            Log.d("storageRef", storageRef.toString())
            Log.d("uploadTask", uploadTask.snapshot.metadata?.reference?.downloadUrl.toString())

            //var uri = uploadTask.result.toString()
        //    var a1 = uploadTask.result
       //     var a2 = storageRef?.downloadUrl
            //var a3 = storageRef?.downloadUrl.result

            //var a4 = storage?.getReferenceFromUrl(storageRef.toString())
        //    var a5 = spaceRef
            var uri = storageRef.toString()

            Log.d("aaaaaaaaa", storageRef.downloadUrl.toString())
            //var uri: Uri? = null


         //   var a7 = storage.getReference().child("imageas").child(imageFileName).downloadUrl
         //   var a8 = FirebaseStorage.getInstance().getReference().child("images").child(imageFileName).downloadUrl

         //   var a9 = task.metadata?.reference?.downloadUrl
            //Log.d("downloadUri", uri.toString())
            //Log.d("bb", storage?.getReferenceFromUrl(storageRef.toString()).toString())
            //시간 생성


//            var a10 = storage?.getReferenceFromUrl(storageRef.toString()).toString() "
            val contentDTO = ContentDTO()

            //이미지 주소
            contentDTO.imageUrl = imageFileName
            //유저의 UID
            contentDTO.uid = auth?.currentUser?.uid
            //게시물의 설명
            contentDTO.explain = addphoto_edit_explain.text.toString()
            //유저의 아이디
            contentDTO.userId = auth?.currentUser?.email
            //게시물 업로드 시간
            contentDTO.timestamp = System.currentTimeMillis()

            //게시물을 데이터를 생성 및 엑티비티 종료
            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)
            finish()
        }?.addOnCompleteListener {
            }

//        var storageRef = storage?.reference?.child("images")?.child(imageFileName)


    }
}
