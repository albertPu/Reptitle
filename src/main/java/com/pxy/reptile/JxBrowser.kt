package com.pxy.reptile

import com.pxy.reptile.intercept.HttpProxyInterceptInitializer
import com.pxy.reptile.intercept.HttpProxyInterceptPipeline
import com.pxy.reptile.intercept.common.FullResponseIntercept
import com.pxy.reptile.parse.BrowserListener
import com.pxy.reptile.parse.ProxyListener
import com.pxy.reptile.parse.Resolver
import com.pxy.reptile.parse.XSP520Provider
import com.pxy.reptile.server.HttpProxyServer
import com.pxy.reptile.server.HttpProxyServerConfig
import com.teamdev.jxbrowser.chromium.Browser
import com.teamdev.jxbrowser.chromium.BrowserContext
import com.teamdev.jxbrowser.chromium.BrowserContextParams
import com.teamdev.jxbrowser.chromium.CustomProxyConfig
import com.teamdev.jxbrowser.chromium.events.*
import com.teamdev.jxbrowser.chromium.internal.FileUtil
import com.teamdev.jxbrowser.chromium.swing.BrowserView
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse

import javax.swing.*
import java.awt.*


object JxBrowser {


    private val homeUrl = "http://www.google.com"
    private const val ProxyHost = "127.0.0.1"
    private const val ProxyPort = 9999
    // private const val url = "http://www.520xsp.com/?m=vod-type-id-21.html"
    private const val url = "http://www.520xsp.com/?m=vod-type-id-2.html"
 //   private const val url = "https://www.baidu.com"
    private var currtentUrl: String? = ""
    private var isCall = false

    @JvmStatic
    fun main(agr0: Array<String>) {
        //JxBrowserCrack()
        DataBaseFactory.init()

        Resolver().apply {
            initBrowser(this).let {
                this.browser = it
            }
            initProxy(this)
        }
    }


    private fun initBrowser(resolver: Resolver): Browser {

        val dataDir = FileUtil.createTempDir("chromium-data").absolutePath
        val contextParams = BrowserContextParams(dataDir)
        val proxyRules = "http=$ProxyHost:$ProxyPort;https=$ProxyHost:$ProxyPort;ftp=$ProxyHost:$ProxyPort;socks=$ProxyHost:$ProxyPort"
        val exceptions = "<local>"  // bypass proxy server for local web pages
        contextParams.setProxyConfig(CustomProxyConfig(proxyRules, exceptions))
        val browser = Browser(BrowserContext(contextParams))
        val view = BrowserView(browser)

        browser.addLoadListener(object : LoadAdapter() {
            override fun onStartLoadingFrame(event: StartLoadingEvent?) {
            }

            override fun onProvisionalLoadingFrame(event: ProvisionalLoadingEvent?) {
            }

            override fun onFinishLoadingFrame(event: FinishLoadingEvent?) {
                var url = event?.browser?.url
                var html = event?.browser?.html
                if (!html!!.contains("未连接到互联网") || !html.contains("代理服务器出现问题，或者地址有误")) {
                    (resolver as BrowserListener).onBrowser(url, html)
                } else {
                    browser.reload()
                }
            }

            override fun onFailLoadingFrame(event: FailLoadingEvent?) {
                browser.reload()
            }

            override fun onDocumentLoadedInFrame(event: FrameLoadEvent?) {

            }

            override fun onDocumentLoadedInMainFrame(event: LoadEvent?) {
            }
        })

        val addressBar = JTextField(url)
        addressBar.addActionListener { e -> browser.loadURL(addressBar.text) }
        val addressPane = JPanel(BorderLayout()).apply {
            add(JLabel(" URL: "), BorderLayout.WEST)
            add(addressBar, BorderLayout.CENTER)
        }

        JFrame("JxBrowser - Hello World").let {
            it.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            it.add(addressPane, BorderLayout.NORTH)
            it.add(view, BorderLayout.CENTER)
            it.setSize(200, 200)
            it.setLocationRelativeTo(null)
            it.isVisible = true
        }
        browser.loadURL(addressBar.text)
        return browser
    }


    private fun initProxy(resolver: Resolver) {
        val config = HttpProxyServerConfig()
        config.isHandleSsl = true
        HttpProxyServer()
                .serverConfig(config)
                .proxyInterceptInitializer(object : HttpProxyInterceptInitializer() {
                    override fun init(pipeline: HttpProxyInterceptPipeline) {
                        pipeline.addLast(object : FullResponseIntercept() {
                            override fun match(httpRequest: HttpRequest, httpResponse: HttpResponse, pipeline: HttpProxyInterceptPipeline): Boolean {
                                //在匹配到百度首页时插入js
                                /* HttpUtil.checkUrl(pipeline.getHttpRequest(), "^www.baidu.com$")
                                        && isHtml(httpRequest, httpResponse)*/
                                //System.out.println(pipeline.getHttpRequest().uri());
                                (resolver as ProxyListener).onProxy(pipeline.httpRequest.uri())
                                return true
                            }

                            override fun handelResponse(httpRequest: HttpRequest, httpResponse: FullHttpResponse, pipeline: HttpProxyInterceptPipeline) {
                                //打印原始响应信息
                                // System.out.println(httpResponse.toString());
                                //  System.out.println(httpResponse.content().toString(Charset.defaultCharset()));
                                //修改响应头和响应体
                                // httpResponse.headers().set("handel", "edit head");
                                /*int index = ByteUtil.findText(httpResponse.content(), "<head>");
                                 ByteUtil.insertText(httpResponse.content(), index, "<script>alert(1)</script>");*/
                                // httpResponse.content().writeBytes("<script>alert('hello reptile')</script>".getBytes());
                            }
                        })
                    }
                })
                .start(9999)
    }

}
