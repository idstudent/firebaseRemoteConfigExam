package com.example.remoteconfigtest

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class intro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }

    override fun onResume() {
        super.onResume()
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.fetch(0).addOnCompleteListener { task ->
            if(task.isSuccessful){
                firebaseRemoteConfig.fetchAndActivate()
                updateDialog(firebaseRemoteConfig)
            }
            else{
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("앱을 실행하는데 오류가 발생하였습니다. 다시 시도해주시기 바랍니다.")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->
                        this.finish()
                    }.show()
            }
        }
    }
    private fun getVersion() : String{
        val packageManager = this.packageManager
        return packageManager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES).versionName
    }
    private fun updateDialog(firebaseRemoteConfig : FirebaseRemoteConfig) {
        val strVersionName = getVersion()
        var strLatestVersion = firebaseRemoteConfig.getString("fb_version_name") // 파이어베이스에서 설정한 키값(매개변수 등록한거)
        Log.e("tag", strLatestVersion)
        if(strLatestVersion == "") {
            strLatestVersion = strVersionName
        }
        if (strVersionName != strLatestVersion) {
            AlertDialog.Builder(this)
                .setTitle("Update")
                .setMessage("최신 버전의 앱을 설치 후 재실행 해주시기 바랍니다. 현재 버전 $strVersionName 업데이트버전 $strLatestVersion")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    this.finish()
                }.show()
        }
        else{
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }
}