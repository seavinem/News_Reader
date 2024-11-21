package com.example.newsreader.presentation.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsreader.databinding.FragmentBookmarksBinding
import com.example.newsreader.presentation.ui.newslist.adapter.NewsAdapter
import dagger.hilt.android.AndroidEntryPoint

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

        viewModel.bookmarks.observe(viewLifecycleOwner) { bookmarks ->
            bookmarksAdapter.setData(bookmarks)

            if(bookmarksAdapter.itemCount != 0) {
                binding.tvEmptyBookmarks.visibility = View.GONE
            }
            else {
                binding.tvEmptyBookmarks.visibility = View.VISIBLE
            }
        }

        viewModel.progressBarStatus.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        }

        viewModel.onRefresh()
    }

    private fun createAdapter(): NewsAdapter {
        return NewsAdapter { news ->
            val action = BookmarksFragmentDirections
                .actionBookmarksFragmentToNewsDetailFragment(
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
