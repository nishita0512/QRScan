package com.example.qrscan
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.qrscan.databinding.FragmentLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mauth: FirebaseAuth
    private var state = 0
    private lateinit var verifyId: String
    private var user: FirebaseUser? = null
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        mauth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                } else if (e is FirebaseTooManyRequestsException) {
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                verifyId = verificationId
                binding.edtTxtPhnNumber.setText("")
                binding.edtTxtPhnNumber.visibility = View.INVISIBLE
                binding.edtTxtotp.visibility = View.VISIBLE
                state=1
                binding.btnProgressBar.visibility = View.GONE
                binding.btnContinue.visibility = View.VISIBLE
            }
        }

        binding.btnContinue.setOnClickListener {
            binding.btnProgressBar.visibility = View.VISIBLE
            binding.btnContinue.visibility = View.INVISIBLE
            if(state==0) {
                var phoneNumber = binding.edtTxtPhnNumber.text.toString()
                phoneNumber = "+91$phoneNumber"
                val options = PhoneAuthOptions.newBuilder(mauth)
                    .setPhoneNumber(phoneNumber) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity()) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
            else if(state==1){
                val otp = binding.edtTxtotp.text.toString()
                val credential = PhoneAuthProvider.getCredential(verifyId,otp)
                signInWithPhoneAuthCredential(credential)
            }
            else{
                val uname = binding.edtTxtuname.text.toString()
                val data = HashMap<String,String>()
                data["name"]=uname
                firestore.collection("users").document(user?.uid.toString()).set(data).addOnCompleteListener {addUser->
                    if(addUser.isSuccessful){
                        val mainAct = requireActivity() as MainActivity
                        Constants.userName = uname
                        mainAct.switchToQRScansFragment()
                    }
                    else{
                        Toast.makeText(requireContext(), "Failed to set name", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
        return binding.root
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mauth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    user = task.result?.user
                    binding.edtTxtotp.setText("")
                    binding.edtTxtotp.visibility = View.GONE
                    binding.edtTxtuname.visibility = View.VISIBLE
                    state=2
                    binding.btnProgressBar.visibility = View.GONE
                    binding.btnContinue.visibility = View.VISIBLE
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    }
                }
            }
    }

}