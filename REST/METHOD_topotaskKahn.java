import java.io.BufferedReader;
import java.util.PriorityQueue;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class t9ll1aj2 {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "t9ll1aj2";
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

        int tasksPos = jsonStr.indexOf("\"tasks\"");
        int tasksStart = jsonStr.indexOf("[", tasksPos) + 1;
        int tasksEnd = jsonStr.indexOf("]", tasksStart);

        String tasksStr = jsonStr.substring(tasksStart, tasksEnd);
        String[] tasks = tasksStr.replace("\"", "").split(",");

        HashMap<String, ArrayList<String>> graph = new HashMap<>();
        HashMap<String, Integer> indegree = new HashMap<>();

        for (String task : tasks) {
            task = task.trim();
            graph.put(task, new ArrayList<>());
            indegree.put(task, 0);
        }

        int depsPos = jsonStr.indexOf("\"deps\"");
        int depsStart = jsonStr.indexOf("[", depsPos) + 1;
        int depsEnd = jsonStr.lastIndexOf("]");

        String depsStr = jsonStr.substring(depsStart, depsEnd);

        String[] deps;

        if (depsStr.trim().length() == 0) {
            deps = new String[0];
        } else {
            deps = depsStr.split("\\},\\{");
        }

        for (String dep : deps) {

            dep = dep.replace("{", "").replace("}", "");

            String beforeTag = "\"before\":\"";
            int beforeStart = dep.indexOf(beforeTag) + beforeTag.length();
            int beforeEnd = dep.indexOf("\"", beforeStart);
            String before = dep.substring(beforeStart, beforeEnd);

            String afterTag = "\"after\":\"";
            int afterStart = dep.indexOf(afterTag) + afterTag.length();
            int afterEnd = dep.indexOf("\"", afterStart);
            String after = dep.substring(afterStart, afterEnd);

            graph.get(before).add(after);
            indegree.put(after, indegree.get(after) + 1);
        }

        PriorityQueue<String> queue = new PriorityQueue<>();

        for (String task : tasks) {
            task = task.trim();

            if (indegree.get(task) == 0) {
                queue.offer(task);
            }
        }

        ArrayList<String> topo = new ArrayList<>();

        while (!queue.isEmpty()) {

            String cur = queue.poll();
            topo.add(cur);

            for (String next : graph.get(cur)) {

                indegree.put(next, indegree.get(next) - 1);

                if (indegree.get(next) == 0) {
                    queue.offer(next);
                }
            }
        }

        StringBuilder answerBuilder = new StringBuilder();

        for (int i = 0; i < topo.size(); i++) {

            answerBuilder.append(topo.get(i));

            if (i < topo.size() - 1) {
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

//b. Server trả về `data` là object gồm `tasks` và `deps`.

//c. Sắp xếp topo các task bằng Kahn để tôn trọng mọi quan hệ phụ thuộc.

//d. Gửi PUT /api/rest/method/{requestId} với body JSON gồm studentCode, qCode và answer.

//Lưu ý: requestId lấy từ phase 1 và truyền trên path.

//e. Ví dụ: nếu thứ tự hợp lệ là `T1,T2,T4,T3` thì `answer` là `T1,T2,T4,T3`.

//Lưu ý: requestId lấy từ phase 1 và truyền trên path /api/rest/method/{requestId}.
