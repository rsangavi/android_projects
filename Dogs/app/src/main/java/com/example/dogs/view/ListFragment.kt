package com.example.dogs.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogs.R
import com.example.dogs.model.DogBreed
import com.example.dogs.viewmodel.DogListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {
    private lateinit var listViewModel: DogListViewModel
    private var dogsListAdapter: DogsListAdapter = DogsListAdapter(arrayListOf())

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
       setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listViewModel = ViewModelProviders.of(this).get(DogListViewModel::class.java)
        listViewModel.refresh()

        recyclerView.apply {
            adapter = dogsListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        refreshLayout.setOnRefreshListener {
            recyclerView.visibility = View.GONE
            errorText.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            listViewModel.refreshByPassCache()
            refreshLayout.isRefreshing = false
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        listViewModel.dogsLiveData.observe(viewLifecycleOwner, { dogBreedsList ->
            dogBreedsList?.let {
                recyclerView.visibility = View.VISIBLE
                dogsListAdapter.updateDogsList(dogBreedsList as ArrayList<DogBreed>)
            }
        })

        listViewModel.errorLiveData.observe(viewLifecycleOwner, { error ->
            error?.let {
                errorText.visibility = if (error == true) View.VISIBLE else View.GONE
            }

        })

        listViewModel.loadingLiveData.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (loading == true) {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.INVISIBLE
                    errorText.visibility = View.GONE
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settingsItem -> {
                view?.let {
                    Navigation.findNavController(it).
                    navigate(ListFragmentDirections.actionListFragmentToSettingsFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}