package nz.qrl.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import io.noties.markwon.Markwon

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val content: LinearLayout = findViewById(R.id.content)
        Thread {
            val retriever = GitRetriever()
            val notices = retriever.notices()
            runOnUiThread {
                val markwon = Markwon.create(this)
                notices.forEach {
                    val textView = TextView(this)
                    markwon.setMarkdown(textView, it.content)
                    content.addView(textView)
                }
            }
        }.start()
    }
}