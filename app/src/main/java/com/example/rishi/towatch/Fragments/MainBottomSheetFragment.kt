package com.example.rishi.towatch.Fragments

/**
 * Created by rishi on 17/6/18.
 */
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.rishi.towatch.Api.ServiceGenerator
import com.example.rishi.towatch.BuildConfig
import com.example.rishi.towatch.POJOs.Configrations.Country
import com.example.rishi.towatch.R
import com.example.rishi.towatch.TmdbApi.TmdbApiClient
import kotlinx.android.synthetic.main.main_bottomsheet.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class BottomSheetFragment : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var client: TmdbApiClient
    private lateinit var countries: List<Country>
    private var countryName: ArrayList<String> = ArrayList()
    private lateinit var translations: List<String>
    private var languageSpinnerItems: ArrayList<String> = ArrayList()
    private lateinit var mSharedPreferences: SharedPreferences


    companion object {

        fun newInstance(): BottomSheetFragment {
            return BottomSheetFragment()
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_bottomsheet, container, false)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSharedPreferences = activity?.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)!!


        client = ServiceGenerator.createService(TmdbApiClient::class.java)

        regionSpinner.onItemSelectedListener = this
        languageSpinner.onItemSelectedListener = this

        callCountries().enqueue(object : Callback<List<Country>> {
            override fun onFailure(call: Call<List<Country>>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<List<Country>>?, response: Response<List<Country>>?) {
                countries = response?.body()!!
                addCountries()
            }
        })

        callTranslations().enqueue(object : Callback<List<String>> {
            override fun onFailure(call: Call<List<String>>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>?) {
                translations = response?.body()!!
                addTranslations()
            }
        })

        defaultButton.setOnClickListener {
            val sharedPreferenceEditor: SharedPreferences.Editor = mSharedPreferences.edit()
            sharedPreferenceEditor.putString("region","US")
            sharedPreferenceEditor.putString("language","en-US")
            sharedPreferenceEditor.commit()
            this.dismiss()
        }

        saveButton.setOnClickListener {

            val languageSelected = languageSpinner.selectedItem.toString()
            var languageQuery: String = ""

            for (translation in translations) {
                val codes = translation.split("-")
                val locale: Locale = Locale(codes[0], codes[1])
                val spinnerItem = locale.displayLanguage + " (" + locale.displayCountry + ")"
                if (languageSelected == spinnerItem) {
                    languageQuery = translation
                }
            }

            val regionSelected = regionSpinner.selectedItem.toString()
            var regionQuery: String = ""
            for (country in countries) {
                if (country.englishName == regionSelected) {
                    regionQuery = country.iso31661
                }
            }

            val sharedPreferenceEditor: SharedPreferences.Editor = mSharedPreferences.edit()
            sharedPreferenceEditor.putString("region", regionQuery)
            sharedPreferenceEditor.putString("language", languageQuery)
            sharedPreferenceEditor.commit()
            this.dismiss()
        }
    }

    private fun addTranslations() {
        val code = mSharedPreferences.getString("language", "en-US")
        var position: Int = 0
        var selectedPosition:Int = 0
        for (translation in translations) {
            if (code == translation) {
                selectedPosition = position
            }
            val codes = translation.split("-")
            val locale: Locale = Locale(codes[0], codes[1])
            val spinnerItem = locale.displayLanguage + " (" + locale.displayCountry + ")"
            languageSpinnerItems.add(spinnerItem)
            position++
        }
        val languageAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, languageSpinnerItems)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = languageAdapter
        languageSpinner.setSelection(selectedPosition, true)
    }

    private fun addCountries() {
        val code = mSharedPreferences.getString("region", "US")
        var position: Int = 0
        var selectedPosition:Int = 0
        for (country in countries) {
            if (code == country.iso31661) {
                selectedPosition = position
            }
            countryName.add(country.englishName)
            position++
        }
        val countryAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, countryName)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = countryAdapter
        regionSpinner.setSelection(selectedPosition, true)
    }

    private fun callCountries(): Call<List<Country>> {
        val call = client.getCountries(BuildConfig.TmdbApiKey)
        return call
    }

    private fun callTranslations(): Call<List<String>> {
        val call = client.getTranslations(BuildConfig.TmdbApiKey)
        return call
    }


}// Required empty public constructor