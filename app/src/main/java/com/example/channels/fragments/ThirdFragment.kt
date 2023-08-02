package com.example.channels.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.channels.R
import com.example.channels.list.Channels
import com.example.channels.retrofit.ChannellsApi
import com.example.channels.retrofit.ChannelsNew
import com.example.channels.retrofit.RecyclerAdapterNew
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThirdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdFragment : Fragment() {
    lateinit var adapter: RecyclerAdapterNew
    lateinit var ChannelsApi: ChannellsApi
    lateinit var layoutManager: LinearLayoutManager
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.jsonserve.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        ChannelsApi = retrofit.create(ChannellsApi::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView3)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        getAllChannelsList()
    }
    private fun getAllChannelsList() {
        ChannelsApi.getChannelList().enqueue(object : Callback<ChannelsNew> {
            override fun onFailure(call: Call<ChannelsNew>, t: Throwable) {
                // Обработка ошибки
            }

            override fun onResponse(call: Call<ChannelsNew>, response: Response<ChannelsNew>) {
                adapter = RecyclerAdapterNew(requireContext(), response.body()?.channels ?: emptyList())
                val recyclerView: RecyclerView = requireView().findViewById(R.id.recyclerView3)
                recyclerView.adapter = adapter
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThirdFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}