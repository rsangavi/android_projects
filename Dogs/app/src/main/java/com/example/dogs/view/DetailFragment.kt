package com.example.dogs.view

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.telephony.SmsManager
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.dogs.R
import com.example.dogs.databinding.FragmentDetailBinding
import com.example.dogs.databinding.SendSmsDialogBinding
import com.example.dogs.model.DogBreed
import com.example.dogs.model.DogPalette
import com.example.dogs.model.SmsInfo
import com.example.dogs.viewmodel.DogDetailsViewModel

class DetailFragment : Fragment() {
    private var dogUuid = 0
    private lateinit var dogDetailsViewModel: DogDetailsViewModel
    private lateinit var bindingFragmentDetail: FragmentDetailBinding

    private var sendSmsStarted = false
    private var currentDog: DogBreed? = null
    private val TAG = "DetailFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView:")
        setHasOptionsMenu(true)
        bindingFragmentDetail =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return bindingFragmentDetail.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dogDetailsViewModel = ViewModelProviders.of(this).get(DogDetailsViewModel::class.java)
        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }
        dogDetailsViewModel.getDetailsData(dogUuid)
        observeViewModel(view)
    }

    private fun observeViewModel(view: View) {
        dogDetailsViewModel.dogBreedList.observe(viewLifecycleOwner, { dogBreed ->
            dogBreed?.let {
                currentDog = it
                bindingFragmentDetail.dog = dogBreed
                it.imageUrl?.let {
                    setupBackgroundColor(it)
                }
            }
        })
    }

    private fun setupBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            val myPalette = DogPalette(intColor)
                            bindingFragmentDetail.palette = myPalette
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: ${item.itemId}")
        when (item.itemId) {
            R.id.action_send -> {
                sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()
            }
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this Dog breed")
                intent.putExtra(Intent.EXTRA_TEXT, "${currentDog?.dogBreed} is breed for ${currentDog?.bredFor}")
                intent.putExtra(Intent.EXTRA_STREAM, currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent, "Share with"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        Log.d(TAG, "onPermissionResult: $permissionGranted")
        if (permissionGranted && sendSmsStarted) {
            context?.let {

                val smsInfo = SmsInfo(
                    "",
                    "${currentDog?.dogBreed} is breed for ${currentDog?.bredFor}",
                    currentDog?.imageUrl
                )
                val dialogBindingView = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    layoutInflater,
                    R.layout.send_sms_dialog,
                    null,
                    false
                )

                AlertDialog.Builder(it)
                    .setView(dialogBindingView.root)
                    .setPositiveButton("Send SMS") { _, _ ->
                        if(!dialogBindingView.message.text.isNullOrEmpty()) {
                            smsInfo.to = PhoneNumberUtils.formatNumber(dialogBindingView.recipient.text.toString())
                            sendSms(smsInfo)
                        }
                    }.setNegativeButton("Cancel") { _, _ ->

                    }.show()
                dialogBindingView.smsInfo = smsInfo
            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        SmsManager.getDefault().sendTextMessage(smsInfo.to, null, smsInfo.message, pendingIntent, null)

    }
}