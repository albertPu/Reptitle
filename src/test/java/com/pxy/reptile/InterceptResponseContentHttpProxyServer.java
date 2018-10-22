package com.pxy.reptile;

import com.pxy.reptile.intercept.HttpProxyInterceptInitializer;
import com.pxy.reptile.intercept.HttpProxyInterceptPipeline;
import com.pxy.reptile.intercept.common.FullResponseIntercept;
import com.pxy.reptile.server.HttpProxyServer;
import com.pxy.reptile.server.HttpProxyServerConfig;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class InterceptResponseContentHttpProxyServer {

    public static void main(String[] args) throws Exception {
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setHandleSsl(true);
        new HttpProxyServer()
                .serverConfig(config)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
                    @Override
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new FullResponseIntercept() {

                            @Override
                            public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                                //在匹配到百度首页时插入js
                               /* HttpUtil.checkUrl(pipeline.getHttpRequest(), "^www.baidu.com$")
                                        && isHtml(httpRequest, httpResponse)*/
                                //System.out.println(pipeline.getHttpRequest().uri());
                                return true;
                            }

                            @Override
                            public void handelResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {

                                //System.out.println(  pipeline.getHttpRequest().uri());
                                //打印原始响应信息
                                // System.out.println(httpResponse.toString());
                                //  System.out.println(httpResponse.content().toString(Charset.defaultCharset()));
                                //修改响应头和响应体
                                // httpResponse.headers().set("handel", "edit head");
                /*int index = ByteUtil.findText(httpResponse.content(), "<head>");
                ByteUtil.insertText(httpResponse.content(), index, "<script>alert(1)</script>");*/
                                // httpResponse.content().writeBytes("<script>alert('hello reptile')</script>".getBytes());
                            }
                        });
                    }
                })
                .start(9999);
    }
}
