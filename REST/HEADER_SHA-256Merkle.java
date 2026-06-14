import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;

public class dabdkKEz {

    public static byte[] sha256Bytes(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(data);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "dabdkKEz";
        String baseUrl = "http://36.50.135.242:2230/api/rest/header";

        String getUrlStr =
                baseUrl
                + "?studentCode="
                + studentCode
                + "&qCode="
                + qCode;

        URL getUrl = new URL(getUrlStr);

        HttpURLConnection getConn =
                (HttpURLConnection) getUrl.openConnection();

        getConn.setRequestMethod("GET");

        BufferedReader in =
                new BufferedReader(
                        new InputStreamReader(
                                getConn.getInputStream()));

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

        String requestId =
                jsonStr.substring(reqStart, reqEnd);

        int leavesPos = jsonStr.indexOf("\"leaves\"");
        int leavesStart = jsonStr.indexOf("[", leavesPos) + 1;
        int leavesEnd = jsonStr.indexOf("]", leavesStart);

        String leavesStr =
                jsonStr.substring(leavesStart, leavesEnd);

        String[] leaves;

        if (leavesStr.trim().length() == 0) {
            leaves = new String[0];
        } else {
            leaves = leavesStr.replace("\"", "").split(",");
        }

        ArrayList<byte[]> level = new ArrayList<>();

        for (String leaf : leaves) {
            level.add(
                    sha256Bytes(
                            leaf.trim().getBytes("UTF-8")));
        }

        while (level.size() > 1) {

            ArrayList<byte[]> next = new ArrayList<>();

            for (int i = 0; i < level.size(); i += 2) {

                byte[] left = level.get(i);

                byte[] right;

                if (i + 1 < level.size()) {
                    right = level.get(i + 1);
                } else {
                    right = left;
                }

                byte[] combined =
                        new byte[left.length + right.length];

                System.arraycopy(
                        left,
                        0,
                        combined,
                        0,
                        left.length);

                System.arraycopy(
                        right,
                        0,
                        combined,
                        left.length,
                        right.length);

                next.add(
                        sha256Bytes(combined));
            }

            level = next;
        }

        String answer = "";

        if (!level.isEmpty()) {
            answer = bytesToHex(level.get(0));
        }

        System.out.println("Answer = " + answer);

        URL postUrl =
                new URL(baseUrl + "/submit");

        HttpURLConnection postConn =
                (HttpURLConnection) postUrl.openConnection();

        postConn.setRequestMethod("POST");
        postConn.setRequestProperty(
                "Content-Type",
                "application/json");

        postConn.setDoOutput(true);

        String jsonPost =
                "{"
                + "\"studentCode\":\"" + studentCode + "\","
                + "\"qCode\":\"" + qCode + "\","
                + "\"requestId\":\"" + requestId + "\","
                + "\"answer\":\"" + answer + "\""
                + "}";

        OutputStream os =
                postConn.getOutputStream();

        os.write(jsonPost.getBytes("UTF-8"));
        os.flush();
        os.close();

        BufferedReader resultReader;

        if (postConn.getResponseCode() >= 200
                && postConn.getResponseCode() < 300) {

            resultReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    postConn.getInputStream()));
        } else {

            resultReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    postConn.getErrorStream()));
        }

        StringBuilder result =
                new StringBuilder();

        while ((line = resultReader.readLine()) != null) {
            result.append(line);
        }

        resultReader.close();

        System.out.println(result.toString());
    }
}
/Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/header để xử lý các bài toán mã kiểm tra, chữ ký và băm dữ liệu. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/header?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `leaves`.

//c. Băm từng lá bằng SHA-256 rồi ghép cặp lên dần để lấy Merkle root.

//d. Gửi POST /api/rest/header/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu root hex là `abcd1234...` thì `answer` là chuỗi hex đó.
