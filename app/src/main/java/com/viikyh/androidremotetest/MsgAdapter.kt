package com.viikyh.androidremotetest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalArgumentException

class MsgAdapter(val msgList: List<Msg>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class TextMsgViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val msgText: TextView = view.findViewById(R.id.msg_text)
    }

    inner class ImageMsgViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val msgImage: ImageView = view.findViewById(R.id.msg_image)
    }

    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return msg.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if(viewType == Msg.TYPE_TEXT){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_text, parent, false)
            TextMsgViewHolder(view)
        }
        else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_image, parent, false)
            ImageMsgViewHolder(view)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = msgList[position]
        when(holder){
            is TextMsgViewHolder -> holder.msgText.text = msg.contentText
            is ImageMsgViewHolder -> holder.msgImage.setImageBitmap(msg.contentImage)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int {
        return msgList.size
    }

}