package com.example.skywatcher

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


import com.google.firebase.auth.FirebaseAuth

class BirdListPage : Fragment() {

    private lateinit var dbref: DatabaseReference
    private lateinit var birdRecyclerView: RecyclerView
    private lateinit var birdArrayList: ArrayList<BirdObservationData>
    private lateinit var birdAdapter: BirdListAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myView = inflater.inflate(R.layout.fragment_bird_list_page, container, false)
        birdRecyclerView = myView.findViewById(R.id.birdList)
        birdRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        birdRecyclerView.setHasFixedSize(true)

        birdArrayList = arrayListOf()
        birdAdapter = BirdListAdapter(birdArrayList)
        birdRecyclerView.adapter = birdAdapter

        auth = FirebaseAuth.getInstance()
        getBirdData()

        return myView
    }

    private fun getBirdData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            dbref = FirebaseDatabase.getInstance().getReference("Users/Observations/$userId")

            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        birdArrayList.clear()
                        for (birdSnapshot in snapshot.children) {
                            val bird = birdSnapshot.getValue(BirdObservationData::class.java)
                            if (bird != null) {
                                birdArrayList.add(bird)
                                Log.d("BirdData", "Added bird: ${bird.birdName}")
                            }
                        }
                        Log.d("BirdData", "Total birds added: ${birdArrayList.size}")
                        birdAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}

