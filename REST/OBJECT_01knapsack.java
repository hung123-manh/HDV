import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class zdFsMloF {

    static final String STUDENT_CODE = "B22DCAT134";
    static final String QCODE = "zdFsMloF";

    static class Item {
        String id;
        int weight;
        int value;

        Item(String id, int weight, int value) {
            this.id = id;
            this.weight = weight;
            this.value = value;
        }
    }

    public static void main(String[] args) {

        try {

            String examIP = "36.50.135.242";

            HttpClient client = HttpClient.newHttpClient();

            String getUrl =
                    "http://" + examIP
                    + ":2230/api/rest/object"
                    + "?studentCode=" + STUDENT_CODE
                    + "&qCode=" + QCODE;

            HttpRequest getRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(getUrl))
                            .GET()
                            .build();

            HttpResponse<String> getResponse =
                    client.send(
                            getRequest,
                            HttpResponse.BodyHandlers.ofString());

            System.out.println("GET:");
            System.out.println(getResponse.body());

            JsonObject root =
                    JsonParser.parseString(
                            getResponse.body())
                            .getAsJsonObject();

            String requestId =
                    root.get("requestId")
                            .getAsString();

            JsonObject data =
                    root.getAsJsonObject("data");

            int capacity =
                    data.get("capacity")
                            .getAsInt();

            JsonArray itemsArr =
                    data.getAsJsonArray("items");

            List<Item> items =
                    new ArrayList<>();

            for (JsonElement e : itemsArr) {

                JsonObject obj =
                        e.getAsJsonObject();

                items.add(
                        new Item(
                                obj.get("id").getAsString(),
                                obj.get("weight").getAsInt(),
                                obj.get("value").getAsInt()
                        )
                );
            }

            int n = items.size();

            int[][] dp =
                    new int[n + 1][capacity + 1];

            boolean[][] take =
                    new boolean[n + 1][capacity + 1];

            for (int i = 1; i <= n; i++) {

                Item item =
                        items.get(i - 1);

                for (int w = 0;
                     w <= capacity;
                     w++) {

                    dp[i][w] =
                            dp[i - 1][w];

                    if (item.weight <= w) {

                        int candidate =
                                dp[i - 1][w - item.weight]
                                + item.value;

                        if (candidate > dp[i][w]) {

                            dp[i][w] = candidate;
                            take[i][w] = true;
                        }
                    }
                }
            }

            List<String> chosen =
                    new ArrayList<>();

            int w = capacity;

            for (int i = n;
                 i >= 1;
                 i--) {

                if (take[i][w]) {

                    Item item =
                            items.get(i - 1);

                    chosen.add(item.id);

                    w -= item.weight;
                }
            }

            Collections.reverse(chosen);

            int bestValue =
                    dp[n][capacity];

            String answer =
                    String.join(",", chosen)
                    + "|"
                    + bestValue;

            System.out.println("\nANSWER:");
            System.out.println(answer);

            String body =
                    "{"
                    + "\"studentCode\":\""
                    + STUDENT_CODE
                    + "\","
                    + "\"qCode\":\""
                    + QCODE
                    + "\","
                    + "\"requestId\":\""
                    + requestId
                    + "\","
                    + "\"answer\":\""
                    + answer
                    + "\""
                    + "}";

            System.out.println("\nPOST BODY:");
            System.out.println(body);

            HttpRequest submitRequest =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            "http://" + examIP
                                            + ":2230/api/rest/object/submit"
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(body)
                            )
                            .build();

            HttpResponse<String> submitResponse =
                    client.send(
                            submitRequest,
                            HttpResponse.BodyHandlers.ofString());

            System.out.println("\nRESULT:");
            System.out.println(
                    submitResponse.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/object để xử lý các bài toán với đối tượng. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/object?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `capacity` và `items`.

//c. Giải bài toán 0/1 knapsack để chọn tập item có tổng giá trị lớn nhất mà không vượt quá `capacity`.

//d. Gửi POST /api/rest/object/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu chọn `I2` và `I4` với tổng giá trị `98` thì `answer` là `I2,I4|98`.
