package nz.qrl.app

import android.util.Base64
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Tree(val path: String, val url: String)

@Serializable
data class Trees(val tree: List<Tree>)

@Serializable
data class Blob(val content: String)

data class Notice(val title: String, val date: String, val author: String, val content: String)

class GitRetriever {

    private val json: Json = Json {
        ignoreUnknownKeys = true
    }

    private fun noticesTrees(): Trees {
        val master = master()
        val treeItem: Tree = master.tree.find { treeObject -> treeObject.path == "_notices" }
            ?: throw IllegalStateException("Repo missing notices")
        return trees(treeItem.url)
    }

    private fun master(): Trees {
        return trees(Constants.MASTER_URL)
    }

    fun notices(): ArrayList<Notice> {
        val notices: ArrayList<Notice> = ArrayList()
        val noticesTrees: Trees = noticesTrees()
        for (it in noticesTrees.tree) {
            if (!it.path.endsWith(".md")) continue
            val treeObject: Blob = blob(it.url)
            val content: String = treeObject.content
            notices.add(parseNotice(content) ?: continue)
        }
        return notices
    }

    private fun parseNotice(rawContent: String): Notice? {
        val data = String(Base64.decode(rawContent, Base64.DEFAULT))
        val lines: List<String> = data.split('\n')
        var heading = false
        var title: String? = null
        var date: String? = null
        var author: String? = null
        val contentBuilder = StringBuilder()
        for (it in lines) {
            if (it == "---") {
                heading = !heading
            } else {
                if (heading) {
                    val parts: List<String> = it.split(": ")
                    if (parts.size >= 2) {
                        val name: String = parts[0]
                        val value: String = parts[1]
                        when (name) {
                            "title" -> title = value
                            "author" -> author = value
                            "date" -> date = value
                        }
                    }
                } else {
                    contentBuilder.append(it).append("\n")
                }
            }
        }
        val outputContent: String = contentBuilder.toString()
        if (title == null || date == null || author == null) return null
        return Notice(title, date, author, outputContent)
    }

    private fun blob(url: String): Blob {
        return json.decodeFromString(NetUtil.get(url))
    }

    private fun trees(url: String): Trees {
        return json.decodeFromString(NetUtil.get(url))
    }

}