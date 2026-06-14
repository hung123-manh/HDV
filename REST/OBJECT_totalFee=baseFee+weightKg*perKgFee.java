import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Ek8BLSuO {

    static final String STUDENT_CODE = "B22DCAT134";
    static final String QCODE = "Ek8BLSuO";

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

                double totalFee =
                        Math.round(
                                (baseFee
                                + weightKg * perKgFee)
                                * 100.0)
                        / 100.0;

                if (totalFee < bestFee
                        || (Math.abs(totalFee - bestFee) < 1e-9
                        && reliability > bestReliability)) {

                    bestFee = totalFee;
                    bestCarrier = carrier;
                    bestEta = etaDays;
                    bestReliability = reliability;
                }
            }

    JsonObject answerObj =
        new JsonObject();

answerObj.addProperty(
        "carrier",
        bestCarrier);

answerObj.addProperty(
        "totalFee",
        Double.parseDouble(
                String.format(
                        java.util.Locale.US,
                        "%.2f",
                        bestFee)));

answerObj.addProperty(
        "etaDays",
        bestEta);

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

//a. Gửi GET /api/rest/object?studentCode=<mã_sinh_viên>&qCode=<qAlias>. qCode là alias runtime được giao.

//b. Server trả về requestId và data gồm orderId, weightKg, maxEtaDays, quotes. Mỗi quote có carrier, baseFee, perKgFee, etaDays, reliability.

//c. Chỉ xét quote có etaDays <= maxEtaDays. Tính totalFee = baseFee + weightKg * perKgFee và làm tròn 2 chữ số thập phân.

//d. Chọn quote có totalFee nhỏ nhất; nếu bằng nhau, chọn quote có reliability cao hơn.

//e. Gửi POST /api/rest/object/submit với body JSON chứa studentCode, qCode, requestId và answer gồm carrier, totalFee, etaDays.
