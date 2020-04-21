package com.spexco.downloadmangerexample

import android.app.DownloadManager
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.yenen.ahmet.basecorelibrary.base.download.CompleteListener
import com.yenen.ahmet.basecorelibrary.base.download.DownloadManagerListener
import android.os.Environment
import java.io.File


class MainActivity : AppCompatActivity(), CompleteListener {


    private var downloadManagerListener: DownloadManagerListener? = null

    override fun onResult(status: Int, reason: Int) {
        if (status == DownloadManager.STATUS_SUCCESSFUL) {
            Toast.makeText(this, "Başarılı", Toast.LENGTH_LONG).show()
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

        download(downloadManager)
    }

    private fun download(downloadManager:DownloadManager) {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "small.mp4")
        val request =
            DownloadManager.Request(Uri.parse("http://techslides.com/demos/sample-videos/small.mp4"))
                .apply {
                    setTitle("Mp4")
                    setDescription("Dosya indiriliyor...")
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationUri(Uri.fromFile(file))
                    setAllowedOverMetered(true)
                    setAllowedOverRoaming(true)
                }
        val requestId = downloadManager.enqueue(request)
        downloadManagerListener?.addRequestIds(requestId)
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadManagerListener?.let {
            unregisterReceiver(it)
        }
        downloadManagerListener = null

    }
}
