package com.spexco.downloadmangerexample

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.yenen.ahmet.basecorelibrary.base.download.CompleteListener
import com.yenen.ahmet.basecorelibrary.base.download.DownloadManagerListener
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.jar.Manifest


class MainActivity : AppCompatActivity(), CompleteListener {


    private var downloadManagerListener: DownloadManagerListener? = null

    override fun onResult(status: Int, reason: Int,requestId:Long) {
        Log.e("hata", "${status} -- ${reason}")
        if (status == DownloadManager.STATUS_SUCCESSFUL) {
            Toast.makeText(this, "Başarılı", Toast.LENGTH_LONG).show()
            getFile(requestId)
        } else {
            Toast.makeText(this, "Başarısız", Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManagerListener =
            DownloadManagerListener(downloadManager)
        downloadManagerListener?.setListener(this)
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadManagerListener, intentFilter)

        ActivityCompat.requestPermissions(
            this,
            listOf<String>(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ).toTypedArray(),
            111
        )

        bbb.setOnClickListener {
                download(downloadManager)
        }

    }

    private fun download(downloadManager: DownloadManager) {
        try {
            val request =
                DownloadManager.Request(Uri.parse("http://techslides.com/demos/sample-videos/small.mp4"))
                    .apply {
                        setTitle("Mp4")
                        setDescription("Dosya indiriliyor...")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"small.mp4")
                        setAllowedOverMetered(true)
                        setAllowedOverRoaming(true)
                    }
            val requestId = downloadManager.enqueue(request)
            downloadManagerListener?.addRequestIds(requestId)
        } catch (ex: Exception) {
            Log.e("hata", ex.toString())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        downloadManagerListener?.let {
            unregisterReceiver(it)
        }
        downloadManagerListener = null

    }

    private fun getFile(id:Long){
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val file = downloadManager.openDownloadedFile(id)
        val fileStream = ParcelFileDescriptor.AutoCloseInputStream(file)
        while(true){
            val ch = fileStream.read()
            if(ch == -1){
                break
            }
            Log.e("file -> ",ch.toChar().toString())
        }
    }

    /* android Q için //
    private fun openDirectory(pickerInitialUri: Uri) {
        // Choose a directory using the system's file picker.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            // Provide read access to files and sub-directories in the user-selected
            // directory.
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, 2222)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?
    ) {
        if (requestCode == 2222
            && resultCode == Activity.RESULT_OK
        ) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->

                val contentResolver = applicationContext.contentResolver

                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
// Check for the freshest data.
                contentResolver.takePersistableUriPermission(uri, takeFlags)
                Log.e("uri", uri.toString())
                // Uri değiştirelecek // File tipi alıyor //
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                download(downloadManager)
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    // android Q için */


}
