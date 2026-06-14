import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class Fqnlgza2 {

    static final String STUDENT_CODE = "B22DCAT134";
    static final String QCODE = "Fqnlgza2";

    static class Job {
        String id;
        int start;
        int end;

        Job(String id, int start, int end) {
            this.id = id;
            this.start = start;
            this.end = end;
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

            JsonArray jobs =
                    root.getAsJsonObject("data")
                            .getAsJsonArray("jobs");

            List<Job> list =
                    new ArrayList<>();

            for (JsonElement e : jobs) {

                JsonObject j =
                        e.getAsJsonObject();

                list.add(
                        new Job(
                                j.get("id")
                                        .getAsString(),
                                j.get("start")
                                        .getAsInt(),
                                j.get("end")
                                        .getAsInt()
                        )
                );
            }

            list.sort(
                    Comparator.comparingInt(
                            a -> a.end));

            List<String> chosen =
                    new ArrayList<>();

            int lastEnd = -1;

            for (Job job : list) {

                if (job.start >= lastEnd) {

                    chosen.add(job.id);
                    lastEnd = job.end;
                }
            }

            String answer =
                    chosen.stream()
                            .collect(
                                    Collectors.joining(","));

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

//b. Server trả về `data` là object gồm danh sách `jobs`.

//c. Chọn nhiều job không chồng lấp nhất bằng greedy theo thời điểm kết thúc.

//d. Gửi POST /api/rest/object/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu chọn được `J1`, `J3`, `J5` thì `answer` là `J1,J3,J5`.
