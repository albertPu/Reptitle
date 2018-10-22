package com.pxy.reptile.parse

import com.teamdev.jxbrowser.chromium.Browser
import org.jsoup.Jsoup
import java.util.*

class Resolver : BrowserListener, ProxyListener {

    var browser: Browser? = null
    var xsP520Provider: XSP520Provider? = null
    var timer: Timer? = null

    init {
        xsP520Provider = XSP520Provider()
    }

    override fun onProxy(url: String) {
        if (url.endsWith(".m3u8")) {
            xsP520Provider?.onParesedPlayUrl(url, browser, timer, this)
        }
    }

    override fun onBrowser(url: String?, html: String?) {
        println(url)
        val document = Jsoup.parse(html)
        if (url!!.contains(XSP520Provider.type)) {
            xsP520Provider?.onParseType(url, document, browser)
        }
        if (url!!.contains(XSP520Provider.detail)) {
            xsP520Provider?.onParseDetail(url, document, browser)
        }
        if (url.contains(XSP520Provider.play)) {
            System.out.println("播放界面 $url")
            if (timer == null) {
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        xsP520Provider?.onParesedPlayUrl("http://null", browser, timer, this@Resolver)
                    }
                }, (10 * 1000).toLong())
            }

        }
    }
}
