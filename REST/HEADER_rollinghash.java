import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class GKG5x7vf {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "GKG5x7vf";
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

        String textTag = "\"text\":\"";
        int textStart = jsonStr.indexOf(textTag) + textTag.length();
        int textEnd = jsonStr.indexOf("\"", textStart);
        String text = jsonStr.substring(textStart, textEnd);

        String winTag = "\"windowSize\":";
        int winStart = jsonStr.indexOf(winTag) + winTag.length();
        int winEnd = winStart;

        while (winEnd < jsonStr.length() &&
                Character.isDigit(jsonStr.charAt(winEnd))) {
            winEnd++;
        }

        int windowSize =
                Integer.parseInt(
                        jsonStr.substring(winStart, winEnd));

        HashSet<String> seen = new HashSet<>();

String answer = "NONE";

for (int i = 0; i <= text.length() - windowSize; i++) {

    String sub = text.substring(i, i + windowSize);

    if (seen.contains(sub)) {
        answer = sub;
        break;
    }

    seen.add(sub);
}

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

//b. Server trả về `data` là object gồm `text` và `windowSize`.

//c. Dò chuỗi con độ dài `windowSize` đầu tiên xuất hiện lặp lại bằng rolling hash.

//d. Gửi POST /api/rest/header/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu chuỗi lặp đầu tiên là `abca` thì `answer` là `abca`.
