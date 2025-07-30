package com.linkan.randomstringgenerator.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.linkan.randomstringgenerator.R
import com.linkan.randomstringgenerator.databinding.ActivityMainBinding
import com.linkan.randomstringgenerator.domain.model.RandomText
import com.linkan.randomstringgenerator.util.ResultEvent
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val randomStringAdapter : RandomStringAdapter by lazy { RandomStringAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.btnGenerate.setOnClickListener {
            val length = binding.itStringLength.text.toString().toIntOrNull() ?: 0
            if(length > 0)
                viewModel.fetchRandomString(length)
            else
                Toast.makeText(this, "Input can not be empty or zero value", Toast.LENGTH_SHORT).show()
        }

        binding.fab.setOnClickListener { view ->
            // clear all item
            randomStringAdapter.stringList = ArrayList<RandomText>()
        }
        initRecyclerView()
        observeChanges()
    }

    private fun initRecyclerView() {
        binding.apply {
            stringRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            stringRecyclerView.adapter = randomStringAdapter

            randomStringAdapter.deleteOnItemClickListener { deleteItem, currentList->
                val newList = ArrayList<RandomText>()
                newList.addAll(currentList)
                newList.remove(deleteItem)
                randomStringAdapter.stringList = newList
            }
        }
    }

    private fun observeChanges() {
        viewModel.result.observe(this) { result ->
            when(result){
               is ResultEvent.Loading -> {
                   binding.progressBar.visibility = View.VISIBLE
               }
               is ResultEvent.Success -> {
                   val newList = ArrayList<RandomText>()
                   newList.addAll(randomStringAdapter.stringList)
                   newList.add(result.data)
                   randomStringAdapter.stringList = newList
               }
               is ResultEvent.Error -> {

                   Toast.makeText(this@MainActivity, result.exception.message ?: "Something Went Wrong", Toast.LENGTH_SHORT)
                       .show()
                   binding?.progressBar?.visibility = View.GONE
               }
            }
        }
    }
}