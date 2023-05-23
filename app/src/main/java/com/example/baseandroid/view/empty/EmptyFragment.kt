package com.example.baseandroid.view.empty

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.navGraphViewModels
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baseandroid.R
import com.example.baseandroid.application.base.BaseAdapter
import com.example.baseandroid.application.base.BaseDiffUtilCallback
import com.example.baseandroid.application.base.BasePagingAdapter
import com.example.baseandroid.application.base.BasePagingSource
import com.example.baseandroid.application.base.BaseVMFragment
import com.example.baseandroid.application.base.BaseViewHolder
import com.example.baseandroid.application.base.BaseViewModel
import com.example.baseandroid.application.base.LoadingAdapter
import com.example.baseandroid.application.base.NavigationAction
import com.example.baseandroid.application.base.NavigationActionImpl
import com.example.baseandroid.application.base.autoCleaned
import com.example.baseandroid.databinding.FragmentEmptyBinding
import com.example.baseandroid.databinding.ItemLoadmoreBinding
import com.example.baseandroid.resource.utils.ResultResponse
import com.example.baseandroid.resource.utils.gone
import com.example.baseandroid.view.main_activity.NavigationMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@AndroidEntryPoint
class EmptyFragment :
    BaseVMFragment<FragmentEmptyBinding, EmptyFragmentViewModel>(FragmentEmptyBinding::inflate),
    NavigationAction by NavigationActionImpl() {

    override val viewModel: EmptyFragmentViewModel by viewModels()
    private val navViewModel: NavigationMainViewModel by navGraphViewModels(R.id.nav_main)

    private val adapter by autoCleaned {
        EmptyAdapter(requireContext())
    }

    @Inject
    lateinit var pageConfig: PagingConfig

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        initNavigation(this)
        binding.root.setBackgroundColor(Color.GRAY)
        binding.btn.setOnClickListener {
            navigateInDirection(EmptyFragmentDirections.actionEmptyFragmentToEmptyFragment2())
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter.withLoadStateFooter(LoadingAdapter())
        lifecycleScope.launchWhenCreated {
            viewModel.flowPagingData.collect { data ->
                adapter.submitData(data)
            }
        }

        navViewModel.liveData.observe(viewLifecycleOwner) {

        }
    }
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
    BaseAdapter<String, ItemLoadmoreBinding>(BaseDiffUtilCallback()) {

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
    NavigationAction by NavigationActionImpl() {

    override val viewModel: EmptyFragment2ViewModel by viewModels()
    private val navViewModel: NavigationMainViewModel by navGraphViewModels(R.id.nav_main)

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        initNavigation(this)
        binding.root.setBackgroundColor(Color.GREEN)
        binding.root.setOnClickListener {
            popBackStack()
        }
        navViewModel.liveData.observe(viewLifecycleOwner) {

        }
    }
}

@HiltViewModel
class EmptyFragment2ViewModel @Inject constructor() : BaseViewModel()

@HiltViewModel
class EmptyFragmentViewModel @Inject constructor(val savedStateHandle: SavedStateHandle) :
    BaseViewModel() {

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