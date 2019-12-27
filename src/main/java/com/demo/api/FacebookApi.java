package com.demo.api;

import com.google.gson.annotations.SerializedName;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface FacebookApi {

    /**
     * 獲取 Access Token
     * @param code   透過Oauth登入之後獲取的code
     * @param redirectUri 登入成功(或失敗)回調地址
     * @return
     */
    @GET("oauth/access_token")
    Call<AccessTokenResponse> getAccessToken(
            @Query("code") String code,
            @Query("redirect_uri") String redirectUri
    );


    /**
     * 取得當前登入者訊息
     * @param accessToken
     * @param fields 選擇需獲取的欄位訊息(需有授權) https://developers.facebook.com/docs/facebook-login/permissions/
     * @return
     */
    @GET("me")
    Call<User> getMe(
            @Query("access_token") String accessToken,
            @Query("fields") String fields
    );

    /**
     * 檢查 Access Token，獲取此token相關訊息
     * https://developers.facebook.com/docs/graph-api/reference/v5.0/debug_token
     * @param inputToken    access token
     * @param accessToken   access token
     * @return {@link DebugToken}
     */
    @GET("debug_token")
    Call<DebugToken> debugToken(
            @Query("input_token") String inputToken,
            @Query("access_token") String accessToken
    );

    @lombok.Data
    class AccessTokenResponse {
        @SerializedName("access_token")
        private String accessToken;

        @SerializedName("token_type")
        private String tokenType;

        @SerializedName("expires_in")
        private Long expiresIn;
    }


    @lombok.Data
    class User {
        private String id;
        private String name;
        private String email;
        private Friends friends;
    }

    @lombok.Data
    class Summary {
        @SerializedName("total_count")
        private Integer totalCount;
    }

    @lombok.Data
    class Friends {
        private List<User> data;
        private Summary summary;
    }

    @lombok.Data
    class DebugToken {

        private Data data;

        @lombok.Data
        public static class Data {
            @SerializedName("user_id")
            private String userId;

            @SerializedName("expires_at")
            private Long expiresAt;
        }
    }
}
