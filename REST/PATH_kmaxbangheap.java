import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.PriorityQueue;

public class XTC6VLdk {

    static class Record {
        String id;
        int value;

        Record(String id, int value) {
            this.id = id;
            this.value = value;
        }
    }

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "XTC6VLdk";
        String baseUrl = "http://36.50.135.242:2230/api/rest/path";

        URL getUrl = new URL(
                baseUrl
                + "?studentCode="
                + studentCode
                + "&qCode="
                + qCode);

        HttpURLConnection conn =
                (HttpURLConnection) getUrl.openConnection();

        conn.setRequestMethod("GET");

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();

        String json = sb.toString();

        System.out.println(json);

        String reqTag = "\"requestId\":\"";
        int reqStart = json.indexOf(reqTag) + reqTag.length();
        int reqEnd = json.indexOf("\"", reqStart);
        String requestId = json.substring(reqStart, reqEnd);

        String typeTag = "\"type\":\"";
        int typeStart = json.lastIndexOf(typeTag) + typeTag.length();
        int typeEnd = json.indexOf("\"", typeStart);
        String targetType = json.substring(typeStart, typeEnd);

        String kTag = "\"k\":";
        int kStart = json.indexOf(kTag) + kTag.length();
        int kEnd = kStart;

        while (kEnd < json.length()
                && Character.isDigit(json.charAt(kEnd))) {
            kEnd++;
        }

        int k = Integer.parseInt(json.substring(kStart, kEnd));

        int recordsPos = json.indexOf("\"records\"");
        int recordsStart = json.indexOf("[", recordsPos) + 1;
        int recordsEnd = json.indexOf("]", recordsStart);

        String recordsStr =
                json.substring(recordsStart, recordsEnd);

        String[] records =
                recordsStr.split("\\},\\{");

        PriorityQueue<Record> pq =
                new PriorityQueue<>(
                        (a, b) -> b.value - a.value);

        for (String r : records) {

            r = r.replace("{", "")
                 .replace("}", "");

            String idTag = "\"id\":\"";
            int idStart = r.indexOf(idTag) + idTag.length();
            int idEnd = r.indexOf("\"", idStart);
            String id = r.substring(idStart, idEnd);

            String valueTag = "\"value\":";
            int valueStart =
                    r.indexOf(valueTag) + valueTag.length();

            int valueEnd = valueStart;

            while (valueEnd < r.length()
                    && Character.isDigit(r.charAt(valueEnd))) {
                valueEnd++;
            }

            int value =
                    Integer.parseInt(
                            r.substring(valueStart, valueEnd));

            String recTypeTag = "\"type\":\"";
            int recTypeStart =
                    r.indexOf(recTypeTag) + recTypeTag.length();

            int recTypeEnd =
                    r.indexOf("\"", recTypeStart);

            String recType =
                    r.substring(recTypeStart, recTypeEnd);

            if (recType.equals(targetType)) {
                pq.offer(new Record(id, value));
            }
        }

        Record selected = null;

        for (int i = 0; i < k; i++) {
            selected = pq.poll();
        }

        String answer =
                selected.id + "|" + selected.value;

        System.out.println("Answer = " + answer);

        String submitUrl =
                baseUrl
                + "/submit"
                + "?studentCode=" + studentCode
                + "&qCode=" + qCode
                + "&requestId=" + requestId
                + "&answer="
                + URLEncoder.encode(answer, "UTF-8");

        URL submit = new URL(submitUrl);

        HttpURLConnection submitConn =
                (HttpURLConnection) submit.openConnection();

        submitConn.setRequestMethod("GET");

        BufferedReader resultReader =
                new BufferedReader(
                        new InputStreamReader(
                                submitConn.getInputStream()));

        StringBuilder result =
                new StringBuilder();

        while ((line = resultReader.readLine()) != null) {
            result.append(line);
        }

        resultReader.close();

        System.out.println(result.toString());
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/path để xử lý các bài toán lựa chọn bản ghi và tìm đường đi. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/path?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `records`, `k` và `type`.

//c. Lọc theo `type`, rồi chọn phần tử thứ k theo giá trị lớn nhất bằng heap.

//d. Gửi POST /api/rest/path/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu phần tử được chọn là `K5` với giá trị `88` thì `answer` là `K5|88`.
