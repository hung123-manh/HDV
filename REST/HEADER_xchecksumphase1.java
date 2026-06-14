import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SVB6wYBA {

    static final String STUDENT_CODE = "B22DCAT134";
    static final String QCODE = "SVB6wYBA";

    public static void main(String[] args) {

        try {

            String examIP = "36.50.135.242";

            HttpClient client = HttpClient.newHttpClient();

            String getUrl =
                    "http://" + examIP
                    + ":2230/api/rest/header"
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

            String checksum =
                    getResponse.headers()
                            .firstValue("x-checksum")
                            .orElse("");

            System.out.println("\nX-CHECKSUM:");
            System.out.println(checksum);

            JsonObject root =
                    JsonParser.parseString(
                            getResponse.body())
                            .getAsJsonObject();

            String requestId =
                    root.get("requestId")
                            .getAsString();

            JsonObject bodyObj =
                    new JsonObject();

            bodyObj.addProperty(
                    "studentCode",
                    STUDENT_CODE);

            bodyObj.addProperty(
                    "qCode",
                    QCODE);

            bodyObj.addProperty(
                    "requestId",
                    requestId);

            String body =
                    bodyObj.toString();

            System.out.println("\nPOST BODY:");
            System.out.println(body);

            HttpRequest submitRequest =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            "http://"
                                            + examIP
                                            + ":2230/api/rest/header/submit"
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "X-Checksum",
                                    checksum
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
//Một dịch vụ REST HeaderService được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/header để kiểm tra cách đọc và gửi HTTP header.

//Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với HeaderService và thực hiện các công việc sau.

//a. Gửi GET /api/rest/header?studentCode=<mã_sinh_viên>&qCode=<qAlias>. qCode là alias runtime được giao.

//b. Server trả về requestId, data là danh sách số nguyên và response header X-Checksum.

//c. Đọc đúng giá trị header X-Checksum từ phase 1.

//d. Gửi POST /api/rest/header/submit với body chứa studentCode, qCode, requestId và kèm lại header X-Checksum đã nhận.

//e. Không cần tự tạo checksum mới; yêu cầu là truyền lại đúng giá trị server đã cấp.
