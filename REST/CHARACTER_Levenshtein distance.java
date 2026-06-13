import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class _2LbXkLbd {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "2LbXkLbd";
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

        String sourceTag = "\"source\":\"";
        int sourceStart = jsonStr.indexOf(sourceTag) + sourceTag.length();
        int sourceEnd = jsonStr.indexOf("\"", sourceStart);
        String source = jsonStr.substring(sourceStart, sourceEnd);

        String targetTag = "\"target\":\"";
        int targetStart = jsonStr.indexOf(targetTag) + targetTag.length();
        int targetEnd = jsonStr.indexOf("\"", targetStart);
        String target = jsonStr.substring(targetStart, targetEnd);

        int n = source.length();
        int m = target.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {

                if (source.charAt(i - 1) == target.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1])
                    );
                }
            }
        }

        String answer = String.valueOf(dp[n][m]);

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

//b. Server trả về `data` là object gồm `source` và `target`.

//c. Tính Levenshtein distance giữa hai chuỗi.

//d. Gửi POST /api/rest/character/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu `source="kitten"` và `target="sitting"` thì `answer` là `3`.
