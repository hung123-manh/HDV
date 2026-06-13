import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class _6TMAgxUc {

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "6TMAgxUc";
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

        int dataPos = jsonStr.indexOf("\"data\"");
        int dataStart = jsonStr.indexOf("[", dataPos) + 1;
        int dataEnd = jsonStr.lastIndexOf("]");

        String dataStr = jsonStr.substring(dataStart, dataEnd);

        String[] transactions;

        if (dataStr.trim().length() == 0) {
            transactions = new String[0];
        } else {
            transactions = dataStr.split("\\},\\{");
        }

        double capturedTotal = 0;
        double refundedTotal = 0;
        int failedCount = 0;

        for (String t : transactions) {

            t = t.replace("{", "").replace("}", "");

            String amountTag = "\"amount\":";
            int amountStart = t.indexOf(amountTag) + amountTag.length();
            int amountEnd = amountStart;

            while (amountEnd < t.length() &&
                    ("0123456789.-".indexOf(t.charAt(amountEnd)) >= 0)) {
                amountEnd++;
            }

            double amount = Double.parseDouble(
                    t.substring(amountStart, amountEnd));

            String statusTag = "\"status\":\"";
            int statusStart = t.indexOf(statusTag) + statusTag.length();
            int statusEnd = t.indexOf("\"", statusStart);

            String status = t.substring(statusStart, statusEnd);

            if ("CAPTURED".equals(status)) {
                capturedTotal += amount;
            }

            if ("REFUNDED".equals(status)) {
                refundedTotal += amount;
            }

            if ("FAILED".equals(status)) {
                failedCount++;
            }
        }

        double netTotal = capturedTotal - refundedTotal;

        String answer =
                "{"
                + "\"capturedTotal\":"
                + String.format(Locale.US,"%.2f",capturedTotal)
                + ",\"refundedTotal\":"
                + String.format(Locale.US,"%.2f",refundedTotal)
                + ",\"netTotal\":"
                + String.format(Locale.US,"%.2f",netTotal)
                + ",\"failedCount\":"
                + failedCount
                + "}";

        System.out.println(answer);

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
                + "\"answer\":" + answer
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
//Một dịch vụ REST DataService được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/data để xử lý các bài toán với dữ liệu nguyên thủy qua HTTP/JSON.

//Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với DataService và thực hiện các công việc sau.

//a. Gửi GET /api/rest/data?studentCode=<mã_sinh_viên>&qCode=<qAlias>. Tham số qCode là alias runtime được giao trong hệ thống.

//b. Server trả về requestId và data là mảng giao dịch, mỗi giao dịch có transactionId, amount, currency, status.

//c. Tính capturedTotal là tổng tiền giao dịch CAPTURED, refundedTotal là tổng tiền giao dịch REFUNDED, netTotal = capturedTotal - refundedTotal, và failedCount là số giao dịch FAILED. Các giá trị tiền làm tròn 2 chữ số thập phân.

//d. Gửi POST /api/rest/data/submit với body JSON chứa studentCode, qCode, requestId và answer là object gồm capturedTotal, refundedTotal, netTotal, failedCount.

//e. Ví dụ answer hợp lệ: {"capturedTotal":120.50,"refundedTotal":20.00,"netTotal":100.50,"failedCount":1}.
