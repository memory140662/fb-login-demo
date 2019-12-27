package com.demo.config;

import com.demo.api.FacebookApi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class Beans {

    @Value("${facebook.graph-url}")
    private String facebookGraphUrl;

    @Value("${facebook.app-id}")
    private String facebookAppId;

    @Value("${facebook.app-secret}")
    private String facebookAppSecret;

    @Bean
    public FacebookApi facebookApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(facebookGraphUrl)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(FacebookApi.class);
    }

    private OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    String url = request.url().toString();
                    if (url.contains("?")) {
                        url += String.format("&client_id=%s&client_secret=%s", facebookAppId, facebookAppSecret);
                    } else {
                        url += String.format("?client_id=%s&client_secret=%s", facebookAppId, facebookAppSecret);
                    }

                    System.out.println(url);
                    return chain.proceed(request.newBuilder().url(url).build());
                })
                .build();
    }

}
