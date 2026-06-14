import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;

public class main {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "ap6N4TDT";
        String baseUrl = "http://36.50.135.242:2230/api/rest/method";

        String getUrl =
                baseUrl
                + "?studentCode="
                + studentCode
                + "&qCode="
                + qCode;

        HttpURLConnection getConn =
                (HttpURLConnection)
                        new URL(getUrl)
                                .openConnection();

        getConn.setRequestMethod("GET");

        BufferedReader in =
                new BufferedReader(
                        new InputStreamReader(
                                getConn.getInputStream()));

        StringBuilder response =
                new StringBuilder();

        String line;

        while ((line = in.readLine()) != null) {
            response.append(line);
        }

        in.close();

        String jsonStr =
                response.toString();

        System.out.println("GET:");
        System.out.println(jsonStr);

        JsonObject root =
                JsonParser.parseString(jsonStr)
                        .getAsJsonObject();

        String requestId =
                root.get("requestId")
                        .getAsString();

        JsonObject data =
                root.getAsJsonObject("data");

        String currentState =
                data.get("initialState")
                        .getAsString();

        JsonArray events =
                data.getAsJsonArray("events");

        JsonArray transitions =
                data.getAsJsonArray("transitions");

        HashMap<String, String> map =
                new HashMap<>();

        for (JsonElement e : transitions) {

            JsonObject t =
                    e.getAsJsonObject();

            String from =
                    t.get("from")
                            .getAsString();

            String event =
                    t.get("event")
                            .getAsString();

            String to =
                    t.get("to")
                            .getAsString();

            map.put(
                    from + "#" + event,
                    to
            );
        }

        for (JsonElement e : events) {

            String event =
                    e.getAsString();

            String key =
                    currentState
                    + "#"
                    + event;

            if (map.containsKey(key)) {

                currentState =
                        map.get(key);
            }
        }

        String answer =
                currentState;

        System.out.println("\nANSWER:");
        System.out.println(answer);

        String jsonPut =
                "{"
                + "\"studentCode\":\""
                + studentCode
                + "\","
                + "\"qCode\":\""
                + qCode
                + "\","
                + "\"answer\":\""
                + answer
                + "\""
                + "}";

        HttpURLConnection putConn =
                (HttpURLConnection)
                        new URL(
                                baseUrl
                                + "/"
                                + requestId)
                                .openConnection();

        putConn.setRequestMethod("PUT");
        putConn.setRequestProperty(
                "Content-Type",
                "application/json");
        putConn.setDoOutput(true);

        OutputStream os =
                putConn.getOutputStream();

        os.write(
                jsonPut.getBytes("UTF-8"));

        os.flush();
        os.close();

        BufferedReader resultReader =
                new BufferedReader(
                        new InputStreamReader(
                                putConn.getInputStream()));

        StringBuilder result =
                new StringBuilder();

        while ((line =
                resultReader.readLine()) != null) {

            result.append(line);
        }

        resultReader.close();

        System.out.println("\nRESULT:");
        System.out.println(result.toString());
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/method để xử lý các bài toán mô phỏng phương thức xử lý, trạng thái và quan hệ phụ thuộc. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/method?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `initialState`, `events` và `transitions`.

//c. Mô phỏng máy trạng thái theo danh sách sự kiện để lấy trạng thái cuối cùng.

//d. Gửi PUT /api/rest/method/{requestId} với body JSON gồm studentCode, qCode và answer.

//Lưu ý: requestId lấy từ phase 1 và truyền trên path.

//e. Ví dụ: nếu trạng thái cuối là `ACTIVE` thì `answer` là `ACTIVE`.
