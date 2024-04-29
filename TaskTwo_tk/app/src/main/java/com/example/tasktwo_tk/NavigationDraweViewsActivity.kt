package com.example.tasktwo_tk

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.tasktwo_tk.databinding.ActivityNavigationDraweViewsBinding

class NavigationDraweViewsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavigationDraweViewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationDraweViewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarNavigationDraweViews.toolbar)

        binding.appBarNavigationDraweViews.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawe_views)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)




        //use the nav view to navigate beteen screens
        binding.navView.setNavigationItemSelectedListener {  menuItem ->
            when (menuItem.itemId){

                R.id.nav_home -> {
                    //go to that screen
                    Toast.makeText(this@NavigationDraweViewsActivity, "Home Feature coming soon", Toast.LENGTH_SHORT).show()
                    //startActivity(intent)
                    true
                }

                R.id.nav_OtherMain -> {
                    //go to that screen
                    val intent = Intent(this@NavigationDraweViewsActivity,MainActivity::class.java )
                    startActivity(intent)
                    true
                }

                R.id.nav_Login -> {
                    //go to that screen
                    val intent = Intent(this@NavigationDraweViewsActivity,Login::class.java )
                    startActivity(intent)
                    true
                }

                R.id.nav_Register -> {
                    //go to that screen
                    val intent = Intent(this@NavigationDraweViewsActivity,Register::class.java )
                    startActivity(intent)
                    true
                }

                R.id.nav_timer -> {
                    //go to that screen
                    //val intent = Intent(this@MainActivity,Timer::class.java )
                    Toast.makeText(this@NavigationDraweViewsActivity, "Feature coming soon", Toast.LENGTH_SHORT).show()
                    //startActivity(intent)
                    true
                }

                R.id.nav_graph -> {
                    //go to that screen
                    Toast.makeText(this@NavigationDraweViewsActivity, "Feature coming soon", Toast.LENGTH_SHORT).show()
                    //startActivity(intent)
                    true
                }

                R.id.nav_calcs -> {
                    //go to that screen
                    Toast.makeText(this@NavigationDraweViewsActivity, "Feature coming soon", Toast.LENGTH_SHORT).show()
                    //startActivity(intent)
                    true
                }

                R.id.nav_dates -> {
                    //go to that screen
                    //val intent = Intent(this@MainActivity,Dates::class.java )
                    Toast.makeText(this@NavigationDraweViewsActivity, "Feature coming soon", Toast.LENGTH_SHORT).show()
                    //startActivity(intent)
                    true
                }


                R.id.nav_Camera -> {
                    //go to that screen
                    val intent = Intent(this@NavigationDraweViewsActivity,Kamera::class.java )
                    startActivity(intent)
                    true
                }

                R.id.nav_Adv_Camera -> {
                    //go to that screen
                    val intent = Intent(this@NavigationDraweViewsActivity,KameraTwo::class.java )
                    startActivity(intent)
                    true
                }





                else -> false
            }
        }//end_setNavigationItemSelectedListener


    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation_drawe_views, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawe_views)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}