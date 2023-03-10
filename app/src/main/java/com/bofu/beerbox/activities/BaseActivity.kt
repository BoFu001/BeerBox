package com.bofu.beerbox.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bofu.beerbox.R
import com.bofu.beerbox.databinding.DialogBottomBinding
import com.bofu.beerbox.extensions.loadImage

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        windowSetup()
    }

    private fun windowSetup(){
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
    fun showDialog(id: Short, name: String, tag: String, description: String, url: String){


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogBottomBinding = DialogBottomBinding.inflate(layoutInflater)
        builder.setView(dialogBottomBinding.root)

        dialogBottomBinding.dialogBottomName.text = id.toString() + " " + name
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