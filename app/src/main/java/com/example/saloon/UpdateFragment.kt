package com.example.saloon

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.util.*

class UpdateFragment : Fragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var tvAddress: TextView
    private lateinit var tvNumber: TextView
    private lateinit var tvName: TextView
    private lateinit var tvOpens: TextView
    private lateinit var accountItem: AccountItem
    private lateinit var tvCloses: TextView
    private lateinit var tvChangeImage: TextView
    private lateinit var tvChangePassword: EditText
    private lateinit var llNumber: LinearLayout
    private lateinit var llName: LinearLayout
    private lateinit var llOpens: LinearLayout
    private lateinit var llCloses: LinearLayout
    private lateinit var llPassword: LinearLayout
    private lateinit var llAddress: LinearLayout
    private var back = false
    private var hour = 0
    private var minute = 0
    private var savedHour = 0
    private var savedMinute = 0
    private var open = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
        }
    }

    private fun updateChanges(){
        val addressItem = accountItem.addressItem
        tvAddress.text = if (addressItem?.address.isNullOrEmpty()){""}else{getString(R.string.address_ph,addressItem?.address,addressItem?.postcode)}
        tvName.text = accountItem.name
        tvNumber.text = accountItem.number
        tvChangePassword.setText(accountItem.password ?: "")
        tvOpens.text = accountItem.open ?: ""
        tvCloses.text = accountItem.close ?: ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<AccountItem>("accountItem")?.let {
            accountItem = it
            back = true
        }
        arguments?.getParcelable<AccountItem>("initAccount")?.let {
            val url = "http://192.168.1.102:8012/saloon/details.php"
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener { response ->
                    println(response)
                    val obj = JSONObject(response)
                    val name = obj.getString("name")
                    val number = obj.getString("number")
                    val password = obj.getString("password")
                    val open = obj.getString("open")
                    val close = obj.getString("close")
                    val city = obj.getString("city")
                    val postcode = obj.getString("postcode")
                    val country = obj.getString("country")
                    val address  = obj.getString("address")
                    val addressItem = AddressItem(city, postcode, country, address)
                    accountItem = AccountItem(it.id,name,password,number,open,close,addressItem)
                    updateChanges()
                },
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["account_id"] = it.id
                    return params
                }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =  inflater.inflate(R.layout.fragment_update, container, false)

        llAddress = rootView.findViewById(R.id.llAddress)
        tvAddress = rootView.findViewById(R.id.tvAddress)
        llNumber = rootView.findViewById(R.id.llNumber)
        tvNumber = rootView.findViewById(R.id.tvNumber)
        llName = rootView.findViewById(R.id.llName)
        tvName = rootView.findViewById(R.id.tvName)
        llOpens = rootView.findViewById(R.id.llOpens)
        tvOpens = rootView.findViewById(R.id.tvOpens)
        llCloses = rootView.findViewById(R.id.llCloses)
        llPassword = rootView.findViewById(R.id.llPassword)
        tvCloses = rootView.findViewById(R.id.tvCloses)
        tvChangeImage = rootView.findViewById(R.id.tvChangeImage)
        tvChangePassword = rootView.findViewById(R.id.tvChangePassword)

        if (back){updateChanges()}

        llAddress.setOnClickListener {
            val fm = parentFragmentManager
            fm.commit { replace(R.id.fragmentContainer,LocationFragment.newInstance(accountItem))
                        addToBackStack("")}
             }
        llNumber.setOnClickListener {
            val fm = parentFragmentManager
            fm.commit { replace(R.id.fragmentContainer,PhoneNumberFragment.newInstance(accountItem)) }
            }
        llName.setOnClickListener {
            val fm = parentFragmentManager
            fm.commit { replace(R.id.fragmentContainer,SaloonNameFragment.newInstance(accountItem)) }
             }
        llOpens.setOnClickListener {
            open = true
            TimePickerDialog(context,this,hour,minute,true).show()
            }
        llCloses.setOnClickListener {
            open = false
            TimePickerDialog(context,this,hour,minute,true).show()
            }
        llPassword.setOnClickListener {
            val fm = parentFragmentManager
            fm.commit { replace(R.id.fragmentContainer,PasswordFragment.newInstance(accountItem)) }
            }
        return rootView
    }

    companion object {
        fun newInstance(param1: AccountItem) =
            UpdateFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("accountItem", param1)
                }
            }
        fun initAccount(param1: AccountItem) =
            UpdateFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("initAccount", param1)
                }
            }

    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        savedHour = hour
        savedMinute = minute
        if (open){
            val timeText = getString(R.string.clock,savedHour,savedMinute)
            tvOpens.text = timeText
            val url = "http://192.168.1.102:8012/saloon/open.php"
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener {},
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["account_id"] = accountItem.id
                    params["time"] = timeText
                    return params
                }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }else{
            val timeText = getString(R.string.clock,savedHour,savedMinute)
            tvCloses.text = timeText
            val url = "http://192.168.1.102:8012/saloon/close.php"
            val stringRequest = object : StringRequest(
                Method.POST, url, Response.Listener {},
                Response.ErrorListener { volleyError -> println(volleyError.message) }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["account_id"] = accountItem.id
                    params["time"] = timeText
                    return params
                }}
            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }
}