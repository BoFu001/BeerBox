package com.bofu.beerbox.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bofu.beerbox.R
import com.bofu.beerbox.databinding.DialogBottomBinding
import com.bofu.beerbox.extensions.loadImage

open class BaseActivity : AppCompatActivity() {

     fun showDialog(name: String, tag: String, description: String, url: String){


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogBottomBinding = DialogBottomBinding.inflate(layoutInflater)
        builder.setView(dialogBottomBinding.root)

        dialogBottomBinding.dialogBottomName.text = name
        dialogBottomBinding.dialogBottomTagline.text = tag
        dialogBottomBinding.dialogBottomDescription.text = description
        dialogBottomBinding.dialogBottomImg.loadImage(url)

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_bottom)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        dialog.show()
    }
}