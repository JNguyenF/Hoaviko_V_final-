package mg.itu.hoaviko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import mg.itu.hoaviko.ui.HoavikoApp
import mg.itu.hoaviko.ui.theme.HoavikoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HoavikoTheme {
                HoavikoApp()
            }
        }
    }
}