package io.benett;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

class MarketDataFetcher {
  private String lgoSandboxUrl = "wss://ws.exchange.lgo.markets/";

  public static void main(String[] args) throws Exception {
    new MarketDataFetcher().fetch();
  }

  private void fetch() throws Exception {
    final var currentDateTimestamp = String.valueOf(System.currentTimeMillis());

    final var client = new OkHttpClient();

    client.newWebSocket(
      new Request.Builder()
        .url(lgoSandboxUrl)
        .header("X-LGO-DATE", currentDateTimestamp)
        .header("Authorization",
          makeAuthorizationHeader(currentDateTimestamp, "60715d6f-adc9-4792-9b4d-d705135a6173"))
        .build(),
      new MarketDataListener()
    );

    client.dispatcher().executorService().shutdown();
  }

  private String makeAuthorizationHeader(String timeStamp, String accessKey) throws Exception {
    final var stringToSign = String.format("%s\n%s", timeStamp, lgoSandboxUrl.replace("wss://", ""));

    return String.format("LGO %s:%s", accessKey, sign(stringToSign));
}

  String sign(String stringToSign) throws Exception {
    final String pkcs8EncodedPrivateKey = Files.readString(Path.of("./out/production/resources/private_key.pem"))
      .replaceAll("-----END PRIVATE KEY-----", "")
      .replaceAll("-----BEGIN PRIVATE KEY-----", "")
      .replaceAll("\n", "");

    byte[] decodedPrivateKey = Base64.getDecoder().decode(pkcs8EncodedPrivateKey);

    var spec = new PKCS8EncodedKeySpec(decodedPrivateKey);
    final var keyFactory = KeyFactory.getInstance("RSA");

    var privateSignature = Signature.getInstance("SHA256withRSA");

    privateSignature.initSign(keyFactory.generatePrivate(spec));
    privateSignature.update(stringToSign.getBytes(StandardCharsets.UTF_8));

    return encodeToHexadecimal(privateSignature.sign());
  }

  private String encodeToHexadecimal(byte[] bytes) {
    StringBuilder sb = new StringBuilder();

    for (byte b : bytes) {
        sb.append(String.format("%02x", b));
    }

    return sb.toString();
  }
}
