package com.example.rishi.towatch.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import com.example.rishi.towatch.Adapters.HomeAdapter
import com.example.rishi.towatch.Fragments.SearchFragment
import com.example.rishi.towatch.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var actionSearchView: android.support.v7.widget.SearchView? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(my_toolbar)
        val toolbar = supportActionBar

        val window: Window = window
        window.clearFlags(FLAG_TRANSLUCENT_STATUS)
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        viewPager.adapter = HomeAdapter(this, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.actionbar_menu, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        actionSearchView = searchItem?.actionView as android.support.v7.widget.SearchView

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                menu.findItem(R.id.app_bar_profile).isVisible = false
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                menu.findItem(R.id.app_bar_profile).isVisible = true
                val fragment = supportFragmentManager.findFragmentById(R.id.mainView)
                if (fragment != null) {
                    fragmentManager.popBackStack()
                    supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentById(R.id.mainView)).commit()
                }
                return true
            }

        })

        actionSearchView?.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val query = actionSearchView?.query.toString()
                val bundle: Bundle = Bundle()
                bundle.putString("searchQuery", query)
                val searchFragment = SearchFragment()
                searchFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainView, searchFragment)
                        .addToBackStack(null)
                        .commit()
                return false
            }
        })

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
//            R.id.app_bar_search ->

            R.id.app_bar_profile -> {
//                val intent = Intent(this, AccountActivity::class.java)
//                startActivity(intent)
                val intent = Intent(this, DiscoverActivity::class.java)
                startActivity(intent)

            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

}
