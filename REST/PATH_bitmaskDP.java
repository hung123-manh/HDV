import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.gson.*;
import java.util.*;

public class qjMrPW3R {

    static final String STUDENT_CODE = "B22DCAT134";
    static final String QCODE = "qjMrPW3R";

    public static void main(String[] args) {

        try {

            String examIP = "36.50.135.242";

            HttpClient client = HttpClient.newHttpClient();

            String getUrl =
                    "http://" + examIP
                    + ":2230/api/rest/path"
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

            String requestId =
                    getResponse.body()
                            .split("\"requestId\":\"")[1]
                            .split("\"")[0];

            JsonObject root =
    JsonParser.parseString(getResponse.body())
              .getAsJsonObject();

JsonObject data =
    root.getAsJsonObject("data");

String start =
    data.get("start").getAsString();

String end =
    data.get("end").getAsString();

JsonArray mandatoryArr =
    data.getAsJsonArray("mandatory");

JsonArray edgesArr =
    data.getAsJsonArray("edges");

class Edge {
    String to;
    int w;

    Edge(String to, int w) {
        this.to = to;
        this.w = w;
    }
}

Map<String, List<Edge>> graph =
    new HashMap<>();

for (JsonElement e : edgesArr) {

    JsonObject edge =
        e.getAsJsonObject();

    String from =
        edge.get("from").getAsString();

    String to =
        edge.get("to").getAsString();

    int weight =
        edge.get("weight").getAsInt();

    graph.computeIfAbsent(
            from,
            k -> new ArrayList<>())
         .add(new Edge(to, weight));

    graph.computeIfAbsent(
            to,
            k -> new ArrayList<>())
         .add(new Edge(from, weight));
}

List<String> important =
    new ArrayList<>();

important.add(start);

for (JsonElement e : mandatoryArr)
    important.add(e.getAsString());

important.add(end);

int n = important.size();

int[][] dist =
    new int[n][n];

for (int[] row : dist)
    Arrays.fill(row, Integer.MAX_VALUE / 4);

for (int i = 0; i < n; i++) {

    String src = important.get(i);

    Map<String, Integer> d =
        new HashMap<>();

    PriorityQueue<String> pq =
        new PriorityQueue<>(
            Comparator.comparingInt(
                x -> d.getOrDefault(
                    x,
                    Integer.MAX_VALUE)));

    d.put(src, 0);
    pq.add(src);

    while (!pq.isEmpty()) {

        String cur = pq.poll();

        int curDist = d.get(cur);

        for (Edge ed :
                graph.getOrDefault(
                    cur,
                    Collections.emptyList())) {

            int nd =
                curDist + ed.w;

            if (nd <
                d.getOrDefault(
                    ed.to,
                    Integer.MAX_VALUE)) {

                d.put(ed.to, nd);
                pq.add(ed.to);
            }
        }
    }

    for (int j = 0; j < n; j++) {

        dist[i][j] =
            d.getOrDefault(
                important.get(j),
                Integer.MAX_VALUE / 4);
    }
}

int m = n - 2;

String answer;

if (m == 0) {

    answer =
        dist[0][1]
        + "|"
        + start
        + "->"
        + end;

} else {

    int FULL = 1 << m;

    int[][] dp =
        new int[FULL][m];

    int[][] prev =
        new int[FULL][m];

    for (int[] row : dp)
        Arrays.fill(row, Integer.MAX_VALUE / 4);

    for (int i = 0; i < m; i++) {

        dp[1 << i][i] =
            dist[0][i + 1];
    }

    for (int mask = 0;
         mask < FULL;
         mask++) {

        for (int u = 0;
             u < m;
             u++) {

            if ((mask & (1 << u)) == 0)
                continue;

            for (int v = 0;
                 v < m;
                 v++) {

                if ((mask & (1 << v)) != 0)
                    continue;

                int nmask =
                    mask | (1 << v);

                int nd =
                    dp[mask][u]
                    + dist[u + 1][v + 1];

                if (nd < dp[nmask][v]) {

                    dp[nmask][v] = nd;
                    prev[nmask][v] = u;
                }
            }
        }
    }

    int best =
        Integer.MAX_VALUE;

    int last = -1;

    for (int i = 0; i < m; i++) {

        int total =
            dp[FULL - 1][i]
            + dist[i + 1][m + 1];

        if (total < best) {

            best = total;
            last = i;
        }
    }

    List<String> route =
        new ArrayList<>();

    route.add(end);

    int mask =
        FULL - 1;

    while (mask != 0) {

        route.add(
            important.get(
                last + 1));

        int p =
            prev[mask][last];

        mask ^= (1 << last);

        last = p;
    }

    route.add(start);

    Collections.reverse(route);

    answer =
        best
        + "|"
        + String.join(
            "->",
            route);
}

System.out.println(
    "\nANSWER = "
    + answer);

            String submitUrl =
                    "http://" + examIP
                    + ":2230/api/rest/path/submit"
                    + "?studentCode=" + STUDENT_CODE
                    + "&qCode=" + QCODE
                    + "&requestId=" + requestId
                    + "&answer="
                    + URLEncoder.encode(
                            answer,
                            StandardCharsets.UTF_8);

            System.out.println("\nSUBMIT URL:");
            System.out.println(submitUrl);

            HttpRequest submitRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(submitUrl))
                            .GET()
                            .build();

            HttpResponse<String> submitResponse =
                    client.send(
                            submitRequest,
                            HttpResponse.BodyHandlers.ofString());

            System.out.println("\nRESULT:");
            System.out.println(submitResponse.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/path để xử lý các bài toán lựa chọn bản ghi và tìm đường đi. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/path?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `nodes`, `edges`, `start`, `end` và `mandatory`.

//c. Tìm lộ trình ngắn nhất đi từ `start` đến `end` và ghé đủ mọi điểm trong `mandatory` bằng bitmask DP.

//d. Gửi POST /api/rest/path/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu lộ trình tốt nhất là `P1->P3->P4->P6` với chi phí `32` thì `answer` là `32|P1->P3->P4->P6`.
