package com.example.knowacause.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.app.Fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.example.knowacause.R
import com.example.knowacause.Utils.Pref
import com.example.knowacause.activities.signIn_activity
import com.example.knowacause.network.*
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private val retrofit = NetworkModule.getRetrofit()
    private val retrofitService = retrofit?.create(KnowaCauseAPI::class.java)
    private var progress : ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    var rootView : View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

       if(rootView == null)
       {
           val pref = Pref(context)
           val email_pref = pref.email
           // Inflate the layout for this fragment
           rootView = inflater.inflate(R.layout.fragment_profile, container, false)
           val userName = rootView?.findViewById<TextView>(R.id.username_profile)
           val email = rootView?.findViewById<TextView>(R.id.email_profile)
           val ngoName = rootView?.findViewById<TextView>(R.id.ngo_name_profile)
           val ngoPhone = rootView?.findViewById<TextView>(R.id.ngo_phone_profile)
           val ngoDescription = rootView?.findViewById<TextView>(R.id.ngo_description_profile)
           val post_btn = rootView?.findViewById<Button>(R.id.post_btn_profile)
         //  val logout_user = rootView?.findViewById<Button>(R.id.logout_user)

           post_btn?.setOnClickListener(object : View.OnClickListener{
               override fun onClick(p0: View?) {
                   newPost()
               }
           })
          // progress = rootView?.findViewById<ProgressBar>(R.id.progressbar_profile)
          // progress?.visibility = View.VISIBLE
            val shimmer_container = rootView?.findViewById<ShimmerFrameLayout>(R.id.shimmer)
            shimmer_container?.startShimmer()
           retrofitService?.getUserDetails(email_pref)?.enqueue(object : retrofit2.Callback<UserFromDB>{
               override fun onFailure(call: Call<UserFromDB>?, t: Throwable?) {
                  // progress?.visibility = View.GONE
                   shimmer_container?.stopShimmer()
                   shimmer_container?.clearAnimation()
                   shimmer_container?.setShimmer(null)
                   Toast.makeText(context,"Trouble Getting the User Details, Check your Internet?", Toast.LENGTH_LONG).show()
               }

               override fun onResponse(call: Call<UserFromDB>?, response: Response<UserFromDB>) {
                  // progress?.visibility = View.GONE
                   shimmer_container?.stopShimmer()
                   shimmer_container?.clearAnimation()
                   shimmer_container?.setShimmer(null)
                   if(response.isSuccessful && response.code() == 200){
                       val userformDB = response.body()
                       userName?.text = userformDB.username
                       email?.text = userformDB.email
                       ngoName?.text = userformDB.ngoname
                       ngoPhone?.text = userformDB.ngophone
                       ngoDescription?.text = userformDB.ngoabout
                   }
               }

           })



           return rootView
       }
        return rootView
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }



    public fun newPost()
    {
        val title  = newPost_title_profile.text.toString().trim()
        val description  = newPost_desc_profile.text.toString().trim()

        if(!title.isEmpty() && !description.isEmpty())
        {
            val pref = Pref(context)
            val loggedIn_user = pref.email
            val post = PostFromClient(title,description,loggedIn_user)
           // Log.d("Post","Post from client: "+ post.added_by )


            val token = pref.authLoginToken
            progress?.visibility = View.VISIBLE
            val response = retrofitService?.savePost("Bearer "+token,post)?.enqueue(object : retrofit2.Callback<PostFromServer>{
                override fun onResponse(call: Call<PostFromServer>?, response: Response<PostFromServer>) {
                    progress?.visibility = View.GONE
                    Toast.makeText(context,response.message(), Toast.LENGTH_SHORT).show()
                   if((response.isSuccessful) && response.code() == 201)
                   {
                       Toast.makeText(context,"Post Saved!",Toast.LENGTH_LONG).show()
                   }
                }

                override fun onFailure(call: Call<PostFromServer>?, t: Throwable?) {
                    progress?.visibility = View.GONE
                    Toast.makeText(context,"Trouble Posting new Post!",Toast.LENGTH_LONG).show()
                    Log.d("Post","Saving New Post"+t?.localizedMessage)
                }

            })
        }
        else
        {
            Toast.makeText(context,"Enter Both Title and Description of the Post",Toast.LENGTH_LONG).show()
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private var INSTANCE : ProfileFragment? = null
        fun getInstance() : ProfileFragment {
            if(INSTANCE!=null)
            {
                return INSTANCE as ProfileFragment;
            }
            else{
                return ProfileFragment()
            }
        }
    }



}

//fun main(args: Array<String>)
//{
//    val str_date = "2019-02-12T17:09:03.041349+00:00"
//    val sdf2 = (SimpleDateFormat("yyyy-MM-ddHH:mm:ss")).parse(str_date)
//    println(" $sdf2")
//}
