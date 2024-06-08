package com.example.psalmify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Favorite : Fragment(), PsalmListAdapter.RecyclerViewEvent {

    private val psalmRepositoryLiveData = MutableLiveData<PsalmRepository>()
    private lateinit var favoritesViewModel: FragmentViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PsalmListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val userDao =
                AppDatabase.getDatabase(requireContext(), CoroutineScope(Dispatchers.IO)).userDao()
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            val psalmRepository = if (currentUserId != null) {
                val user = userDao.getUser(currentUserId)
                val favoriteList = user?.getFavoritePsalmsList() ?: emptyList()
                PsalmRepository(
                    AppDatabase.getDatabase(requireContext(), CoroutineScope(Dispatchers.IO)).psalmDao(),
                    favoriteList
                )
            } else {
                PsalmRepository(
                    AppDatabase.getDatabase(requireContext(), CoroutineScope(Dispatchers.IO)).psalmDao(),
                    emptyList()
                )
            }
            psalmRepositoryLiveData.postValue(psalmRepository)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        recyclerView = view.findViewById(R.id.recyclerview_favorites)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        psalmRepositoryLiveData.observe(viewLifecycleOwner, Observer { psalmRepository ->
            if (psalmRepository != null) {
                favoritesViewModel = FragmentViewModel(psalmRepository)
                adapter = PsalmListAdapter(requireContext(), this,RecyclerType.FAVORITE)
                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                recyclerView.adapter = adapter

                favoritesViewModel.favoritePsalms.observe(viewLifecycleOwner) { psalmsItems ->
                    psalmsItems.let { adapter.submitList(it) }
                }
            }
        })

        return view
    }

    override fun onItemClicked(position: Int) {
        val psalm = adapter.currentList[position]
        val bundle: Bundle = bundleOf(
            "psalm_number" to psalm.psalm.id,
            "psalm_content" to psalm.psalm.content
        )
        if (findNavController().currentDestination?.id != R.id.favorite) {
            findNavController().navigate(R.id.favorite)
        }
        findNavController().navigate(R.id.action_favorite_to_detailsFragment, bundle)
    }
}
