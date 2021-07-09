package com.viikyh.androidremotetest

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val msgList = ArrayList<Msg>()

    private var adapter: MsgAdapter? = null

    val takePhoto = 1
    val fromAlbum = 2
    lateinit var imageUri : Uri
    lateinit var outputImage: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initMsg()
        val layoutManger = LinearLayoutManager(this)
        recyclerview.layoutManager = layoutManger
        adapter = MsgAdapter(msgList)
        recyclerview.adapter = adapter
        btn_send.setOnClickListener {
            val content = inputText.text.toString()
            if(content.isNotEmpty()){
                val msg = Msg(content)
                msgList.add(msg)
                adapter?.notifyItemInserted(msgList.size - 1)
                recyclerview.scrollToPosition(msgList.size - 1)
                inputText.setText("")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    private fun initMsg(){
        val msg1 = Msg("hi there")
        msgList.add(msg1)
        val msg2 = Msg("greetings")
        msgList.add(msg2)
        val msg3 = Msg("rising upper-cut")
        msgList.add(msg3)
    }

    //拍照及访问相册
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.icon_camera -> {
                //创建File对象
                outputImage = File(externalCacheDir, "output_image.jpg")
                if(outputImage.exists())
                    outputImage.delete()
                outputImage.createNewFile()
                imageUri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    FileProvider.getUriForFile(this, "com.viikyh.androidremotetest.fileprovider", outputImage)
                }else{
                    Uri.fromFile(outputImage)
                }
                //启动相机程序
                val intent = Intent("android.media.action.IMAGE_CAPTURE")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, takePhoto)
            }
            R.id.icon_gallery -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, fromAlbum)
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            takePhoto -> {
                if(resultCode == Activity.RESULT_OK){
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    val msg = Msg(rotateIfRequired(bitmap))
                    msgList.add(msg)
                    adapter?.notifyItemInserted(msgList.size - 1)
                    recyclerview.scrollToPosition(msgList.size - 1)
                }
            }
            fromAlbum -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    data.data?.let { uri->
                        val bitmap = getBitmapFromUri(uri)
                        if (bitmap!=null){
                            val msg = Msg(bitmap)
                            msgList.add(msg)
                            adapter?.notifyItemInserted(msgList.size - 1)
                            recyclerview.scrollToPosition(msgList.size - 1)
                        }
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap{
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation){
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap{
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }

}