package com.android.bdkstock.screens.main

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.bdkstock.R
import com.android.bdkstock.databinding.ActivityMainBinding
import com.android.model.utils.Const.FLAG_SIGNED_IN
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

   private lateinit var binding: ActivityMainBinding
   private lateinit var navController: NavController

   private val TAG = this.javaClass.simpleName

   private val topLevelDestinations = setOf(getSignInId(), getActionsId())

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding.root)

      val navHost = supportFragmentManager
         .findFragmentById(R.id.fragment_main_container) as NavHostFragment
      navController = navHost.navController

      onNavControllerActivated(navController)

      supportActionBar?.elevation = 0f

      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

      if (savedInstanceState == null) prepareRootNavGraph(checkSign(), navController)
      else prepareRestorationRootNavGraph(savedInstanceState, navController)

      NavigationUI.setupActionBarWithNavController(this, navController)

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
         supportActionBar?.title = prepareTitle(destination.label, arguments)
         supportActionBar?.setDisplayHomeAsUpEnabled(!isStartDestination(destination))
      }

   private fun prepareRootNavGraph(isSignedIn: Boolean, navController: NavController) {
      val graph = navController.navInflater.inflate(getMainGraphId())
      graph.setStartDestination(if (isSignedIn) getActionsId() else getSignInId())
      navController.graph = graph
   }

   private fun checkSign(): Boolean = intent.getBooleanExtra(FLAG_SIGNED_IN, false)
   private fun getMainGraphId(): Int = R.navigation.main_graph
   private fun getSignInId(): Int = R.id.signInFragment
   private fun getActionsId(): Int = R.id.actionsFragment

   override fun onSupportNavigateUp(): Boolean {
      return navController.navigateUp() || super.onSupportNavigateUp()
   }

   /**
    * Saves all navigation controller state to a Bundle.
    * State may be restored from a bundle returned from this method by calling
    * NavController.restoreState(Bundle). Saving controller state
    * is the responsibility of a NavHost.
    */
   override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
      super.onSaveInstanceState(outState, outPersistentState)
      outState.putBundle(NAVIGATION_KEY, navController.saveState())
   }

   private fun isStartDestination(destination: NavDestination?): Boolean {
      if (destination == null) return false
      val graph = destination.parent ?: return false
      val startDestinations = topLevelDestinations + graph.startDestinationId
      return startDestinations.contains(destination.id)
   }

   private fun prepareTitle(label: CharSequence?, arguments: Bundle?): String {
      if (label == null) return ""
      val title = StringBuffer()
      val fillInPattern = Pattern.compile("\\{(.+?)\\}")
      val matcher = fillInPattern.matcher(label)
      while (matcher.find()) {
         val argName = matcher.group(1)
         if (arguments != null && arguments.containsKey(argName)) {
            matcher.appendReplacement(title, "")
            title.append(arguments[argName].toString())
         } else {
            throw IllegalArgumentException(
               "Could not find $argName in $arguments to fill label $label"
            )
         }
      }
      matcher.appendTail(title)
      return title.toString()
   }

   private fun prepareRestorationRootNavGraph(
      savedInstState: Bundle,
      navController: NavController
   ) {
      val stateRestoration = savedInstState.getBundle(NAVIGATION_KEY)
      navController.restoreState(stateRestoration)
   }

   override fun onBackPressed() {
      if (isStartDestination(navController.currentDestination)) {
         onBackPressedDispatcher.onBackPressed()
      } else {
         navController.popBackStack()
      }
   }

   private companion object {
      const val NAVIGATION_KEY = "navigation_key"
   }
}