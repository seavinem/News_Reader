package com.example.newsreader.presentation.ui.newslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsreader.databinding.FragmentNewsListBinding
import com.example.newsreader.presentation.ui.newslist.adapter.NewsAdapter
import com.example.newsreader.presentation.ui.utils.NewsType
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

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

            viewModel.currentTab.observe(viewLifecycleOwner) { currentTab ->
                val selectedTab = when(currentTab) {
                    NewsType.TopHeadlines -> 0
                    NewsType.Everything -> 1
                }
                getTabAt(selectedTab)?.select()

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
        viewModel.viewState.observe(viewLifecycleOwner) { list ->
            newsAdapter.setData(list)
        }

        viewModel.progressBarStatus.observe(viewLifecycleOwner) { isLoading ->
            binding.progressDialog.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showToast(it)
                viewModel.clearError()
            }
        }
    }

    private fun createAdapter(): NewsAdapter {
        return NewsAdapter { news ->
            val action = NewsListFragmentDirections
                .actionNewsListFragmentToNewsDetailFragment(
                    title = news.title,
                    url = news.url,
                    description = news.description,
                    content = news.content,
                    author = news.author ?: "Unknown author",
                    publishedAt = news.publishedAt,
                    source = news.sourceName
                )
            findNavController().navigate(action)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}

