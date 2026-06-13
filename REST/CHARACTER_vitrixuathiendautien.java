import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class xSIdd5xi {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "xSIdd5xi";
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

        String patternTag = "\"pattern\":\"";
        int patternStart = jsonStr.indexOf(patternTag) + patternTag.length();
        int patternEnd = jsonStr.indexOf("\"", patternStart);
        String pattern = jsonStr.substring(patternStart, patternEnd);

        int m = pattern.length();
        int[] lps = new int[m];

        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        int answerPos = -1;

        int p1 = 0;
        int p2 = 0;

        while (p1 < text.length()) {

            if (text.charAt(p1) == pattern.charAt(p2)) {
                p1++;
                p2++;
            }

            if (p2 == pattern.length()) {
                answerPos = p1 - p2;
                break;
            }

            else if (p1 < text.length() && text.charAt(p1) != pattern.charAt(p2)) {

                if (p2 != 0) {
                    p2 = lps[p2 - 1];
                } else {
                    p1++;
                }
            }
        }

        String answer = String.valueOf(answerPos);

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

//b. Server trả về `data` là object gồm `text` và `pattern`.

//c. Tìm vị trí xuất hiện đầu tiên của `pattern` trong `text` bằng KMP.

//d. Gửi POST /api/rest/character/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu `text="alpha beta gamma"` và `pattern="beta"` thì `answer` là `6`.
