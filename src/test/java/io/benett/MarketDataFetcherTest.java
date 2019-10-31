package io.benett;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MarketDataFetcherTest {

  @Test
  void it_is_working_with_junit_and_assertj() {
    assertThat(true).isTrue();
  }

  @Test
  void test_ok_http_client() throws IOException {
    OkHttpClient client = new OkHttpClient();

      Request request = new Request.Builder()
      .url("https://google.com")
      .build();

    try (Response response = client.newCall(request).execute()) {
      System.out.println(response.body().string());
    }
  }

  @Test
  void test_unauthorized_connection_to_lgo_api() throws IOException {
    var request = new Request.Builder()
      .url("wss://ws.exchange.lgo.markets")
      .build();

    final var client = new OkHttpClient();

    try (var response = client.newCall(request).execute()) {
      assertThat(response.code()).isEqualTo(401);
    }
  }

  @Test
  void can_sign_a_string_using_SH256withRSA() throws Exception {
    final String signedToto = new MarketDataFetcher().signString("toto");

    System.out.println(signedToto);
  }
}
