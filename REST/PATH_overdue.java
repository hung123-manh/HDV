import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class l5ZmLTdB {

    static final String STUDENT_CODE = "B22DCAT134";
    static final String QCODE = "l5ZmLTdB";

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

            JsonObject root =
                    JsonParser.parseString(
                            getResponse.body())
                            .getAsJsonObject();

            String requestId =
                    root.get("requestId")
                            .getAsString();

            JsonArray customers =
                    root.getAsJsonArray("data");

            String bestCustomerId = "";
            int bestPage = 0;
            int maxOverdue = -1;

            for (JsonElement e : customers) {

                JsonObject c =
                        e.getAsJsonObject();

                String status =
                        c.get("status")
                                .getAsString();

                if (!"OVERDUE".equals(status))
                    continue;

                int overdueAmount =
                        c.get("overdueAmount")
                                .getAsInt();

                if (overdueAmount > maxOverdue) {

                    maxOverdue = overdueAmount;

                    bestCustomerId =
                            c.get("customerId")
                                    .getAsString();

                    bestPage =
                            c.get("page")
                                    .getAsInt();
                }
            }

            String detailUrl =
                    "http://" + examIP
                    + ":2230/api/rest/path/"
                    + bestCustomerId
                    + "?studentCode=" + STUDENT_CODE
                    + "&qCode=" + QCODE
                    + "&requestId=" + requestId
                    + "&status=OVERDUE"
                    + "&page=" + bestPage;

            System.out.println("\nDETAIL URL:");
            System.out.println(detailUrl);

            HttpRequest detailRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(detailUrl))
                            .GET()
                            .build();

            HttpResponse<String> detailResponse =
                    client.send(
                            detailRequest,
                            HttpResponse.BodyHandlers.ofString());

            System.out.println("\nRESULT:");
            System.out.println(detailResponse.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//Một dịch vụ REST PathService được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/path để kiểm tra cách sử dụng path parameter và query parameter.

//Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với PathService và thực hiện các công việc sau.

//a. Gửi GET /api/rest/path?studentCode=<mã_sinh_viên>&qCode=<qAlias>. qCode là alias runtime được giao.

//b. Server trả về requestId và data là danh sách khách hàng, mỗi phần tử có customerId, status, overdueAmount, page.

//c. Chỉ xét khách hàng có status bằng OVERDUE, chọn khách hàng có overdueAmount lớn nhất.

//d. Gửi GET /api/rest/path/{customerId}?studentCode=<mã_sinh_viên>&qCode=<qAlias>&requestId=<requestId>&status=OVERDUE&page=<page>.

//e. customerId, status và page phải khớp khách hàng đã chọn.
