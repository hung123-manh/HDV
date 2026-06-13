import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class iKS7nsCn {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "iKS7nsCn";
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
        int dataEnd = jsonStr.indexOf("\"", dataStart);

        String data = jsonStr.substring(dataStart, dataEnd);

        String[] words = data.trim().split("\\s+");

        Arrays.sort(words);

        StringBuilder answerBuilder = new StringBuilder();

        for (int i = 0; i < words.length; i++) {

            answerBuilder.append(words[i]);

            if (i < words.length - 1) {
                answerBuilder.append(" ");
            }
        }

        String answer = answerBuilder.toString();

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
                + "\"answer\":\"" + answer + "\""
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

//a. Gửi GET /api/rest/character?studentCode=<mã_sinh_viên>&qCode=<qAlias> tới server REST. qCode phải là alias runtime được giao.

//b. Server trả về requestId và data là chuỗi gồm nhiều từ phân tách bằng khoảng trắng.

//c. Tách chuỗi thành danh sách từ và sắp xếp theo thứ tự từ điển có phân biệt hoa thường, đúng như dữ liệu nhận được.

//d. Gửi POST /api/rest/character/submit với body JSON chứa studentCode, qCode, requestId và answer là các từ đã sắp xếp, nối lại bằng một dấu cách.

//e. Ví dụ: "banana apple cherry" được submit thành "apple banana cherry".
