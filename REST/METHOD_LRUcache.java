import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class main {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "POENOtag";
        String baseUrl = "http://36.50.135.242:2230/api/rest/method";

        String getUrl =
                baseUrl
                + "?studentCode="
                + studentCode
                + "&qCode="
                + qCode;

        HttpURLConnection getConn =
                (HttpURLConnection)
                        new URL(getUrl)
                                .openConnection();

        getConn.setRequestMethod("GET");

        BufferedReader in =
                new BufferedReader(
                        new InputStreamReader(
                                getConn.getInputStream()));

        StringBuilder response =
                new StringBuilder();

        String line;

        while ((line = in.readLine()) != null) {
            response.append(line);
        }

        in.close();

        String jsonStr =
                response.toString();

        System.out.println("GET:");
        System.out.println(jsonStr);

        JsonObject root =
                JsonParser.parseString(jsonStr)
                        .getAsJsonObject();

        String requestId =
                root.get("requestId")
                        .getAsString();

        JsonObject data =
                root.getAsJsonObject("data");

        int capacity =
                data.get("capacity")
                        .getAsInt();

        JsonArray requests =
                data.getAsJsonArray("requests");

        LinkedHashMap<String, String> cache =
                new LinkedHashMap<>(
                        capacity,
                        0.75f,
                        true);

        List<String> accepted =
                new ArrayList<>();

        for (JsonElement e : requests) {

            JsonObject req =
                    e.getAsJsonObject();

            String id =
                    req.get("id")
                            .getAsString();

            String key =
                    req.get("key")
                            .getAsString();

            if (cache.containsKey(key)) {

                cache.get(key); // cập nhật LRU

            } else {

                accepted.add(id);

                if (cache.size() >= capacity) {

                    String oldest =
                            cache.keySet()
                                    .iterator()
                                    .next();

                    cache.remove(oldest);
                }

                cache.put(key, id);
            }
        }

        String answer =
                String.join(
                        ",",
                        accepted);

        System.out.println("\nANSWER:");
        System.out.println(answer);

        String jsonPut =
                "{"
                + "\"studentCode\":\""
                + studentCode
                + "\","
                + "\"qCode\":\""
                + qCode
                + "\","
                + "\"answer\":\""
                + answer
                + "\""
                + "}";

        HttpURLConnection putConn =
                (HttpURLConnection)
                        new URL(
                                baseUrl
                                + "/"
                                + requestId)
                                .openConnection();

        putConn.setRequestMethod("PUT");
        putConn.setRequestProperty(
                "Content-Type",
                "application/json");
        putConn.setDoOutput(true);

        OutputStream os =
                putConn.getOutputStream();

        os.write(
                jsonPut.getBytes("UTF-8"));

        os.flush();
        os.close();

        BufferedReader resultReader =
                new BufferedReader(
                        new InputStreamReader(
                                putConn.getInputStream()));

        StringBuilder result =
                new StringBuilder();

        while ((line =
                resultReader.readLine()) != null) {

            result.append(line);
        }

        resultReader.close();

        System.out.println("\nRESULT:");
        System.out.println(result.toString());
    }
}
