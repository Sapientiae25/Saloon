package com.example.saloon

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FilterFragment : Fragment(){

    private lateinit var accountItem : AccountItem
    private lateinit var filterItem: FilterItem
    private var genderCount = 0
    private var lengthCount = 0
    private lateinit var rgFilterGender : RadioGroup
    private lateinit var llFilterLength : LinearLayout
    private lateinit var tvSort : TextView
    private lateinit var tvFilterGender : TextView
    private lateinit var tvFilterLength : TextView
    private lateinit var cbAllLength : CheckBox
    private lateinit var btnClear : AppCompatButton
    private lateinit var btnApply : AppCompatButton
    private lateinit var cancelFilter : FloatingActionButton
    private lateinit var rgSort : RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_filter, container, false)
        filterItem = (activity as DefaultActivity).accountItem.filterItem
        tvSort = rootView.findViewById(R.id.tvSort)
        tvFilterGender = rootView.findViewById(R.id.tvFilterGender)
        tvFilterLength = rootView.findViewById(R.id.tvFilterLength)
        cbAllLength = rootView.findViewById(R.id.cbAllLength)
        btnClear = rootView.findViewById(R.id.btnClear)
        btnApply = rootView.findViewById(R.id.btnApply)
        cancelFilter = rootView.findViewById(R.id.cancelFilter)
        (activity as DefaultActivity).title = "Filter"
        llFilterLength = rootView.findViewById(R.id.llFilterLength)
        rgSort = rootView.findViewById(R.id.rgSort)
        rgFilterGender = rootView.findViewById(R.id.rgFilterGender)
        lengthCount = llFilterLength.childCount

        for (i in 1 until lengthCount){
            val child = llFilterLength.getChildAt(i) as CheckBox
            child.setOnClickListener { if (child.isChecked){filterItem.length.add(i)} else {filterItem.length.remove(i)};lengthCheck()} }
        tvSort.setOnClickListener { tvSort.visibility = if (tvSort.visibility == View.GONE) View.VISIBLE else View.GONE}
        tvFilterLength.setOnClickListener { llFilterLength.visibility = if (llFilterLength.visibility == View.GONE) View.VISIBLE
        else View.GONE}
        tvFilterGender.setOnClickListener { rgFilterGender.visibility = if (rgFilterGender.visibility == View.GONE) View.VISIBLE
        else View.GONE}
        cbAllLength.setOnClickListener { filterItem.length.clear();lengthCheck()}
        btnClear.setOnClickListener{cbAllLength.performClick();(rgFilterGender.getChildAt(0) as RadioButton).isChecked = true}
        cancelFilter.setOnClickListener { view ->
            view.findNavController().popBackStack() }
        btnApply.setOnClickListener { view ->
            allCheck()
            val checked = rgFilterGender.findViewById<RadioButton>(rgFilterGender.checkedRadioButtonId)
            (activity as DefaultActivity).accountItem.filterItem.gender = rgFilterGender.indexOfChild(checked)
            val bundle = bundleOf(Pair("back",1))
            view.findNavController().navigate(R.id.action_filterFragment_to_saloonFragment,bundle)
        }
        resetChecked()
        return rootView }
    private fun resetChecked(){
        val length = filterItem.length
        val sort = filterItem.sort
        if (length.size == lengthCount-1 || length.size == 0){(llFilterLength.getChildAt(0) as CheckBox).isChecked=true}
        else { for (x in length){ (llFilterLength.getChildAt(x) as CheckBox).isChecked=true }}
        rgFilterGender.check((rgFilterGender.getChildAt(sort) as RadioButton).id)
        rgSort.check((rgSort.getChildAt(sort) as RadioButton).id) }
    private fun allCheck(){
        if (filterItem.length.size == 0) filterItem.length = (1 until lengthCount).toMutableSet()}
    private fun lengthCheck(){
        val length = filterItem.length
        if (length.size == lengthCount-1 || length.size == 0){
            (llFilterLength.getChildAt(0) as CheckBox).isChecked=true
            for (i in 1 until lengthCount){(llFilterLength.getChildAt(i) as CheckBox).isChecked=false} ;filterItem.length.clear()}
        else{(llFilterLength.getChildAt(0) as CheckBox).isChecked=false} }
}