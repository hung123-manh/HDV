import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class fSFH7eDI {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "fSFH7eDI";
        String baseUrl = "http://36.50.135.242:2230/api/rest/data";

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

        int dataStart = jsonStr.indexOf("[") + 1;
        int dataEnd = jsonStr.indexOf("]");

        String dataStr = jsonStr.substring(dataStart, dataEnd);

        String[] numbers = dataStr.split(",");

        long sum = 0;

        for (String num : numbers) {
            sum += Long.parseLong(num.trim());
        }

        String answer = String.valueOf(sum);

        System.out.println("Answer = " + answer);

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

        BufferedReader resultReader = new BufferedReader(
                new InputStreamReader(postConn.getInputStream()));

        StringBuilder result = new StringBuilder();

        while ((line = resultReader.readLine()) != null) {
            result.append(line);
        }

        resultReader.close();

        System.out.println(result.toString());
    }
}
//Một dịch vụ REST DataService được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/data để xử lý các bài toán với dữ liệu nguyên thủy qua HTTP/JSON.

//Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với DataService và thực hiện các công việc sau.

//a. Gửi yêu cầu GET /api/rest/data?studentCode=<mã_sinh_viên>&qCode=<qAlias> tới server REST trên cổng 2230. Tham số qCode là alias được hệ thống giao, không dùng mã chuẩn của câu hỏi.

//b. Server trả về JSON gồm requestId và data là một mảng số nguyên.

//c. Tính tổng tất cả số nguyên trong mảng data.

//d. Gửi POST /api/rest/data/submit với body JSON chứa studentCode, qCode, requestId và answer là tổng vừa tính.

//e. Ví dụ: nếu data là [1,2,3,4] thì answer là 10.
