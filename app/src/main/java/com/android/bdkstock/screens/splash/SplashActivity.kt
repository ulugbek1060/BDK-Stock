package com.android.bdkstock.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.bdkstock.databinding.ActivitySplashBinding
import com.android.bdkstock.screens.main.MainActivity
import com.android.model.utils.Const.FLAG_SIGNED_IN
import com.android.model.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

   private val viewModel by viewModels<SplashViewModel>()
   private lateinit var binding: ActivitySplashBinding

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivitySplashBinding.inflate(layoutInflater)
      setContentView(binding.root)

//      renderAnimations()
      launchToMainActivity()
   }

   private fun launchToMainActivity() {
      viewModel.launchMainScreen.observeEvent(this) {
         launchMainScreen(it)
      }
   }

   private fun launchMainScreen(isSignedIn: Boolean) {
      val intent = Intent(this, MainActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      intent.putExtra(FLAG_SIGNED_IN, isSignedIn)
      startActivity(intent)
   }

   private fun renderAnimations() {
      binding.ivLogo.alpha = 0f
      binding.ivLogo.animate()
         .alpha(0.7f)
         .setDuration(1000)
         .start()

      binding.ivLogo.alpha = 0f
      binding.ivLogo.animate()
         .alpha(1f)
         .setStartDelay(500)
         .setDuration(1000)
         .start()
   }

}