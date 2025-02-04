package com.example.skywatcher

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//This is our birdList Adapter. This pulls out data from firebase and then populates our recyclerview

class BirdListAdapter(private val birdList: ArrayList<BirdObservationData>): RecyclerView.Adapter<BirdListAdapter.MyViewHolder>() {

   //This just inflates the layout so that our data will be shown on the recylerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val itemView= LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView)
    }

//This gets us the size of the observations for the user and populates out list with it
    override fun getItemCount(): Int {
        return birdList.size
    }
//Then this binds out text from our list item to the firebase data
override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val currentItem = birdList[position]
    Log.d("BirdListAdapter", "Binding item at position $position: ${currentItem.birdName}, ${currentItem.location}")
    holder.birdLocation.text = currentItem.location
    holder.birdAmount.text = currentItem.birdName
}


    class MyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){

        val birdLocation: TextView = itemView.findViewById(R.id.textleadinglocation)
        val birdAmount: TextView = itemView.findViewById(R.id.textleadingName)



    }

}