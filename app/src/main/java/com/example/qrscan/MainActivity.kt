package com.example.qrscan

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.qrscan.databinding.ActivityMainBinding
import com.example.qrscan.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var mauth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mauth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if(mauth.currentUser==null){
            val loginFragment = LoginFragment()
            val trans = supportFragmentManager.beginTransaction()
            trans.add(R.id.mainFrameLayout,loginFragment)
            trans.commitAllowingStateLoss()
        }
        else{
            binding.progressBarMainActivity.visibility = View.VISIBLE
            getUserName()
        }

    }

    private fun getUserName(){
        firestore.collection("users").document(mauth.currentUser?.uid.toString()).get().addOnCompleteListener{getUser ->
            if(getUser.isSuccessful){
                Constants.userName = getUser.result["name"].toString()
                switchToQRScansFragment()
                binding.progressBarMainActivity.visibility = View.GONE
            }
            else{
                Toast.makeText(this@MainActivity,"Failed to get Username",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if(requestCode==101 && grantResults[0]==PackageManager.PERMISSION_DENIED){
            Toast.makeText(this@MainActivity,"Accept Camera Permission to Scan QR Code",Toast.LENGTH_LONG).show()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    fun switchToQRFragment(){
        val qrFragment = QrFragment()
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.mainFrameLayout,qrFragment)
        trans.commitAllowingStateLoss()
    }

    fun switchToQRScansFragment(){
        val qrScansFragment = QrScansFragment()
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.mainFrameLayout,qrScansFragment)
        trans.commitAllowingStateLoss()
    }

}