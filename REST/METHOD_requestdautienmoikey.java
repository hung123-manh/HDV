import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.ArrayList;

public class HITV0rOW {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "HITV0rOW";
        String baseUrl = "http://36.50.135.242:2230/api/rest/method";

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

        String reqTag = "\"requestId\":\"";
        int reqStart = jsonStr.indexOf(reqTag) + reqTag.length();
        int reqEnd = jsonStr.indexOf("\"", reqStart);
        String requestId = jsonStr.substring(reqStart, reqEnd);

        String capTag = "\"capacity\":";
        int capStart = jsonStr.indexOf(capTag) + capTag.length();
        int capEnd = capStart;

        while (capEnd < jsonStr.length() && Character.isDigit(jsonStr.charAt(capEnd))) {
            capEnd++;
        }

        int capacity = Integer.parseInt(jsonStr.substring(capStart, capEnd));

        int reqsStart = jsonStr.indexOf("\"requests\"");
        reqsStart = jsonStr.indexOf("[", reqsStart) + 1;

        int reqsEnd = jsonStr.lastIndexOf("]");

        String reqsStr = jsonStr.substring(reqsStart, reqsEnd);

        String[] requests = reqsStr.split("\\},\\{");

        LinkedHashMap<String, Boolean> cache = new LinkedHashMap<>(capacity, 0.75f, true);

        ArrayList<String> accepted = new ArrayList<>();

        for (String req : requests) {

            req = req.replace("{", "").replace("}", "");

            String idTag = "\"id\":\"";
            int idStart = req.indexOf(idTag) + idTag.length();
            int idEnd = req.indexOf("\"", idStart);
            String id = req.substring(idStart, idEnd);

            String keyTag = "\"key\":\"";
            int keyStart = req.indexOf(keyTag) + keyTag.length();
            int keyEnd = req.indexOf("\"", keyStart);
            String key = req.substring(keyStart, keyEnd);

            if (cache.containsKey(key)) {

                cache.get(key); // refresh LRU

            } else {

                accepted.add(id);

                if (cache.size() >= capacity) {
                    String oldest = cache.keySet().iterator().next();
                    cache.remove(oldest);
                }

                cache.put(key, true);
            }
        }

        StringBuilder answerBuilder = new StringBuilder();

        for (int i = 0; i < accepted.size(); i++) {

            answerBuilder.append(accepted.get(i));

            if (i < accepted.size() - 1) {
                answerBuilder.append(",");
            }
        }

        String answer = answerBuilder.toString();

        System.out.println("Answer = " + answer);

        URL putUrl = new URL(baseUrl + "/" + requestId);

        HttpURLConnection putConn = (HttpURLConnection) putUrl.openConnection();
        putConn.setRequestMethod("PUT");
        putConn.setRequestProperty("Content-Type", "application/json");
        putConn.setDoOutput(true);

        String jsonPut = "{"
                + "\"studentCode\":\"" + studentCode + "\","
                + "\"qCode\":\"" + qCode + "\","
                + "\"answer\":\"" + answer + "\""
                + "}";

        OutputStream os = putConn.getOutputStream();
        os.write(jsonPut.getBytes("UTF-8"));
        os.flush();
        os.close();

        int responseCode = putConn.getResponseCode();

        BufferedReader resultReader;

        if (responseCode >= 200 && responseCode < 300) {
            resultReader = new BufferedReader(new InputStreamReader(putConn.getInputStream()));
        } else {
            resultReader = new BufferedReader(new InputStreamReader(putConn.getErrorStream()));
        }

        StringBuilder result = new StringBuilder();
        String line;

        while ((line = resultReader.readLine()) != null) {
            result.append(line);
        }

        resultReader.close();

        System.out.println(result.toString());
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/method để xử lý các bài toán mô phỏng phương thức xử lý, trạng thái và quan hệ phụ thuộc. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/method?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `capacity` và `requests`.

//c. Duyệt các request theo thứ tự, chỉ nhận request đầu tiên của mỗi key và loại bỏ duplicate theo LRU cache.

//d. Gửi PUT /api/rest/method/{requestId} với body JSON gồm studentCode, qCode và answer.

//Lưu ý: requestId lấy từ phase 1 và truyền trên path.

//e. Ví dụ: nếu chỉ nhận được `REQ-101,REQ-205` thì `answer` là `REQ-101,REQ-205`.
