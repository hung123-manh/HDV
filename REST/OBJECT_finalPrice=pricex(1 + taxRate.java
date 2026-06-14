import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class RTklxLdH {

    static final String STUDENT_CODE = "B22DCAT134";
    static final String QCODE = "RTklxLdH";

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

            double price =
                    data.get("price")
                            .getAsDouble();

            double taxRate =
                    data.get("taxRate")
                            .getAsDouble();

            double discount =
                    data.get("discount")
                            .getAsDouble();

            double finalPrice =
                    price
                    * (1 + taxRate / 100.0)
                    * (1 - discount / 100.0);

            finalPrice =
                    Math.round(
                            finalPrice * 100.0)
                    / 100.0;

            JsonObject answerObj =
                    new JsonObject();

            answerObj.addProperty(
                    "finalPrice",
                    Double.parseDouble(
                            String.format(
                                    Locale.US,
                                    "%.2f",
                                    finalPrice)));

            System.out.println("\nANSWER:");
            System.out.println(
                    answerObj.toString());

            JsonObject submitObj =
                    new JsonObject();

            submitObj.addProperty(
                    "studentCode",
                    STUDENT_CODE);

            submitObj.addProperty(
                    "qCode",
                    QCODE);

            submitObj.addProperty(
                    "requestId",
                    requestId);

            submitObj.add(
                    "answer",
                    answerObj);

            String body =
                    submitObj.toString();

            System.out.println("\nPOST BODY:");
            System.out.println(body);

            HttpRequest submitRequest =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            "http://"
                                            + examIP
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
//Một dịch vụ REST ObjectService được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/object để xử lý các bài toán với đối tượng JSON.

//Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với ObjectService và thực hiện các công việc sau.

//a. Gửi GET /api/rest/object?studentCode=<mã_sinh_viên>&qCode=<qAlias>. qCode là alias runtime, không dùng mã chuẩn của câu hỏi.

//b. Server trả về requestId và data là object sản phẩm gồm name, price, taxRate, discount.

//c. Tính finalPrice = price * (1 + taxRate / 100) * (1 - discount / 100), làm tròn 2 chữ số thập phân.

//d. Gửi POST /api/rest/object/submit với body JSON chứa studentCode, qCode, requestId và answer là object có trường finalPrice.

//e. Sai số chấp nhận tối đa 0.01.
