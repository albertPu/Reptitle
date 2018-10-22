package com.pxy.reptile.parse

import com.teamdev.jxbrowser.chromium.Browser
import org.jsoup.nodes.Document
import java.util.*

interface IResolverProvider {
    fun onParseType(url: String?, doc: Document, browser: Browser?)
    fun onParseDetail(url: String?, doc: Document, browser: Browser?)
    fun onParsePay(url: String?, doc: Document, browser: Browser?)
    fun onParesedPlayUrl(playUrl: String, browser: Browser?,timer: Timer?)
}