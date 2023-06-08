package com.example.qrscan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.qrscan.databinding.FragmentQrBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QrFragment : Fragment() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var binding: FragmentQrBinding
    private var permissions = arrayOf("android.permission.CAMERA")
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mauth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrBinding.inflate(layoutInflater,container,false)

        val mainAct = requireActivity() as MainActivity
        mainAct.requestPermissions(permissions,101)

        firestore = FirebaseFirestore.getInstance()
        mauth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = binding.scannerView
        val mainAct = requireActivity() as MainActivity
        codeScanner = CodeScanner(mainAct, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            mainAct.runOnUiThread {
                Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
            }
            val data = HashMap<String,String>()
            data["data"] = it.text
            firestore.collection("users").document(mauth.currentUser?.uid.toString()).collection("qrScans").add(data).addOnCompleteListener{ uploadQrData ->
                if(!uploadQrData.isSuccessful){
                   Toast.makeText(requireContext(),"Failed to upload QR Data to Firebase",Toast.LENGTH_SHORT).show()
                }
            }
            mainAct.switchToQRScansFragment()
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}