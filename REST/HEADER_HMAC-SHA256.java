import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class qi0HYqph {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "qi0HYqph";
        String baseUrl = "http://36.50.135.242:2230/api/rest/header";

        String getUrlStr = baseUrl + "?studentCode=" + studentCode + "&qCode=" + qCode;

        URL getUrl = new URL(getUrlStr);
        HttpURLConnection getConn = (HttpURLConnection) getUrl.openConnection();
        getConn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(getConn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            response.append(line);
        }

        in.close();

        String jsonStr = response.toString();

        String reqTag = "\"requestId\":\"";
        int reqStart = jsonStr.indexOf(reqTag) + reqTag.length();
        int reqEnd = jsonStr.indexOf("\"", reqStart);
        String requestId = jsonStr.substring(reqStart, reqEnd);

        String nonceTag = "\"nonce\":\"";
        int nonceStart = jsonStr.indexOf(nonceTag) + nonceTag.length();
        int nonceEnd = jsonStr.indexOf("\"", nonceStart);
        String nonce = jsonStr.substring(nonceStart, nonceEnd);

        String keyTag = "\"signingKey\":\"";
        int keyStart = jsonStr.indexOf(keyTag) + keyTag.length();
        int keyEnd = jsonStr.indexOf("\"", keyStart);
        String signingKey = jsonStr.substring(keyStart, keyEnd);

        int eventsPos = jsonStr.indexOf("\"events\"");
        int eventsStart = jsonStr.indexOf("[", eventsPos) + 1;
        int eventsEnd = jsonStr.indexOf("]", eventsStart);

        String eventsStr = jsonStr.substring(eventsStart, eventsEnd);

        String[] events;

        if (eventsStr.trim().length() == 0) {
            events = new String[0];
        } else {
            events = eventsStr.replace("\"", "").split(",");
        }

        StringBuilder payloadBuilder = new StringBuilder();

        payloadBuilder.append(nonce);
        payloadBuilder.append(":");

        for (int i = 0; i < events.length; i++) {

            payloadBuilder.append(events[i].trim());

            if (i < events.length - 1) {
                payloadBuilder.append("|");
            }
        }

        payloadBuilder.append(":");
        payloadBuilder.append(studentCode.toUpperCase());

        String payload = payloadBuilder.toString();

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKey =
                new SecretKeySpec(
                        signingKey.getBytes("UTF-8"),
                        "HmacSHA256");

        mac.init(secretKey);

        byte[] hash =
                mac.doFinal(payload.getBytes("UTF-8"));

        StringBuilder hex = new StringBuilder();

        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }

        String answer = hex.toString();

        System.out.println("Payload = " + payload);
        System.out.println("Answer = " + answer);

        URL postUrl = new URL(baseUrl + "/submit");

        HttpURLConnection postConn =
                (HttpURLConnection) postUrl.openConnection();

        postConn.setRequestMethod("POST");
        postConn.setRequestProperty("Content-Type", "application/json");
        postConn.setDoOutput(true);

        String jsonPost =
                "{"
                + "\"studentCode\":\"" + studentCode + "\","
                + "\"qCode\":\"" + qCode + "\","
                + "\"requestId\":\"" + requestId + "\","
                + "\"answer\":\"" + answer + "\""
                + "}";

        OutputStream os = postConn.getOutputStream();
        os.write(jsonPost.getBytes("UTF-8"));
        os.flush();
        os.close();

        BufferedReader resultReader =
                new BufferedReader(
                        new InputStreamReader(
                                postConn.getInputStream()));

        StringBuilder result = new StringBuilder();

        while ((line = resultReader.readLine()) != null) {
            result.append(line);
        }

        resultReader.close();

        System.out.println(result.toString());
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/header để xử lý các bài toán mã kiểm tra, chữ ký và băm dữ liệu. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/header?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `nonce`, `signingKey` và `events`.

//c. Tạo payload `nonce:event1|event2|...:STUDENT_CODE_UPPER` rồi tính HMAC-SHA256.

//. Gửi POST /api/rest/header/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu chữ ký hex là `9f0a...` thì `answer` là chuỗi hex đó.
