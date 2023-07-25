package com.example.channels

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.channels.FavUtils

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {
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

    //////список
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Здесь можно выполнять все операции, связанные с представлениями макета

        val recyclerView: RecyclerView? = view.findViewById(R.id.channelList)
        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = CustomRecyclerAdapter(getCatList())
        } else {
            // Обработка случая, когда recyclerView равен null
            Toast.makeText(requireContext(), "Error: RecyclerView not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCatList(): List<Channels> {
        val catNames = this.resources.getStringArray(R.array.channel_names)
        val catDescriptions = this.resources.getStringArray(R.array.channel_desc)
        val catIcons = this.resources.obtainTypedArray(R.array.channel_icons)
        val channelList = ArrayList<Channels>()

        for (i in catNames.indices) {
            val name = catNames[i]
            val description = catDescriptions[i]
            val iconResource = catIcons.getResourceId(i, R.drawable.ic_launcher_background)
            val icon_fav = R.drawable.baseline_star_24
            val favSelected = i in FavUtils.favSelectedPositions // Проверяем, есть ли позиция в наборе избранных позиций

            val channel = Channels(name, description, iconResource, icon_fav, favSelected)
            channelList.add(channel)
        }

        catIcons.recycle()

        return channelList
    }
     //////////////////

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}