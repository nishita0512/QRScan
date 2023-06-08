package com.example.qrscan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrscan.databinding.FragmentQrBinding
import com.example.qrscan.databinding.FragmentQrScansBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QrScansFragment : Fragment() {

    private lateinit var binding: FragmentQrScansBinding
    private lateinit var scansList: ArrayList<String>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mauth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrScansBinding.inflate(layoutInflater,container,false)

        firestore = FirebaseFirestore.getInstance()
        mauth = FirebaseAuth.getInstance()
        getAllScans()

        binding.apply{
            progressBarRecylerView.visibility = View.VISIBLE
            txtHelloUsername.text = "Hello, ${Constants.userName}!"
            btnScanQr.setOnClickListener {
                val mainAct = requireActivity() as MainActivity
                mainAct.switchToQRFragment()
            }
        }

        return binding.root
    }

    private fun getAllScans(){

        scansList = ArrayList()

        firestore.collection("users").document(mauth.currentUser?.uid.toString()).collection("qrScans").get().addOnCompleteListener{getScans ->
            if(getScans.isSuccessful){
                val result = getScans.result
                result.forEach {
                    scansList.add(it["data"].toString())
                }
                setUpRecyclerView()
            }
            else{
                Toast.makeText(requireContext(),"Failed to get scans",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun setUpRecyclerView(){

        binding.apply{
            allScansRecyclerView.adapter = QrScansAdapter(scansList)
            allScansRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            progressBarRecylerView.visibility = View.GONE
        }

    }

}