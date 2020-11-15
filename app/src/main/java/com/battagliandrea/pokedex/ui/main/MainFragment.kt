package com.battagliandrea.pokedex.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.battagliandrea.pokedex.R
import com.battagliandrea.pokedex.di.viewmodel.InjectingSavedStateViewModelFactory
import com.battagliandrea.pokedex.ext.getViewModel
import com.battagliandrea.pokedex.ext.observe
import com.battagliandrea.pokedex.ui.adapter.PokemonAdapter
import com.battagliandrea.pokedex.ui.base.ViewState
import com.battagliandrea.pokedex.ui.items.pokemon.OnPokemonItemClickListener
import com.battagliandrea.pokedex.ui.utils.MarginItemDecorator
import com.battagliandrea.pokedex.ui.utils.PaginationListener
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject


class MainFragment : Fragment() {

    private lateinit var mViewModel: MainViewModel

    @Inject
    lateinit var mAdapter: PokemonAdapter

    @Inject
    lateinit var abstractFactory: InjectingSavedStateViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        postponeEnterTransition()
//        view.doOnPreDraw { startPostponedEnterTransition() }
        return view
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = getViewModel<MainViewModel>(abstractFactory, savedInstanceState)
        with(mViewModel) {
            observe(listViewState){ renderPokemon(it) }
        }

        setupList()
    }

    private fun setupList(){
        recyclerView.adapter = mAdapter
        recyclerView.addItemDecoration(MarginItemDecorator(resources.getDimension(R.dimen.default_quarter_padding).toInt()))

        val lm = recyclerView.layoutManager as GridLayoutManager
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return mAdapter.getSpanSize(position)
            }
        }

        recyclerView.addOnScrollListener(object : PaginationListener(lm) {
            override fun loadMoreItems() {
                mViewModel.load()
            }

            override fun isLastPage(): Boolean {
                return mViewModel.isLastPage
            }

            override fun isLoading(): Boolean {
                return mViewModel.isLoading
            }
        })

        mAdapter.onItemClickListener = object : OnPokemonItemClickListener {

            override fun onItemClick(view: View, pokemonId: Int) {
//
//                val action = MainFragmentDirections.actionMainFragmentToDetailsFragment(pokemonId = pokemonId)
//                val extras: FragmentNavigator.Extras = FragmentNavigatorExtras(view to "${pokemonId}")
//
//                findNavController().navigate(action, extras)
            }
        }
    }

    private fun renderPokemon(state: MainViewState.PokemonList){
        with(state){
            when(listViewState){
                is ViewState.Success -> {
                    mAdapter.updateList(data = listViewState.data.orEmpty())
                }
                is ViewState.Error -> {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                }
                is ViewState.Loading -> {
                    mAdapter.showLoadingItem()
                }
            }
        }
    }
}
