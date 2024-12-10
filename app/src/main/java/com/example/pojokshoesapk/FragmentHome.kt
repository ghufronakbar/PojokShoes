package com.example.pojokshoesapk

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment

class FragmentHome : Fragment() {

    private lateinit var viewFlipper: ViewFlipper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewFlipper = view.findViewById(R.id.slider)

        viewFlipper.setOnClickListener {
            viewFlipper.showNext()
        }

        // Deep Clean
        val imgDeepClean: View = view.findViewById(R.id.imgdeep)
        val textDeepClean: View = view.findViewById(R.id.tv4)
        val deepCleanCard = view.findViewById<View>(R.id.carddeepclean)

        deepCleanCard.setOnClickListener {
            val intent = Intent(activity, ActivityFormDeepClean::class.java)
            startActivity(intent)
        }

        imgDeepClean.setOnClickListener {
            val intent = Intent(activity, ActivityFormDeepClean::class.java)
            startActivity(intent)
        }

        textDeepClean.setOnClickListener {
            val intent = Intent(activity, ActivityFormDeepClean::class.java)
            startActivity(intent)
        }

        // Keranjang
        val imgKeranjang: View = view.findViewById(R.id.homekeranjang)
        imgKeranjang.setOnClickListener {
            val intent = Intent(activity, ActivityKeranjang::class.java)
            startActivity(intent)
        }

        // Fast Clean
        val fastCleanCard = view.findViewById<View>(R.id.cardfastclean)
        val imgFastClean: View = view.findViewById(R.id.imgfast)
        val textFastClean: View = view.findViewById(R.id.tv3)

        fastCleanCard.setOnClickListener {
            val intent = Intent(activity, ActivityFormFastClean::class.java)
            startActivity(intent)
        }

        imgFastClean.setOnClickListener {
            val intent = Intent(activity, ActivityFormFastClean::class.java)
            startActivity(intent)
        }

        textFastClean.setOnClickListener {
            val intent = Intent(activity, ActivityFormFastClean::class.java)
            startActivity(intent)
        }

        // Reglue
        val reglueCard = view.findViewById<View>(R.id.cardreglue)
        val imgReglue: View = view.findViewById(R.id.imgreglue)
        val textReglue: View = view.findViewById(R.id.tv5)

        reglueCard.setOnClickListener {
            val intent = Intent(activity, ActivityFormReglue::class.java)
            startActivity(intent)
        }

        imgReglue.setOnClickListener {
            val intent = Intent(activity, ActivityFormReglue::class.java)
            startActivity(intent)
        }

        textReglue.setOnClickListener {
            val intent = Intent(activity, ActivityFormReglue::class.java)
            startActivity(intent)
        }

        // Recolor
        val recolorCard = view.findViewById<View>(R.id.cardrecolor)
        val imgRecolor: View = view.findViewById(R.id.imgrecolor)
        val textRecolor: View = view.findViewById(R.id.tv6)

        recolorCard.setOnClickListener {
            val intent = Intent(activity, ActivityFormRecolor::class.java)
            startActivity(intent)
        }

        imgRecolor.setOnClickListener {
            val intent = Intent(activity, ActivityFormRecolor::class.java)
            startActivity(intent)
        }

        textRecolor.setOnClickListener {
            val intent = Intent(activity, ActivityFormRecolor::class.java)
            startActivity(intent)
        }

        return view
    }
}
