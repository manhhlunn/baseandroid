package com.example.baseandroid.view.empty

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseandroid.NavMainDirections
import com.example.baseandroid.application.base.BaseAdapter
import com.example.baseandroid.application.base.BasePagingAdapter
import com.example.baseandroid.application.base.BasePagingSource
import com.example.baseandroid.application.base.BaseVMFragment
import com.example.baseandroid.application.base.BaseViewHolder
import com.example.baseandroid.application.base.BaseViewModel
import com.example.baseandroid.application.base.LoadingAdapter
import com.example.baseandroid.application.base.NavigationAction
import com.example.baseandroid.application.base.NavigationActionImpl
import com.example.baseandroid.application.base.NavigationFragment
import com.example.baseandroid.application.base.NavigationFragmentImpl
import com.example.baseandroid.application.base.autoCleaned
import com.example.baseandroid.databinding.FragmentEmptyBinding
import com.example.baseandroid.databinding.ItemLoadmoreBinding
import com.example.baseandroid.resource.utils.ResultResponse
import com.example.baseandroid.resource.utils.gone
import com.example.baseandroid.resource.utils.observeNewEvent
import com.example.baseandroid.view.main_activity.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@AndroidEntryPoint
class EmptyFragment :
    BaseVMFragment<FragmentEmptyBinding, EmptyFragmentViewModel>(FragmentEmptyBinding::inflate),
    NavigationFragment by NavigationFragmentImpl() {

    override val viewModel: EmptyFragmentViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val adapter by autoCleaned {
        EmptyAdapter(requireContext())
    }

    @Inject
    lateinit var pageConfig: PagingConfig

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        initNavigation(this, viewModel)
        binding.root.setBackgroundColor(Color.GRAY)
        binding.btn.setOnClickListener {
            viewModel.navigateInDirection(NavMainDirections.actionToEmptyFragment2())
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter.withLoadStateFooter(LoadingAdapter())
        lifecycleScope.launchWhenCreated {
            viewModel.flowPagingData.collect { data ->
                adapter.submitData(data)
            }
        }

        activityViewModel.a.observeNewEvent(viewLifecycleOwner) {
            Log.d("AAA", "observeOnceAndSkipFirst:$it ")
        }
    }

}

fun fetchDataAsFlow(): Flow<String> = callbackFlow {

}

fun createFlow(page: Int) = flow {
    val list = if (page != 10) (1..20).toList() else (1..16).toList()
    kotlinx.coroutines.delay(500)
    emit(ResultResponse.Success(list.map {
        "page: $page pos:$it"
    }))
}




class EmptyAdapter(val context: Context) :
    BasePagingAdapter<String, ItemLoadmoreBinding>(object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return false
        }
    }) {

    override fun bindViewHolderItem(
        holder: BaseViewHolder.ItemViewHolder<ItemLoadmoreBinding>,
        item: String,
        position: Int
    ) {
        holder.binding.tv.text = item
        holder.binding.progressBar.gone()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder.ItemViewHolder<ItemLoadmoreBinding> {
        val binding =
            ItemLoadmoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder.ItemViewHolder(binding)
    }


}

class EmptyAdapter2(val context: Context) :
    BaseAdapter<String, ItemLoadmoreBinding>(object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return false
        }
    }) {


    override fun createItemViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder.ItemViewHolder<ItemLoadmoreBinding> {
        val binding =
            ItemLoadmoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder.ItemViewHolder(binding)
    }

    override fun onBindItemViewHolder(
        holder: BaseViewHolder.ItemViewHolder<ItemLoadmoreBinding>,
        item: String,
        position: Int
    ) {
        holder.binding.tv.text = item
        holder.binding.progressBar.gone()
    }


}

@AndroidEntryPoint
class EmptyFragment2 :
    BaseVMFragment<FragmentEmptyBinding, EmptyFragment2ViewModel>(FragmentEmptyBinding::inflate),
    NavigationFragment by NavigationFragmentImpl() {

    override val viewModel: EmptyFragment2ViewModel by viewModels()


    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        initNavigation(this, viewModel)
        binding.root.setBackgroundColor(Color.GREEN)
        binding.root.setOnClickListener {
            viewModel.popBackStack()
        }

    }

}

@HiltViewModel
class EmptyFragment2ViewModel @Inject constructor() : BaseViewModel(),
    NavigationAction by NavigationActionImpl()

@HiltViewModel
class EmptyFragmentViewModel @Inject constructor(val savedStateHandle: SavedStateHandle) :
    BaseViewModel(), NavigationAction by NavigationActionImpl() {

    val flowPagingData by lazy {
        val requestFlow = { page: Int -> createFlow(page) }
        return@lazy Pager(
            config = pageConfig,
            pagingSourceFactory = {
                BasePagingSource(requestFlow) {
                    onHandleException(it)
                }
            }
        ).flow.cachedIn(viewModelScope)
    }

    val data = liveData {
        emit((1..100).toList().map { "VALUE $it" })
    }

    @Inject
    lateinit var pageConfig: PagingConfig


}