import com.google.gson.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class _2ISJeDIU{

    static final String STUDENT_CODE = "B22DCAT134";
    static final String QCODE = "2ISJeDIU";

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

            double weightKg =
                    data.get("weightKg")
                            .getAsDouble();

            int maxEtaDays =
                    data.get("maxEtaDays")
                            .getAsInt();

            JsonArray quotes =
                    data.getAsJsonArray("quotes");

            String bestCarrier = "";
            double bestFee = Double.MAX_VALUE;
            int bestEta = 0;
            double bestReliability = -1;

            for (JsonElement e : quotes) {

                JsonObject q =
                        e.getAsJsonObject();

                String carrier =
                        q.get("carrier")
                                .getAsString();

                double baseFee =
                        q.get("baseFee")
                                .getAsDouble();

                double perKgFee =
                        q.get("perKgFee")
                                .getAsDouble();

                int etaDays =
                        q.get("etaDays")
                                .getAsInt();

                double reliability =
                        q.get("reliability")
                                .getAsDouble();

                if (etaDays > maxEtaDays)
                    continue;

                double fee =
                        baseFee
                        + weightKg * perKgFee;

                if (fee < bestFee
                        || (Math.abs(fee - bestFee) < 1e-9
                        && reliability > bestReliability)) {

                    bestFee = fee;
                    bestCarrier = carrier;
                    bestEta = etaDays;
                    bestReliability = reliability;
                }
            }

            String answer =
                    String.format(
                            "%.2f",
                            bestFee);

            answer =
                    bestCarrier
                    + "|"
                    + answer
                    + "|"
                    + bestEta;

            System.out.println("\nANSWER:");
            System.out.println(answer);

           String body =
        "{"
        + "\"studentCode\":\"" + STUDENT_CODE + "\","
        + "\"qCode\":\"" + QCODE + "\","
        + "\"requestId\":\"" + requestId + "\","
        + "\"answer\":\"" + answer + "\""
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
System.out.println(submitResponse.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/object để xử lý các bài toán với đối tượng. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/object?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `orderId`, `weightKg`, `maxEtaDays` và `quotes`.

//c. Chỉ xét quote có `etaDays <= maxEtaDays`, tính phí và chọn quote rẻ nhất; nếu hòa thì chọn reliability cao hơn.

//d. Gửi POST /api/rest/object/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu quote tốt nhất là `C2` với phí `12.50` và `etaDays=3` thì `answer` là `C2|12.50|3`.
