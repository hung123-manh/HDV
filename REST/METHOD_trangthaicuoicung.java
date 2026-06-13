import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class czdyjrO2 {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "czdyjrO2";
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

        String stateTag = "\"initialState\":\"";
        int stateStart = jsonStr.indexOf(stateTag) + stateTag.length();
        int stateEnd = jsonStr.indexOf("\"", stateStart);
        String currentState = jsonStr.substring(stateStart, stateEnd);

        int eventsStart = jsonStr.indexOf("\"events\"");
        eventsStart = jsonStr.indexOf("[", eventsStart) + 1;
        int eventsEnd = jsonStr.indexOf("]", eventsStart);

        String eventsStr = jsonStr.substring(eventsStart, eventsEnd);
        String[] events = eventsStr.replace("\"", "").split(",");

        int transStart = jsonStr.indexOf("\"transitions\"");
        transStart = jsonStr.indexOf("[", transStart) + 1;
        int transEnd = jsonStr.indexOf("]", transStart);

        String transStr = jsonStr.substring(transStart, transEnd);
        String[] transitions = transStr.replace("\"", "").split(",");

        HashMap<String,String> map = new HashMap<>();

        for(int i=0;i+2<transitions.length;i+=3){
            String from = transitions[i].trim();
            String event = transitions[i+1].trim();
            String to = transitions[i+2].trim();

            map.put(from + "#" + event, to);
        }

        for(String event : events){

            event = event.trim();

            String key = currentState + "#" + event;

            if(map.containsKey(key)){
                currentState = map.get(key);
            }
        }

        String answer = currentState;

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

        if(responseCode == HttpURLConnection.HTTP_OK){

            BufferedReader putIn = new BufferedReader(new InputStreamReader(putConn.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();

            while((line = putIn.readLine()) != null){
                result.append(line);
            }

            putIn.close();

            System.out.println("Answer = " + answer);
            System.out.println(result.toString());
        }
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/method để xử lý các bài toán mô phỏng phương thức xử lý, trạng thái và quan hệ phụ thuộc. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/method?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `initialState`, `events` và `transitions`.

//c. Mô phỏng máy trạng thái theo danh sách sự kiện để lấy trạng thái cuối cùng.

//d. Gửi PUT /api/rest/method/{requestId} với body JSON gồm studentCode, qCode và answer.

//Lưu ý: requestId lấy từ phase 1 và truyền trên path.

//e. Ví dụ: nếu trạng thái cuối là `ACTIVE` thì `answer` là `ACTIVE`.
