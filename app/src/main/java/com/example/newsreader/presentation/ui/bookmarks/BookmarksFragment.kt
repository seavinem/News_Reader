package com.example.newsreader.presentation.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsreader.databinding.FragmentBookmarksBinding
import com.example.newsreader.presentation.ui.newslist.adapter.NewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookmarksViewModel by viewModels()
    private val bookmarksAdapter = createAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bookmarksRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookmarksAdapter
        }

        observeViewModel()
        viewModel.onRefresh()
    }

    private fun createAdapter(): NewsAdapter {
        return NewsAdapter { news ->
            val action = BookmarksFragmentDirections
                .actionBookmarksFragmentToNewsDetailFragment(
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bookmarks.collect { bookmarks ->
                        bookmarksAdapter.setData(bookmarks)

                        if(bookmarksAdapter.itemCount != 0) {
                            binding.tvEmptyBookmarks.visibility = View.GONE
                        }
                        else {
                            binding.tvEmptyBookmarks.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}
