package com.example.newsreader.presentation.ui.newslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsreader.databinding.FragmentNewsListBinding
import com.example.newsreader.domain.model.NewsResult
import com.example.newsreader.presentation.ui.newslist.adapter.NewsAdapter
import com.example.newsreader.presentation.ui.utils.NewsType
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsListFragment : Fragment() {

    private var _binding: FragmentNewsListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsListViewModel by viewModels()
    private val newsAdapter = createAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabLayout()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.newsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.apply {
            addTab(newTab().setText("Top Headlines"))
            addTab(newTab().setText("Everything"))

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.currentTab.collect { currentTab ->
                        val selectedTab = when (currentTab) {
                            NewsType.TopHeadlines -> 0
                            NewsType.Everything -> 1
                        }
                        getTabAt(selectedTab)?.select()
                    }
                }
            }

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            viewModel.setNewsType(NewsType.TopHeadlines)
                            binding.newsRecycler.scrollToPosition(0)
                        }
                        1 -> {
                            viewModel.setNewsType(NewsType.Everything)
                            binding.newsRecycler.scrollToPosition(0)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.viewState.collect {
                        when (it) {
                            is NewsResult.Loading -> {
                                binding.progressDialog.visibility = if (it.isLoading) View.VISIBLE else View.GONE
                            }
                            is NewsResult.Success -> {
                                newsAdapter.setData(it.content)
                            }
                            is NewsResult.Failure -> {
                                showToast(it.errorMessage)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createAdapter(): NewsAdapter {
        return NewsAdapter { news ->
            val action = NewsListFragmentDirections
                .actionNewsListFragmentToNewsDetailFragment(
                    title = news.title.ifEmpty { "Unknown title" },
                    url = news.url.ifEmpty { "Unknown" },
                    description = news.description.ifEmpty { "Unknown description" },
                    content = news.content.ifEmpty { "Unknown content" },
                    author = news.author.ifEmpty { "Unknown author" },
                    publishedAt = news.publishedAt.ifEmpty { "Unknown date" },
                    source = news.sourceName.ifEmpty { "Unknown source" }
                )
            findNavController().navigate(action)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}

