package com.pxy.reptile.parse

import com.pxy.reptile.databaseentity.MVideo
import com.pxy.reptile.isNull
import com.teamdev.jxbrowser.chromium.Browser
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.nodes.Document
import java.lang.Exception
import java.util.*

class XSP520Provider : IResolverProvider {


    companion object {
        const val type = "type"
        const val detail = "detail"
        const val play = "play"
    }

    private var baseUrl = "http://www.520xsp.com"
    private var xsP520MapBeans = ArrayList<XSP520MapBean>()
    private var homeItem = ArrayList<String>()
    private var itemPosition = 0
    private var currentDetailUrl: String? = ""
    private var nextTypePage: String? = ""
    private var currentTypeUrl = ""

    override fun onParseType(url: String?, doc: Document, browser: Browser?) {
        try {
            if (currentTypeUrl == url) return
            if (url!!.contains(type)) {
                System.out.println("************************************当前12个主界面--$url************************************")
                doc.select("div.index-area.clearfix").let { w ->
                    if (w.size > 0) {
                        currentTypeUrl = url
                        w[0].getElementsByTag("ul").let { n ->
                            if (n.size > 0) {
                                n[0].getElementsByTag("li").forEach { it ->
                                    val detailLink = it.getElementsByTag("a")[0].attr("href")
                                    it.getElementsByTag("img")[0].let {
                                        XSP520MapBean().apply {
                                            title = it.attr("alt")
                                            coverImageUrl = it.attr("data-original")
                                            detailUrl = baseUrl + detailLink
                                            xsP520MapBeans.add(this)
                                        }
                                    }
                                    homeItem.add(baseUrl + detailLink)
                                }
                            }
                        }
                    }
                }
                doc.getElementsByClass("pagelink_a").forEach {
                    if (it.text() == "下一页") {
                        nextTypePage = it.attr("href")
                    }
                }
                parseOneDetail(browser)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onParseDetail(url: String?, doc: Document, browser: Browser?) {
        val path = doc.getElementById("vlink_0") ?: return
        if (currentDetailUrl != url) {
            currentDetailUrl = url
            val pa = path.getElementsByTag("a")[0].attr("href")
            Thread.sleep(3000)
            browser?.loadURL(baseUrl + pa)
        }

    }


    override fun onParsePay(url: String?, doc: Document, browser: Browser?) {

    }

    override fun onParesedPlayUrl(playUrl: String, browser: Browser?, timer: Timer?, resolver: Resolver) {

        getXSP520MapBean(browser).apply {
            if (this?.playerUrlOne.isNullOrEmpty()) {
                this?.playerUrlOne = playUrl
            } else {
                this?.playerUrlTow = playUrl
            }
            if ((!this?.playerUrlOne.isNullOrEmpty() && !this?.playerUrlTow.isNullOrEmpty()) || playUrl == "http://null") {
                timer?.cancel()
                resolver.timer = null
                System.out.println("标题:${this?.title} \n 封面图片: \n ${this?.coverImageUrl} \n 播放地址一:${this?.playerUrlOne} \n 播放地址二:${this?.playerUrlTow}")
                store(this)
                itemPosition++
                parseOneDetail(browser)
            }
        }
    }

    private fun parseOneDetail(browser: Browser?) {
        Thread.sleep(3000)
        if (homeItem.size > itemPosition) {
            System.out.println("****************${homeItem[itemPosition]}")
            browser?.loadURL(homeItem[itemPosition])
        } else {
            itemPosition = 0
            xsP520MapBeans.clear()
            homeItem.clear()
            browser?.loadURL(baseUrl + nextTypePage)
        }
    }

    private fun getXSP520MapBean(browser: Browser?): XSP520MapBean? {
        System.out.println("当前位置$itemPosition 总大小:${xsP520MapBeans.size}")
        if (xsP520MapBeans.size > itemPosition) {
            return xsP520MapBeans[itemPosition]
        } else {
            itemPosition = 0
            xsP520MapBeans.clear()
            homeItem.clear()
            browser?.loadURL(baseUrl + nextTypePage)
        }
        return null
    }

    private fun store(bean: XSP520MapBean?) {
        if (bean == null) return
        transaction {
            val isNull = MVideo.select {
                MVideo.title.eq(bean.title)
            }.isNull("")

            if (isNull) {
                MVideo.insert {
                    it[MVideo.coverImageUrl] = bean.coverImageUrl
                    it[MVideo.playerUrlOne] = bean.playerUrlOne
                    it[MVideo.playerUrlTow] = bean.playerUrlTow
                    it[MVideo.title] = bean.title
                    it[MVideo.hostType] = 1
                }
            }
        }
    }


}