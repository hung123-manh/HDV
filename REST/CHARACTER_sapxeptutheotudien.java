import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

public class D7mb5qzI {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "D7mb5qzI";
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

        int wordsStart = jsonStr.indexOf("[") + 1;
        int wordsEnd = jsonStr.indexOf("]");
        String wordsStr = jsonStr.substring(wordsStart, wordsEnd);

        String[] words = wordsStr.replace("\"", "").split(",");

        HashMap<String, ArrayList<String>> map = new HashMap<>();

        for (String word : words) {

            word = word.trim();

            char[] arr = word.toCharArray();
            Arrays.sort(arr);

            String key = new String(arr);

            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }

            map.get(key).add(word);
        }

        ArrayList<String> groups = new ArrayList<>();

        for (ArrayList<String> group : map.values()) {

            Collections.sort(group);

            StringBuilder temp = new StringBuilder();

            for (int i = 0; i < group.size(); i++) {

                temp.append(group.get(i));

                if (i < group.size() - 1) {
                    temp.append(",");
                }
            }

            groups.add(temp.toString());
        }

        Collections.sort(groups);

        StringBuilder answerBuilder = new StringBuilder();

        for (int i = 0; i < groups.size(); i++) {

            answerBuilder.append(groups.get(i));

            if (i < groups.size() - 1) {
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

//b. Server trả về `data` là object gồm `words`.

//c. Nhóm các từ cùng chữ cái sau khi sắp xếp, rồi sắp xếp từng nhóm theo thứ tự từ điển.

//d. Gửi POST /api/rest/character/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu `words=["eat","tea","tan","ate"]` thì `answer` là `ate,eat,tea|tan`.
