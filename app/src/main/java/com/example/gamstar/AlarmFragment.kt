package com.example.gamstar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gamstar.dataclass.AlarmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_alarm.view.*
import kotlinx.android.synthetic.main.item_commentlayout.view.*

class AlarmFragment : Fragment() {

    var alarmSnapshot: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)
        view.alarmFragment_RecyclerView.adapter = AlarmRecyclerViewAdapter()
        view.alarmFragment_RecyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class AlarmRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val alarmDTOList = ArrayList<AlarmDTO>()

        init {
            Log.d("init", "init")
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            //println(uid)
//            FirebaseFirestore.getInstance()
//                .collection("alarms")
//                .whereEqualTo("destinationUid", uid)
//                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                    alarmDTOList.clear()
//                    if(querySnapshot == null)return@addSnapshotListener
//                    Log.d("querySnapshot", querySnapshot.toString())
//                    for (snapshot in querySnapshot.documents) {
//                        alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
//                        Log.d("add", "add")
//                    }
//                    alarmDTOList.sortByDescending { it.timestamp }
//                    notifyDataSetChanged()
//                }

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid)?.get().addOnCompleteListener {
                task->
                if(task.isSuccessful){
                    for(dc in task.result!!.documents){
                        alarmDTOList.add(dc.toObject(AlarmDTO::class.java)!!)
//                        Log.d("userId", dc.get("userId").toString())
//                        Log.d("kind", dc.get("kind").toString())
//                        Log.d("ttttt", dc.toObject(AlarmDTO::class.java)!!.toString())
                    }
                    alarmDTOList.sortByDescending { it.timestamp }
                    notifyDataSetChanged()
                }

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_commentlayout, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val profileImage = holder.itemView.commentviewitem_imageview_profile
            val commentTextView = holder.itemView.commentviewitem_textview_comment

            FirebaseFirestore.getInstance().collection("profileImages")
                .document(alarmDTOList[position].uid!!).get().addOnCompleteListener {
                    task ->
                    if(task.isSuccessful){


                    }

                }

            FirebaseFirestore.getInstance().collection("profileImages")
                .document(alarmDTOList[position].uid!!).get().addOnCompleteListener {
                        task ->
                    if(task.isSuccessful){
                        val url = task.result!!.get("image")
                        Log.d("taskA",url.toString())
                        Glide.with(activity)
                            .load("https://firebasestorage.googleapis.com/v0/b/gamstar-d1100.appspot.com/o/images%2FJPEG_20200713_195853_.png?alt=media&token=275b83f4-3c8d-47eb-a3a3-5f239b2efd1c")
                            .apply(RequestOptions().circleCrop())
                            .into(profileImage)
                    }
                }

//                var ref = FirebaseFirestore.getInstance().collection("profileImages").document(alarmDTOList[position].uid!!).get()
//            ref.addOnCompleteListener {
//                    task ->
//                Log.d("taskB", task.result?.get("image")?.toString())
//                if(task.isSuccessful){
//                    Glide.with(holder.itemView.context)
////              .applyDefaultRequestOptions(RequestOptions().centerCrop())
//                        //.load(contentDTOs[position].imageUrl)
//                        .load(task.result?.get("image"))
//                        .apply(RequestOptions().circleCrop())
//                        .into(profileImage)
//                }
//            }

            when (alarmDTOList[position].kind) {
                0 -> {
                    val str_0 = alarmDTOList[position].userId + getString(R.string.alarm_favorite)
                    commentTextView.text = str_0
                }

                1 -> {
                    val str_1 = alarmDTOList[position].userId + getString(R.string.alarm_who) + alarmDTOList[position].message + getString(R.string.alarm_comment)
                    commentTextView.text = str_1
                }

                2 -> {
                    val str_2 = alarmDTOList[position].userId + getString(R.string.alarm_follow)
                    commentTextView.text = str_2
                }
            }
        }

        override fun getItemCount(): Int {
            return alarmDTOList.size
            //return 2
        }
        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }
}