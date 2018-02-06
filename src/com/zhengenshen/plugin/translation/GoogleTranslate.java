package com.zhengenshen.plugin.translation;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * google Api
 *
 * @author zhengenshen
 * @create 2018-02-05 16:54
 */

public class GoogleTranslate implements Runnable {

    final String HOST = "translate.google.cn";
    final String PATH = "/translate_a/single";

    private Editor editor;
    private String query;
    private final String basePath;

    public GoogleTranslate(Editor editor, String query, String basePath) {
        this.editor = editor;
        this.query = query;
        this.basePath = basePath;
    }

    @Override
    public void run() {

        try {
            URI uri = createURI(query);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000).build();
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setConfig(requestConfig);
            HttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity httpEntity = response.getEntity();
                String json = EntityUtils.toString(httpEntity, "UTF-8");

                String formatStr = GoogleBean.build(json);
                showPopupBalloon(formatStr);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private void showPopupBalloon(final String s) {
        ApplicationManager.getApplication().invokeLater(() -> {
            JBPopupFactory factory = JBPopupFactory.getInstance();
            factory.createHtmlTextBalloonBuilder(s, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
                    .setFadeoutTime(5000)
                    .createBalloon()
                    .show(factory.guessBestPopupLocation(editor), Balloon.Position.below);
        });
    }

    private URI createURI(String query) throws URISyntaxException {

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http")
                .setHost(HOST)
                .setPath(PATH)
                .addParameter("client", "gtx")
                .addParameter("dt", "at")
                .addParameter("ie", "UTF-8")
                .addParameter("oe", "UTF-8");

        if (query.matches("[\\u4e00-\\u9fbb]+")) {
            builder.addParameter("sl", "zh-CN").addParameter("tl", "en");
        } else {
            builder.addParameter("sl", "en").addParameter("tl", "zh-CN");
        }
        builder.addParameter("q", query);
        return builder.build();
    }
}
