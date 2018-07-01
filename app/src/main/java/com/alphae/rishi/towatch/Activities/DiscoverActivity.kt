package com.alphae.rishi.towatch.Activities

import android.app.FragmentTransaction
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.alphae.rishi.towatch.Api.ServiceGenerator
import com.alphae.rishi.towatch.BuildConfig
import com.alphae.rishi.towatch.Fragments.DiscoverFragment
import com.alphae.rishi.towatch.POJOs.Configrations.Genres.Genre
import com.alphae.rishi.towatch.POJOs.Configrations.Genres.GenreResult
import com.alphae.rishi.towatch.POJOs.Configrations.Language
import com.alphae.rishi.towatch.R
import com.alphae.rishi.towatch.TmdbApi.TmdbApiClient
import kotlinx.android.synthetic.main.bottom_sheet.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DiscoverActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var sheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private lateinit var client: TmdbApiClient
    private lateinit var genres: List<Genre>
    private lateinit var languages: List<Language>
    private var currentFragment: DiscoverFragment? = null

    override fun onNothingSelected(parent: AdapterView<*>?) {
//        Toast.makeText(parent?.context, "Nothing Selected ", Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        val item = parent?.getItemAtPosition(position).toString()
//        Toast.makeText(parent?.context, "Selected: $item", Toast.LENGTH_LONG).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

//        setSupportActionBar(toolbarDiscover)
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        client = ServiceGenerator.createService(TmdbApiClient::class.java)

        originalLanguageSpinner.onItemSelectedListener = this
        genreSpinner.onItemSelectedListener = this
        yearSpinner.onItemSelectedListener = this
        voteAverageSpinner.onItemSelectedListener = this
        sortSpinner.onItemSelectedListener = this

        val sortList: ArrayList<String> = ArrayList()
        sortList.apply {
            add("Popularity Asc")
            add("Popularity Desc")
            add("Release_Date Asc")
            add("Release_Date Desc")
            add("Original_Title Asc")
            add("Original_Title Desc")
            add("Vote_Average Asc")
            add("Vote_Average Desc")
        }
        val sortAdapter = ArrayAdapter<String>(this@DiscoverActivity, android.R.layout.simple_spinner_item, sortList)
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = sortAdapter

        val voteList: ArrayList<String> = ArrayList()
        voteList.apply {
            add("9+")
            add("8+")
            add("7+")
            add("6+")
            add("5+")
            add("4+")
            add("3+")
            add("2+")
            add("1+")
            add("0+")
        }
        val voteAdapter = ArrayAdapter<String>(this@DiscoverActivity, android.R.layout.simple_spinner_item, voteList)
        voteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        voteAverageSpinner.adapter = voteAdapter

        val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        val yearList: ArrayList<String> = ArrayList()
        for (count in currentYear downTo 1912)
            yearList.add(count.toString())
        val yearAdapter = ArrayAdapter<String>(this@DiscoverActivity, android.R.layout.simple_spinner_item, yearList)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        callLanguages().enqueue(object : Callback<List<Language>> {
            override fun onFailure(call: Call<List<Language>>?, t: Throwable?) {
                Log.v("Discover Activity", "Language Network Error", t)
            }

            override fun onResponse(call: Call<List<Language>>?, response: Response<List<Language>>?) {
                languages = response?.body()!!
                val languageStrings: ArrayList<String> = ArrayList()
                for (language in languages) languageStrings.add(language.englishName)
                val languageAdapter = ArrayAdapter<String>(this@DiscoverActivity, android.R.layout.simple_spinner_item, languageStrings)
                languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                originalLanguageSpinner.adapter = languageAdapter
            }
        })

        callGenres().enqueue(object : Callback<GenreResult> {
            override fun onFailure(call: Call<GenreResult>?, t: Throwable?) {
                Log.v("Discover Activity", "Genre Network Error", t)
            }

            override fun onResponse(call: Call<GenreResult>?, response: Response<GenreResult>?) {
                genres = response?.body()?.genres!!

                val genreStrings: ArrayList<String> = ArrayList()
                for (genre in genres) genreStrings.add(genre.name)
                val genreAdapter = ArrayAdapter<String>(this@DiscoverActivity, android.R.layout.simple_spinner_item, genreStrings)
                genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                genreSpinner.adapter = genreAdapter
            }
        })



        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        sheetBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    findViewById<View>(R.id.bg).visibility = View.GONE

                when (newState) {

                    BottomSheetBehavior.STATE_HIDDEN -> sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

                    BottomSheetBehavior.STATE_EXPANDED -> return

                    BottomSheetBehavior.STATE_COLLAPSED -> return

                    BottomSheetBehavior.STATE_DRAGGING -> return

                    BottomSheetBehavior.STATE_SETTLING -> return

                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                findViewById<View>(R.id.bg).visibility = View.VISIBLE
                findViewById<View>(R.id.bg).alpha = slideOffset
            }

        })

        bottomSheetPeek.setOnClickListener {
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val initialBundle: Bundle = Bundle()
        initialBundle.putString("genreId", null)
        initialBundle.putInt("year", 0)
        initialBundle.putInt("vote", -1)
        initialBundle.putString("language", null)
        initialBundle.putString("sortBy", null)
        initialBundle.putBoolean("adult", false)

//        val addedFragment = DiscoverFragment()
        currentFragment = DiscoverFragment()
        currentFragment!!.arguments = initialBundle

//        addedFragment.arguments = initialBundle
        supportFragmentManager.beginTransaction()
                .replace(R.id.discoverActivityPlaceholder, currentFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .disallowAddToBackStack()
                .commit()

        discoverButton.setOnClickListener {
            supportFragmentManager.beginTransaction().remove(currentFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
            currentFragment = null
            val newBundle: Bundle = Bundle()
            if (genreCheckBox.isChecked) {
                val genreString: String = genreSpinner.selectedItem.toString()
                var genreId: Long = 0
                for (genre in genres) {
                    if (genre.name == genreString)
                        genreId = genre.id
                }
                newBundle.putString("genreId", genreId.toString())
            } else {
                newBundle.putString("genreId", null)
            }
            if (yearCheckBox.isChecked) {
                val year: Int = yearSpinner.selectedItem.toString().toInt()
                newBundle.putInt("year", year)
            } else {
                newBundle.putInt("year", 0)
            }
            if (voteAverageCheckBox.isChecked) {
                val vote: Int = voteAverageSpinner.selectedItem.toString().substring(0, 1).toInt()
                newBundle.putInt("vote", vote)
            } else {
                newBundle.putInt("vote", 0)
            }
            if (originalLanguageCheckBox.isChecked) {
                val languageString: String = originalLanguageSpinner.selectedItem.toString()
                var languageCode: String = ""
                for (language in languages) {
                    if (language.englishName == languageString)
                        languageCode = language.iso6391
                }
                newBundle.putString("language", languageCode)
            } else {
                newBundle.putString("language", null)
            }
            if (sortCheckBox.isChecked) {
                val sortString: String = sortSpinner.selectedItem.toString().decapitalize().replace(" ", ".")
                newBundle.putString("sortBy", sortString)
            } else {
                newBundle.putString("sortBy", null)
            }
            if (adultSwitch.isChecked) {
                newBundle.putBoolean("adult", true)
            } else {
                newBundle.putBoolean("adult", false)
            }

            currentFragment = DiscoverFragment()
            currentFragment!!.arguments = newBundle
            supportFragmentManager.beginTransaction()
                    .replace(R.id.discoverActivityPlaceholder, currentFragment)
                    .disallowAddToBackStack()
                    .commit()
            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        }


    }

    private fun callLanguages(): Call<List<Language>> {
        val call = client.getLanguages(BuildConfig.TmdbApiKey)
        return call
    }

    private fun callGenres(): Call<GenreResult> {
        val call = client.getGenres(BuildConfig.TmdbApiKey)
        return call
    }
}
