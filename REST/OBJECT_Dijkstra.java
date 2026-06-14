import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class RbcimgMe {

    static final String STUDENT_CODE = "B22DCAT134";
    static final String QCODE = "RbcimgMe";

    static class Edge {
        String to;
        int weight;

        Edge(String to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    static class Node {
        String name;
        int dist;

        Node(String name, int dist) {
            this.name = name;
            this.dist = dist;
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

            String start =
                    data.get("start")
                            .getAsString();

            String end =
                    data.get("end")
                            .getAsString();

            JsonArray edgesArr =
                    data.getAsJsonArray("edges");

            Map<String, List<Edge>> graph =
                    new HashMap<>();

            for (JsonElement e : edgesArr) {

                JsonObject edge =
                        e.getAsJsonObject();

                String from =
                        edge.get("from")
                                .getAsString();

                String to =
                        edge.get("to")
                                .getAsString();

                int weight =
                        edge.get("weight")
                                .getAsInt();

                graph.computeIfAbsent(
                        from,
                        k -> new ArrayList<>())
                        .add(new Edge(to, weight));

                graph.computeIfAbsent(
                        to,
                        k -> new ArrayList<>())
                        .add(new Edge(from, weight));
            }

            Map<String, Integer> dist =
                    new HashMap<>();

            Map<String, String> parent =
                    new HashMap<>();

            PriorityQueue<Node> pq =
                    new PriorityQueue<>(
                            Comparator.comparingInt(
                                    a -> a.dist));

            dist.put(start, 0);

            pq.add(
                    new Node(
                            start,
                            0));

            while (!pq.isEmpty()) {

                Node cur =
                        pq.poll();

                if (cur.dist >
                        dist.getOrDefault(
                                cur.name,
                                Integer.MAX_VALUE))
                    continue;

                for (Edge edge :
                        graph.getOrDefault(
                                cur.name,
                                Collections.emptyList())) {

                    int nd =
                            cur.dist
                            + edge.weight;

                    if (nd <
                            dist.getOrDefault(
                                    edge.to,
                                    Integer.MAX_VALUE)) {

                        dist.put(
                                edge.to,
                                nd);

                        parent.put(
                                edge.to,
                                cur.name);

                        pq.add(
                                new Node(
                                        edge.to,
                                        nd));
                    }
                }
            }

            LinkedList<String> path =
                    new LinkedList<>();

            String cur = end;

            while (cur != null) {

                path.addFirst(cur);

                if (cur.equals(start))
                    break;

                cur = parent.get(cur);
            }

            int cost =
                    dist.get(end);

            String answer =
                    cost
                    + "|"
                    + String.join(
                            "->",
                            path);

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

//b. Server trả về `data` là object gồm `nodes`, `edges`, `start`, `end`.

//c. Dùng Dijkstra để tìm đường đi ngắn nhất từ `start` đến `end`.

//d. Gửi POST /api/rest/object/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu đường đi ngắn nhất là `N1->N3->N5` với chi phí `17` thì `answer` là `17|N1->N3->N5`.
