package com.example.expertgoggles

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class Texting: AppCompatActivity(), TextToSpeech.OnInitListener {
    lateinit var textToSpeech: TextToSpeech
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    private var mUserList: ArrayList<Users>? = null
    private var mAdapter: UserListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.text)

        initList();

        val spinnerCountries = findViewById<Spinner>(R.id.spinner_countries)

        mAdapter = UserListAdapter(this, mUserList)
        spinnerCountries.adapter = mAdapter

        spinnerCountries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val clickedItem: Users = parent.getItemAtPosition(position) as  Users
                val clickedCountryName: String? = clickedItem.getName()
                Toast.makeText(
                    this@Texting,
                    "$clickedCountryName selected",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()

        var feed: EditText = findViewById(R.id.editText)  // 'feed' store text from textbox

        textToSpeech = TextToSpeech(this, this)
        var btn: LottieAnimationView = findViewById(R.id.speak)
        btn.setOnClickListener {

            var text: String = "This is a test"
            text= feed.text.toString()
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            sendText()
            }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
                var res: Int = 1
                res = textToSpeech.setLanguage(Locale("hin"))

            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "language not supported", Toast.LENGTH_LONG).show()
            }

        }
    }

    fun sendText()
    {
        var feed: EditText = findViewById(R.id.editText)
        val mtext= feed.text.toString()
        val user =userText()
        user.setText(mtext)

        // we are use add value event listener method
        // which is called with database reference.
        val postListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                databaseReference.push().setValue(user)

                // after adding this data we are showing toast message.
                Toast.makeText(this@Texting, "added", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(this@Texting, "Fail to add data $error", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        databaseReference.addListenerForSingleValueEvent((postListener))
    }

    private fun initList() {

        val inidividual_user = Users()
        inidividual_user.userItem("me",R.drawable.cloud2)

        mUserList = ArrayList()
        mUserList!!.add(inidividual_user)
        mUserList!!.add(inidividual_user)
        mUserList!!.add(inidividual_user)

    }

}