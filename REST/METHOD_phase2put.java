import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class d0cS7coW {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "d0cS7coW";
        String baseUrl = "http://36.50.135.242:2230/api/rest/method";

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

        String answer =
                "{"
                + "\"status\":\"ACTIVE\","
                + "\"activatedBy\":\"" + studentCode.toUpperCase() + "\""
                + "}";

        URL putUrl = new URL(baseUrl + "/" + requestId);

        HttpURLConnection putConn = (HttpURLConnection) putUrl.openConnection();
        putConn.setRequestMethod("PUT");
        putConn.setRequestProperty("Content-Type", "application/json");
        putConn.setDoOutput(true);

        String jsonPut =
                "{"
                + "\"studentCode\":\"" + studentCode + "\","
                + "\"qCode\":\"" + qCode + "\","
                + "\"answer\":" + answer
                + "}";

        OutputStream os = putConn.getOutputStream();
        os.write(jsonPut.getBytes("UTF-8"));
        os.flush();
        os.close();

        BufferedReader resultReader =
                new BufferedReader(new InputStreamReader(putConn.getInputStream()));

        StringBuilder result = new StringBuilder();

        while ((line = resultReader.readLine()) != null) {
            result.append(line);
        }

        resultReader.close();

        System.out.println(result.toString());
    }
}
//Một dịch vụ REST MethodService được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/method để kiểm tra cách sử dụng HTTP method trong quy trình submit.

//Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với MethodService và thực hiện các công việc sau.

//a. Gửi GET /api/rest/method?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận dữ liệu kích hoạt tài khoản. qCode là alias runtime được giao.

//b. Server trả về requestId và data gồm accountId, currentStatus, riskLevel, requiresAudit.

//c. Gửi phase 2 bằng đúng phương thức PUT tới /api/rest/method/{requestId}.

//d. Body JSON chứa studentCode, qCode và answer; trong đó answer.status bằng ACTIVE và answer.activatedBy bằng mã sinh viên đã gửi, viết hoa.

//e. Dùng phương thức khác như PATCH không được chấp nhận cho bài này.
