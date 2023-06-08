package com.example.qrscan

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class QrScansAdapter(private val qrCodesList: List<String>) : RecyclerView.Adapter<QrScansAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.qr_scan_single_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtContent.text = qrCodesList[position]
        holder.cardView.setOnClickListener {
            val clipboard = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("QR Scan", qrCodesList[position])
            clipboard.setPrimaryClip(clip)
            Toast.makeText(it.context,"Copied to Clipboard",Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return qrCodesList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtContent: TextView = itemView.findViewById(R.id.txtDataQrScanSingleRow)
        val cardView: CardView = itemView.findViewById(R.id.cardViewQrScanSingleRow)
    }
}