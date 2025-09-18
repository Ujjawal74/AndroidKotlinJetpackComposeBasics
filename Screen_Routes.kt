package com.example.myapp

sealed class Screen(val route: String, val title: String, val icon: Int) {
    object Home : Screen("home", "Home", R.drawable.home)
    object Settings : Screen("settings", "Settings", R.drawable.settings)

    object  About : Screen("about_us", "About Us", R.drawable.about)

    object Updates : Screen("updates", "Updates", R.drawable.update_centre)
    object Profile : Screen("profile/{name}/{age}", "Profile", R.drawable.profile_circle_thin) {
        fun createRoute(name: String, age: Int) = "profile/$name/$age"
    }
}
