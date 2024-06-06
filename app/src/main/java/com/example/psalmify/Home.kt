package com.example.psalmify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class Home : Fragment(), PsalmListAdapter.RecyclerViewEvent{

    private lateinit var psalmRepository: PsalmRepository

    private val homeViewModel: HomeViewModel by viewModels(){
        HomeViewModelFactory(psalmRepository)
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PsalmListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO get favorites from user
        //testing favorites
        val favoriteList : List<Int> = listOf(1,2,3)

        //TODO instead of empty list we pass the favorites Psalm list from the user : List<int>
        psalmRepository = PsalmRepository(
            AppDatabase.getDatabase(requireContext(), CoroutineScope(Dispatchers.IO)).psalmDao(),
            favoriteList
        )


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerview_home)

        adapter = PsalmListAdapter(homeViewModel, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.allPsalm.observe(viewLifecycleOwner){ psalmsItems ->
            psalmsItems.let{adapter.submitList(it)}
        }
        return view
    }
    override fun onItemClicked(position: Int) {
        // Handle the item click event here
        val psalm = adapter.currentList[position]

        val bundle : Bundle = bundleOf(
            "psalm_number" to psalm.psalm.id,
            "psalm_content" to psalm.psalm.content
        )
        // Navigate to the details or perform any action
        //findNavController().navigate(R.id.action_home2_to_detailsFragment, bundle)
        if (findNavController().currentDestination?.id != R.id.home2){

           // findNavController().navigateUp()
            // Pop the back stack
            //findNavController().popBackStack(R.id.home2, false)

            // Re-add the Home fragment
            findNavController().navigate(R.id.home2)
        }


        findNavController().navigate(R.id.action_home2_to_detailsFragment, bundle)


    }
}

