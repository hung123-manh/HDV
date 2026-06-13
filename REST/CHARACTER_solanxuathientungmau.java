import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class yMIB2T59 {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "yMIB2T59";
        String baseUrl = "http://36.50.135.242:2230/api/rest/character";

        String getUrlStr = baseUrl + "?studentCode=" + studentCode + "&qCode=" + qCode;

        URL getUrl = new URL(getUrlStr);
        HttpURLConnection getConn = (HttpURLConnection) getUrl.openConnection();
        getConn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(getConn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        String jsonStr = response.toString();

        String requestId = "";

        String reqTag = "\"requestId\":\"";
        int reqStart = jsonStr.indexOf(reqTag) + reqTag.length();
        int reqEnd = jsonStr.indexOf("\"", reqStart);
        requestId = jsonStr.substring(reqStart, reqEnd);

        String textTag = "\"text\":\"";
        int textStart = jsonStr.indexOf(textTag) + textTag.length();
        int textEnd = jsonStr.indexOf("\"", textStart);
        String text = jsonStr.substring(textStart, textEnd);

        int patternsStart = jsonStr.lastIndexOf("[") + 1;
        int patternsEnd = jsonStr.lastIndexOf("]");
        String patternsStr = jsonStr.substring(patternsStart, patternsEnd);

        String[] patterns = patternsStr.replace("\"", "").split(",");

        StringBuilder answerBuilder = new StringBuilder();

        for (int p = 0; p < patterns.length; p++) {

            String pattern = patterns[p].trim();
            int count = 0;

            for (int i = 0; i <= text.length() - pattern.length(); i++) {

                if (text.substring(i, i + pattern.length()).equals(pattern)) {
                    count++;
                }
            }

            answerBuilder.append(pattern).append("=").append(count);

            if (p < patterns.length - 1) {
                answerBuilder.append("|");
            }
        }

        String answer = answerBuilder.toString();

        URL postUrl = new URL(baseUrl + "/submit");
        HttpURLConnection postConn = (HttpURLConnection) postUrl.openConnection();

        postConn.setRequestMethod("POST");
        postConn.setRequestProperty("Content-Type", "application/json");
        postConn.setDoOutput(true);

        String jsonPost = "{"
                + "\"studentCode\":\"" + studentCode + "\","
                + "\"qCode\":\"" + qCode + "\","
                + "\"requestId\":\"" + requestId + "\","
                + "\"answer\":\"" + answer + "\""
                + "}";

        OutputStream os = postConn.getOutputStream();
        os.write(jsonPost.getBytes("UTF-8"));
        os.flush();
        os.close();

        int responseCode = postConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {

            BufferedReader postIn = new BufferedReader(new InputStreamReader(postConn.getInputStream()));
            String postLine;
            StringBuilder postResponse = new StringBuilder();

            while ((postLine = postIn.readLine()) != null) {
                postResponse.append(postLine);
            }

            postIn.close();

            System.out.println("Answer = " + answer);
            System.out.println(postResponse.toString());
        }
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/character để xử lý các bài toán về chuỗi và ký tự. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/character?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `text` và `patterns`.

//c. Đếm số lần xuất hiện của từng mẫu trong `patterns` trong `text` bằng khớp đa mẫu.

//d. Gửi POST /api/rest/character/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu `text="ababa"` và `patterns=["aba","ba"]` thì `answer` là `aba=2|ba=2`.
