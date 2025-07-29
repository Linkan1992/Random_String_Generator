package com.linkan.randomstringgenerator.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.linkan.randomstringgenerator.R
import com.linkan.randomstringgenerator.databinding.ActivityMainBinding
import com.linkan.randomstringgenerator.util.ResultEvent
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        observeChanges()
    }

    private fun observeChanges() {
        viewModel.result.observe(this) { result ->
            // TO-DO
            when(result){
               is ResultEvent.Loading -> {}
               is ResultEvent.Success -> {}
               is ResultEvent.Error -> {}
            }
        }
    }
}