package se.linerotech.module202.project4.recipe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import se.linerotech.module202.project4.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val host = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        host.navController.setGraph(R.navigation.nav_graph)
    }
}
