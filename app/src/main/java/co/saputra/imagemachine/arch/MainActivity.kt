package co.saputra.imagemachine.arch

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.saputra.imagemachine.Constants.REQUEST_CODE
import co.saputra.imagemachine.R
import co.saputra.imagemachine.util.ManagePermissions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}