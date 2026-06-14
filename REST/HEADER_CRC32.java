import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.CRC32;

public class YUeG6viG {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "YUeG6viG";
        String baseUrl = "http://36.50.135.242:2230/api/rest/header";

        String getUrl =
                baseUrl
                + "?studentCode="
                + studentCode
                + "&qCode="
                + qCode;

        HttpURLConnection conn =
                (HttpURLConnection)
                        new URL(getUrl)
                                .openConnection();

        conn.setRequestMethod("GET");

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()));

        StringBuilder sb =
                new StringBuilder();

        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();

        String json =
                sb.toString();

        System.out.println("GET:");
        System.out.println(json);

        String reqTag =
                "\"requestId\":\"";

        int reqStart =
                json.indexOf(reqTag)
                + reqTag.length();

        int reqEnd =
                json.indexOf(
                        "\"",
                        reqStart);

        String requestId =
                json.substring(
                        reqStart,
                        reqEnd);

        String payloadTag =
                "\"payload\":\"";

        int payloadStart =
                json.indexOf(payloadTag)
                + payloadTag.length();

        int payloadEnd =
                json.indexOf(
                        "\"",
                        payloadStart);

        String payload =
                json.substring(
                        payloadStart,
                        payloadEnd);

        CRC32 crc =
                new CRC32();

        crc.update(
                payload.getBytes(
                        "UTF-8"));

        String answer =
                Long.toHexString(
                        crc.getValue());

        System.out.println(
                "\nPAYLOAD = "
                + payload);

        System.out.println(
                "ANSWER = "
                + answer);

        String body =
                "{"
                + "\"studentCode\":\""
                + studentCode
                + "\","
                + "\"qCode\":\""
                + qCode
                + "\","
                + "\"requestId\":\""
                + requestId
                + "\","
                + "\"answer\":\""
                + answer
                + "\""
                + "}";

        HttpURLConnection postConn =
                (HttpURLConnection)
                        new URL(
                                baseUrl
                                + "/submit")
                                .openConnection();

        postConn.setRequestMethod(
                "POST");

        postConn.setRequestProperty(
                "Content-Type",
                "application/json");

        postConn.setDoOutput(true);

        OutputStream os =
                postConn.getOutputStream();

        os.write(
                body.getBytes(
                        "UTF-8"));

        os.flush();
        os.close();

        BufferedReader resultReader =
                new BufferedReader(
                        new InputStreamReader(
                                postConn.getInputStream()));

        StringBuilder result =
                new StringBuilder();

        while ((line =
                resultReader.readLine())
                != null) {

            result.append(line);
        }

        resultReader.close();

        System.out.println(
                "\nRESULT:");

        System.out.println(
                result.toString());
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/header để xử lý các bài toán mã kiểm tra, chữ ký và băm dữ liệu. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/header?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `payload` và một giá trị checksum để đối chiếu.

//c. Tính CRC32 của `payload` và nộp lại kết quả ở dạng hex chữ thường.

//d. Gửi POST /api/rest/header/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu CRC32 của `payload` là `1a2b3c4d` thì `answer` là `1a2b3c4d`.
