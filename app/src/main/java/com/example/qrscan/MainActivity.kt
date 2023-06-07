package com.example.qrscan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qrscan.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mauth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mauth = FirebaseAuth.getInstance()

        if(mauth.currentUser==null){
            val loginFragment = LoginFragment()
            val frag = supportFragmentManager
            val trans = frag.beginTransaction()
            trans.add(R.id.mainFrameLayout,loginFragment)
            trans.commitAllowingStateLoss()
        }
        else{
            switchToQRFragment()
        }
    }
    fun switchToQRFragment(){
        val qrFragment = QrFragment()
        val frag = supportFragmentManager
        val trans = frag.beginTransaction()
        trans.replace(R.id.mainFrameLayout,qrFragment)
        trans.commitAllowingStateLoss()
    }
}