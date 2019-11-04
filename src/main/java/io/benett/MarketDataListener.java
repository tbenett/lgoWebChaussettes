package io.benett;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

class MarketDataListener extends WebSocketListener {

  @Override
  public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
    System.out.println("connected");

    webSocket.send(
      "{\n" +
        "  \"type\": \"subscribe\",\n" +
        "  \"channels\": [\n" +
        "    {\n" +
        "      \"name\": \"level2\",\n" +
        "      \"product_id\": \"BTC-USD\"\n" +
        "    }\n" +
        "  ]\n" +
        "}"
    );
  }

  @Override
  public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
    System.out.println(String.format("onMessageString: %s", text));
  }

  @Override
  public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
    System.out.println(String.format("onMessageBytes: %s", bytes.toString()));
    super.onMessage(webSocket, bytes);
  }

  @Override
  public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
    System.out.println("has failed");
    try {
      System.out.println(String.format("code: %s\nbody:%s", response.code(), response.body().string()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}