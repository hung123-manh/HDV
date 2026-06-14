import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HY44pRSg {

    static final String STUDENT_CODE = "B22DCAT134";
    static final String QCODE = "HY44pRSg";

    public static void main(String[] args) {

        try {

            String examIP = "36.50.135.242";

            HttpClient client = HttpClient.newHttpClient();

            String getUrl =
                    "http://" + examIP
                    + ":2230/api/rest/method"
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

            String etag =
                    data.get("etag")
                            .getAsString();

            JsonObject answer =
                    new JsonObject();

            answer.addProperty(
                    "status",
                    "RESOLVED");

            JsonObject bodyObj =
                    new JsonObject();

            bodyObj.addProperty(
                    "studentCode",
                    STUDENT_CODE);

            bodyObj.addProperty(
                    "qCode",
                    QCODE);

            bodyObj.add(
                    "answer",
                    answer);

            String body =
                    bodyObj.toString();

            System.out.println("\nETAG:");
            System.out.println(etag);

            System.out.println("\nPATCH BODY:");
            System.out.println(body);

            HttpRequest patchRequest =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            "http://"
                                            + examIP
                                            + ":2230/api/rest/method/"
                                            + requestId
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "If-Match",
                                    etag
                            )
                            .method(
                                    "PATCH",
                                    HttpRequest.BodyPublishers
                                            .ofString(body)
                            )
                            .build();

            HttpResponse<String> patchResponse =
                    client.send(
                            patchRequest,
                            HttpResponse.BodyHandlers.ofString());

            System.out.println("\nRESULT:");
            System.out.println(
                    patchResponse.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//Một dịch vụ REST MethodService được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/method để kiểm tra cách sử dụng HTTP method trong quy trình submit.

//Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với MethodService và thực hiện các công việc sau.

//a. Gửi GET /api/rest/method?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận ticket cần xử lý. qCode là alias runtime được giao.

//b. Server trả về requestId và data gồm ticketId, status, targetStatus, version, etag.

//c. Gửi phase 2 bằng phương thức PATCH tới /api/rest/method/{requestId} và kèm header If-Match đúng bằng etag nhận được.

//d. Body JSON chứa studentCode, qCode và answer; trong đó answer.status bằng RESOLVED.

//e. Thiếu header If-Match, sai etag, hoặc dùng sai phương thức sẽ không đạt.
