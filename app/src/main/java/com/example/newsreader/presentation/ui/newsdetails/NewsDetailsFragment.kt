package com.example.newsreader.presentation.ui.newsdetails

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.newsreader.R
import com.example.newsreader.databinding.FragmentNewsDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsDetailsFragment : Fragment() {

    private var _binding: FragmentNewsDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsDetailsViewModel by viewModels()
    private val args: NewsDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDetails()

        viewModel.checkIsBookmarked(args.url)
        viewModel.loadNewsContent(args.url)

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDetails() {
        binding.apply {
            tvTitle.text = args.title
            tvAuthor.text = getString(R.string.author, args.author)
            tvPublishedAt.text = getString(R.string.publishedAt, args.publishedAt)
            tvSource.text = getString(R.string.source, args.source)

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAddBookmark.setOnClickListener {
                viewModel.toggleBookmark(
                    title = args.title,
                    description = args.description,
                    sourceName = args.source,
                    author = args.author,
                    content = args.content,
                    url = args.url,
                    publishedAt = args.publishedAt
                )
            }
        }
    }

    private fun observeViewModel() {
        binding.apply {
            viewModel.isBookmarked.observe(viewLifecycleOwner) { isBookmarked ->
                btnAddBookmark.text =
                    if(isBookmarked) getString(R.string.remove_from_bookmarks)
                    else getString(R.string.add_to_bookmarks)
            }

            viewModel.newsContent.observe(viewLifecycleOwner) { loadedContent ->
                if(loadedContent.isNotEmpty()) {
                    tvContent.text = loadedContent
                }
                else {
                    tvContent.text = getString(R.string.more)
                    tvContent.append("\n\n")
                    tvContent.append(args.content)
                    tvContent.setOnClickListener {
                        openNewsInBrowser(args.url)
                    }
                }
                tvContent.visibility = View.VISIBLE
                tvLoadingContent.visibility = View.GONE
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                tvLoadingContent.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showToast(it)
                viewModel.clearError()
            }
        }
    }

    private fun openNewsInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
