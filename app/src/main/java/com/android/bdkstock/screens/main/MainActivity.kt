package com.android.bdkstock.screens.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.android.bdkstock.R
import com.android.bdkstock.databinding.ActivityMainBinding
import com.android.model.utils.Const.FLAG_SIGNED_IN
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

   private lateinit var binding: ActivityMainBinding
   private lateinit var navController: NavController
   private val TAG = this.javaClass.simpleName

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

      val navHost =
         supportFragmentManager.findFragmentById(R.id.fragment_main_container) as NavHostFragment
      navController = navHost.navController

      onNavControllerActivated(navController)
      prepareRootNavGraph(checkSign(), navController)

      supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)
   }

   private val fragmentListener = object : FragmentLifecycleCallbacks() {
      override fun onFragmentViewCreated(
         fm: FragmentManager,
         f: Fragment,
         v: View,
         savedInstanceState: Bundle?
      ) {
         super.onFragmentViewCreated(fm, f, v, savedInstanceState)
         if (f is ActionsFragment || f is NavHostFragment) return
         onNavControllerActivated(f.findNavController())
      }
   }

   private fun onNavControllerActivated(navController: NavController) {
      if (this.navController == navController) return
      this.navController.removeOnDestinationChangedListener(destinationListener)
      navController.addOnDestinationChangedListener(destinationListener)
      this.navController = navController
   }

   private val destinationListener =
      NavController.OnDestinationChangedListener { _, destination, arguments ->
         Log.d(TAG, "destinationListener: $destination")
      }

   private fun prepareRootNavGraph(isSignedIn: Boolean, navController: NavController) {
      val graph = navController.navInflater.inflate(getMainGraphId())
      graph.setStartDestination(if (isSignedIn) getActivitiesId() else getSignInId())
      navController.graph = graph
   }

   private fun checkSign(): Boolean = intent.getBooleanExtra(FLAG_SIGNED_IN, false)
   private fun getMainGraphId(): Int = R.navigation.main_graph
   private fun getSignInId(): Int = R.id.signInFragment
   private fun getActivitiesId(): Int = R.id.actionsFragment

   override fun onSupportNavigateUp(): Boolean {
      return navController.navigateUp() || super.onSupportNavigateUp()
   }
}