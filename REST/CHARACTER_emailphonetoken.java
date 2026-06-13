import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class y5ck2Ypk {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "y5ck2Ypk";
        String baseUrl = "http://36.50.135.242:2230/api/rest/character";

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

        String dataTag = "\"data\":\"";
        int dataStart = jsonStr.indexOf(dataTag) + dataTag.length();
        int dataEnd = jsonStr.lastIndexOf("\"");

        String data = jsonStr.substring(dataStart, dataEnd);

        data = data.replaceAll(
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
                "[EMAIL]"
        );

        data = data.replaceAll(
                "\\b0\\d{9}\\b",
                "[PHONE]"
        );

        data = data.replaceAll(
                "token=[^\\s|]+",
                "token=[TOKEN]"
        );

        String answer = data;

        System.out.println("Answer = " + answer);

        URL postUrl = new URL(baseUrl + "/submit");

        HttpURLConnection postConn = (HttpURLConnection) postUrl.openConnection();
        postConn.setRequestMethod("POST");
        postConn.setRequestProperty("Content-Type", "application/json");
        postConn.setDoOutput(true);

        String jsonPost =
                "{"
                + "\"studentCode\":\"" + studentCode + "\","
                + "\"qCode\":\"" + qCode + "\","
                + "\"requestId\":\"" + requestId + "\","
                + "\"answer\":\""
                + answer.replace("\\", "\\\\").replace("\"", "\\\"")
                + "\""
                + "}";

        OutputStream os = postConn.getOutputStream();
        os.write(jsonPost.getBytes("UTF-8"));
        os.flush();
        os.close();

        BufferedReader resultReader =
                new BufferedReader(new InputStreamReader(postConn.getInputStream()));

        StringBuilder result = new StringBuilder();

        while ((line = resultReader.readLine()) != null) {
            result.append(line);
        }

        resultReader.close();

        System.out.println(result.toString());
    }
}
//Một dịch vụ REST CharacterService được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/character để xử lý các bài toán về chuỗi và ký tự qua HTTP/JSON.

//Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với CharacterService và thực hiện các công việc sau.

//a. Gửi GET /api/rest/character?studentCode=<mã_sinh_viên>&qCode=<qAlias>. qCode là alias runtime được giao.

//b. Server trả về requestId và data là nhiều dòng log nối bằng ||. Mỗi dòng có thể chứa email, số điện thoại Việt Nam 10 chữ số bắt đầu bằng 0, và token dạng token=<giá_trị>.

//c. Thay email bằng [EMAIL], số điện thoại bằng [PHONE], token bằng token=[TOKEN]. Giữ nguyên thứ tự các dòng.

//d. Gửi POST /api/rest/character/submit với body JSON chứa studentCode, qCode, requestId và answer là chuỗi log sau khi che dữ liệu, các dòng vẫn nối bằng ||.

//e. Ví dụ một phần kết quả: INFO user=[EMAIL] phone=[PHONE] token=[TOKEN].
