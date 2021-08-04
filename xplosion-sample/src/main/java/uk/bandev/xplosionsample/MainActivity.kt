package uk.bandev.xplosionsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uk.bandev.xplosionsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val heart = binding.heart
        val heartAnimator = binding.heartAnimator
        var isLiked = false

        heart.setOnClickListener {
            if (isLiked) {
                heart.setImageResource(R.drawable.ic_heart_outline)
            } else {
                heart.setImageResource(R.drawable.ic_heart_red)
                heartAnimator.likeAnimation()
            }
            isLiked = !isLiked
        }
    }
}