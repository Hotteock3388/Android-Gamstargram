package com.example.gamstar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.gamstar.dataclass.AlarmDTO
import com.example.gamstar.dataclass.ContentDTO
import com.example.gamstar.dataclass.FollowDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_detail_view.view.*
import kotlinx.android.synthetic.main.item_detail.view.*
import okhttp3.OkHttpClient

class DetailViewFragment : Fragment() {

    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null
    var okHttpClient: OkHttpClient? = null

    var mainView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()
        okHttpClient = OkHttpClient()

        //리사이클러 뷰와 어뎁터랑 연결
        mainView = inflater.inflate(R.layout.fragment_detail_view, container, false)


        return mainView
    }

    override fun onResume() {
        super.onResume()
        mainView?.detailViewFragment_recycleView?.layoutManager = LinearLayoutManager(activity)
        mainView?.detailViewFragment_recycleView?.adapter = DetailRecyclerViewAdapter()
        var mainActivity = activity as MainActivity
        mainActivity.progress_bar.visibility = View.INVISIBLE

    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }

    inner class DetailRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val contentDTOs: ArrayList<ContentDTO>
        val contentUidList: ArrayList<String>

        init {
            contentDTOs = ArrayList()
            contentUidList = ArrayList()
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            firestore?.collection("users")?.document(uid!!)?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var userDTO = task.result?.toObject(FollowDTO::class.java)
                    if (userDTO?.followings != null) {
                        getContents(userDTO?.followings)
                    }
                }
            }
        }

        fun getContents(followers: MutableMap<String, Boolean>?) {
            imagesSnapshot = firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentDTO::class.java)!!

                    if (followers?.keys?.contains(item.uid)!!) {
                        contentDTOs.add(item)
                        contentUidList.add(snapshot.id)
                    }
                }
                notifyDataSetChanged()
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
//            view.mainConstraintLayout.maxWidth = resources.displayMetrics.widthPixels
//            view.mainConstraintLayout.minWidth = resources.displayMetrics.widthPixels
            view.mainConstraintLayout.maxHeight = resources.displayMetrics.widthPixels - 60
            view.mainConstraintLayout.maxWidth = resources.displayMetrics.widthPixels
            return CustomViewHolder(view)

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val viewHolder = (holder as CustomViewHolder).itemView

             //Profile Image 가져오기
//            firestore?.collection("profileImages")?.document(contentDTOs[position].uid!!)
//                ?.get()?.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//
//                        val url = task.result?.get("image")
//                        Log.d("url", url.toString())
//                        Glide.with(holder.itemView.context)
//                            .load(url)
//                            .apply(RequestOptions().circleCrop())
//                            .into(viewHolder.detailViewItem_profileImage)
//                    }
//                }

            var profileref = FirebaseStorage.getInstance().reference.child(contentDTOs[position].uid.toString())

            profileref.downloadUrl.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    Glide.with(holder.itemView.context)
                        .load(task.result)
                        .apply(RequestOptions().circleCrop())
                        .into(viewHolder.detailViewItem_profileImage)

                }
            }


            //UserFragment로 이동
            viewHolder.detailViewItem_profileImage.setOnClickListener {

                val fragment = UserFragment()
                val bundle = Bundle()

                bundle.putString("destinationUid", contentDTOs[position].uid)
                bundle.putString("userId", contentDTOs[position].userId)

                fragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit()
            }

            // 유저 아이디
            viewHolder.detailViewItem_profileTextView.text = contentDTOs[position].userId

            // 가운데 이미지
//            Glide.with(holder.itemView.context)
//                .load(contentDTOs[position].imageUrl)
//                .into(viewHolder.detailViewItem_imageView_content)

            var ref = FirebaseStorage.getInstance().reference.child("images").child(contentDTOs[position].imageUrl.toString());

//            var imageView = (holder as GridFragment.GridFragmentRecyclerViewAdatper.CustomViewHolder).imageView

            ref.downloadUrl.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    Glide.with(holder.itemView.context)
                        .load(task.result)
                        .apply(RequestOptions().centerInside().transform(RoundedCorners(30)))
                        .into(viewHolder.detailViewItem_imageView_content)

                }
            }

            // 설명 텍스트
            viewHolder.detailViewItem_explain_textView.text = contentDTOs[position].explain
            // 좋아요 이벤트
            viewHolder.detailViewItem_favorite_imaveVIew.setOnClickListener { favoriteEvent(position) }

            //좋아요 버튼 설정
            if (contentDTOs[position].favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                viewHolder.detailViewItem_favorite_imaveVIew.setImageResource(R.drawable.ic_favorite)

            } else {

                viewHolder.detailViewItem_favorite_imaveVIew.setImageResource(R.drawable.ic_favorite_border)
            }
            //좋아요 카운터 설정
            viewHolder.detailViewItem_favoritecounter_textView.text = "좋아요 " + contentDTOs[position].favoriteCount + "개"

            viewHolder.detailViewItem_comment_imaveVIew.setOnClickListener {
                val intent = Intent(activity, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                startActivity(intent)
            }

        }

        fun favoriteAlarm(destinationUid: String) {

            val alarmDTO = AlarmDTO()

            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = user?.email
            alarmDTO.uid = user?.uid

            alarmDTO.kind = 0
            alarmDTO.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
            var message = user?.email + getString(R.string.alarm_favorite)
        }

        override fun getItemCount(): Int {

            return contentDTOs.size

        }

        //좋아요 이벤트 기능
        private fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->

                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)) {
                    // Unstar the post and remove self from stars
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                    contentDTO?.favorites.remove(uid)

                } else {
                    // Star the post and add self to stars
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                    contentDTO?.favorites[uid] = true
                    favoriteAlarm(contentDTOs[position].uid!!)
                }
                transaction.set(tsDoc, contentDTO)
            }
        }
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
