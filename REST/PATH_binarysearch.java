import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class mIi1GzEE {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "mIi1GzEE";
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

        String reqTag = "\"requestId\":\"";
        int reqStart = json.indexOf(reqTag) + reqTag.length();
        int reqEnd = json.indexOf("\"", reqStart);
        String requestId = json.substring(reqStart, reqEnd);

        String targetTag = "\"target\":";
        int targetStart = json.indexOf(targetTag) + targetTag.length();
        int targetEnd = targetStart;

        while (targetEnd < json.length()
                && Character.isDigit(json.charAt(targetEnd))) {
            targetEnd++;
        }

        int target =
                Integer.parseInt(
                        json.substring(targetStart, targetEnd));

        int recordsPos = json.indexOf("\"records\"");
        int recordsStart = json.indexOf("[", recordsPos) + 1;
        int recordsEnd = json.indexOf("]", recordsStart);

        String recordsStr =
                json.substring(recordsStart, recordsEnd);

        String[] records =
                recordsStr.split("\\},\\{");

        String answer = "";

        for (String r : records) {

            r = r.replace("{", "")
                 .replace("}", "");

            String idTag = "\"id\":\"";
            int idStart = r.indexOf(idTag) + idTag.length();
            int idEnd = r.indexOf("\"", idStart);
            String id = r.substring(idStart, idEnd);

            String thTag = "\"threshold\":";
            int thStart = r.indexOf(thTag) + thTag.length();
            int thEnd = thStart;

            while (thEnd < r.length()
                    && Character.isDigit(r.charAt(thEnd))) {
                thEnd++;
            }

            int threshold =
                    Integer.parseInt(
                            r.substring(thStart, thEnd));

            if (threshold >= target) {
                answer = id;
                break;
            }
        }

        System.out.println("Answer = " + answer);

        String submitUrl =
                baseUrl
                + "/submit"
                + "?studentCode=" + studentCode
                + "&qCode=" + qCode
                + "&requestId=" + requestId
                + "&answer=" + answer;

        URL url2 = new URL(submitUrl);

        HttpURLConnection conn2 =
                (HttpURLConnection) url2.openConnection();

        conn2.setRequestMethod("GET");

        BufferedReader br2 =
                new BufferedReader(
                        new InputStreamReader(
                                conn2.getInputStream()));

        StringBuilder result = new StringBuilder();

        while ((line = br2.readLine()) != null) {
            result.append(line);
        }

        br2.close();

        System.out.println(result.toString());
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/path để xử lý các bài toán lựa chọn bản ghi và tìm đường đi. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/path?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `records` và `target`.

//c. Dùng binary search trên danh sách đã sắp xếp để tìm bản ghi đầu tiên có giá trị không nhỏ hơn `target`.

//d. Gửi POST /api/rest/path/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu chọn bản ghi `R3` thì `answer` là `R3`.
