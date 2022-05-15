package com.janboerman.f2pstarassist.plugin;

import com.google.gson.*;
import com.janboerman.f2pstarassist.common.*;
import com.janboerman.f2pstarassist.common.web.EndPoints;
import com.janboerman.f2pstarassist.common.web.StarJson;
import okhttp3.*;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class StarClient {

    private static final MediaType APPLICATION_JSON = MediaType.get("application/json");

    private final OkHttpClient httpClient;
    private final StarAssistConfig config;

    @Inject
    public StarClient(OkHttpClient httpClient, StarAssistConfig config) {
        this.httpClient = httpClient;
        this.config = config;
    }

    public CompletableFuture<StarList> requestStars(Set<GroupKey> groups, Set<CrashedStar> knownStars) {
        final String json = StarJson.starPacketJson(new StarPacket(groups, new StarRequest(knownStars))).toString();

        final String url = config.httpUrl() + EndPoints.ALL_STARS;
        final RequestBody requestBody = RequestBody.create(APPLICATION_JSON, json);
        final Request request = new Request.Builder().url(url).post(requestBody).build();

        final CompletableFuture<StarList> future = new CompletableFuture<>();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(new ResponseException(call, e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        future.completeExceptionally(new ResponseException(call, "WebServer answered with response code: " + response.code()));
                        return;
                    }

                    ResponseBody body = response.body();
                    //assert content type json?
                    Reader reader = body.charStream();
                    JsonParser jsonParser = new JsonParser();

                    try {
                        JsonElement jsonElement = jsonParser.parse(reader);
                        if (jsonElement instanceof JsonArray) {
                            //legacy star list: json array of crashed stars
                            Set<CrashedStar> starSet = StarJson.crashedStars((JsonArray) jsonElement);
                            StarList starList = new StarList(Collections.singletonMap(starSet, Collections.emptySet()), Collections.emptySet(), Collections.emptySet());
                            future.complete(starList);
                        } else if (jsonElement instanceof JsonObject) {
                            //new star list: is using its own json representation
                            StarList starList = StarJson.starList((JsonObject) jsonElement);
                            future.complete(starList);
                        }

                        else {
                            future.completeExceptionally(new ResponseException(call, "Expected a json representation of a star list, but got: " + jsonElement));
                        }
                    } catch (RuntimeException e) {
                        future.completeExceptionally(new ResponseException(call, e));
                    }
                } finally {
                    response.close();
                }
            }
        });

        return future;
    }

    public CompletableFuture<Optional<CrashedStar>> sendStar(Set<GroupKey> groups, CrashedStar star) {
        final String json = StarJson.starPacketJson(new StarPacket(groups, star)).toString();

        final String url = config.httpUrl() + EndPoints.SEND_STAR;
        final RequestBody requestBody = RequestBody.create(APPLICATION_JSON, json);
        final Request request = new Request.Builder().url(url).put(requestBody).build();

        final CompletableFuture<Optional<CrashedStar>> future = new CompletableFuture<>();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(new ResponseException(call, e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new ResponseException(call, "WebServer answered with with response code: " + response.code()));
                    return;
                }

                switch (response.code()) {
                    case 200:
                        //assert content type json?
                        Reader reader = response.body().charStream();
                        JsonParser jsonParser = new JsonParser();

                        try {
                            JsonElement jsonElement = jsonParser.parse(reader);
                            if (jsonElement instanceof JsonObject) {
                                future.complete(Optional.of(StarJson.crashedStar((JsonObject) jsonElement)));
                            } else {
                                future.completeExceptionally(new ResponseException(call, "Expected a crashed star json object, but got: " + jsonElement));
                            }
                        } catch (RuntimeException e) {
                            future.completeExceptionally(new ResponseException(call, e));
                        }

                        break;
                    default:
                        future.complete(Optional.empty());
                        break;
                }
            }
        });

        return future;
    }

    public CompletableFuture<CrashedStar> updateStar(Set<GroupKey> groups, StarKey starKey, StarTier tier) {
        final String json = StarJson.starPacketJson(new StarPacket(groups, new StarUpdate(starKey, tier))).toString();

        final String url = config.httpUrl() + EndPoints.UPDATE_STAR;
        final RequestBody requestBody = RequestBody.create(APPLICATION_JSON, json);
        final Request request = new Request.Builder().url(url).patch(requestBody).build();

        final CompletableFuture<CrashedStar> future = new CompletableFuture<>();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(new ResponseException(call, e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new ResponseException(call, "WebServer answered with response code: " + response.code()));
                    return;
                }

                ResponseBody responseBody = response.body();
                //assert content type json?
                Reader reader = responseBody.charStream();
                JsonParser jsonParser = new JsonParser();

                try {
                    JsonElement jsonElement = jsonParser.parse(reader);
                    if (jsonElement instanceof JsonObject) {
                        future.complete(StarJson.crashedStar((JsonObject) jsonElement));
                    } else {
                        future.completeExceptionally(new ResponseException(call, "Expected a crashed star json object, but got: " + jsonElement));
                    }
                } catch (RuntimeException e) {
                    future.completeExceptionally(new ResponseException(call, e));
                }
            }
        });

        return future;
    }

    public CompletableFuture<Void> deleteStar(Set<GroupKey> groups, StarKey starKey) {
        final String json = StarJson.starPacketJson(new StarPacket(groups, starKey)).toString();

        final String url = config.httpUrl() + EndPoints.DELETE_STAR;
        final RequestBody requestBody = RequestBody.create(APPLICATION_JSON, json);
        final Request request = new Request.Builder().url(url).delete(requestBody).build();

        final CompletableFuture<Void> future = new CompletableFuture<>();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(new ResponseException(call, e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new ResponseException(call, "WebServer answered with response code: " + response.code()));
                    return;
                }

                future.complete(null);
            }
        });

        return future;
    }
}
