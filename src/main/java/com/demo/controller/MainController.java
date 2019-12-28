package com.demo.controller;


import com.demo.api.FacebookApi;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MainController {


    @Value("${facebook.app-id}")
    private String facebookAppId;

    private static final String FB_REDIRECT_URL_NAME = "fbLoginCallback";

    @NonNull
    private FacebookApi facebookApi;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "Facebook Login Demo Home");
        return "pages/index";
    }


    /**
     * 進行 fb oauth 登入
     * @return
     */
    @RequestMapping("doFBLogin")
    public String doFBLogin(HttpServletRequest request) {
        val url = "https://www.facebook.com/v5.0/dialog/oauth?" +
                "client_id=%s" +
                "&redirect_uri=%s" +
                "&scope=email,user_friends";
        return "redirect:" + String.format(url, facebookAppId, getFBRedirectUrl(request));
    }


    /**
     * 登入成功(失敗)回調
     * @param session
     * @param model
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(FB_REDIRECT_URL_NAME)
    public String fbLoginCallback(HttpSession session, Model model, HttpServletRequest request) throws IOException {
        model.addAttribute("title", "Facebook Login Demo");

        val code = request.getParameter("code");

        val accessTokenCall = facebookApi.getAccessToken(code, getFBRedirectUrl(request));
        val accessTokenResponse = accessTokenCall.execute().body();

        if (accessTokenResponse == null) {
            model.addAttribute("error", "獲取 Access Token 失敗");
            return "pages/error";
        }

        val meCall = facebookApi.getMe(accessTokenResponse.getAccessToken(), "id,email,name,friends");
        val me = meCall.execute().body();
        session.setAttribute("user", me);
        session.setAttribute("token", accessTokenResponse);

        return "redirect:";
    }

    /**
     * 根據access token 獲取個人訊息
     * @param session
     * @return
     * @throws IOException
     */
    @GetMapping("me")
    @ResponseBody
    public FacebookApi.User getMe(HttpSession session) throws IOException {
        FacebookApi.AccessTokenResponse accessToken = (FacebookApi.AccessTokenResponse) session.getAttribute("token");
        val meCall = facebookApi.getMe(accessToken.getAccessToken(), "id,email,name,friends");
        return meCall.execute().body();
    }


    @GetMapping("debugToken")
    @ResponseBody
    public FacebookApi.DebugToken debugToken(HttpSession session) throws IOException {
        FacebookApi.AccessTokenResponse accessToken = (FacebookApi.AccessTokenResponse) session.getAttribute("token");
        val debugTokenCall = facebookApi.debugToken(accessToken.getAccessToken(), accessToken.getAccessToken());
        return debugTokenCall.execute().body();
    }

    private String getFBRedirectUrl(HttpServletRequest request) {
        return String.format("https://%s:%d/%s", request.getServerName(), request.getServerPort(), FB_REDIRECT_URL_NAME);
    }

}
