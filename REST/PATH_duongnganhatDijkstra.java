import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class _767hljjO {

    static class Edge {
        String to;
        int w;

        Edge(String to, int w) {
            this.to = to;
            this.w = w;
        }
    }

    static class Node {
        String id;
        int dist;

        Node(String id, int dist) {
            this.id = id;
            this.dist = dist;
        }
    }

    public static void main(String[] args) throws Exception {

        String studentCode = "B22DCAT134";
        String qCode = "767hljjO";
        String baseUrl = "http://36.50.135.242:2230/api/rest/path";

        URL getUrl = new URL(baseUrl + "?studentCode=" + studentCode + "&qCode=" + qCode);

        HttpURLConnection conn = (HttpURLConnection)getUrl.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;

        while((line = br.readLine()) != null){
            sb.append(line);
        }

        br.close();

        String json = sb.toString();

        String reqTag = "\"requestId\":\"";
        int reqStart = json.indexOf(reqTag) + reqTag.length();
        int reqEnd = json.indexOf("\"", reqStart);
        String requestId = json.substring(reqStart, reqEnd);

        String startTag = "\"start\":\"";
        int startStart = json.indexOf(startTag) + startTag.length();
        int startEnd = json.indexOf("\"", startStart);
        String start = json.substring(startStart, startEnd);

        String endTag = "\"end\":\"";
        int endStart = json.indexOf(endTag) + endTag.length();
        int endEnd = json.indexOf("\"", endStart);
        String end = json.substring(endStart, endEnd);

        HashMap<String, ArrayList<Edge>> graph = new HashMap<>();

        int edgesPos = json.indexOf("\"edges\"");
        int arrStart = json.indexOf("[", edgesPos) + 1;
        int arrEnd = json.indexOf("]", arrStart);

        String edgesStr = json.substring(arrStart, arrEnd);

        String[] edges = edgesStr.split("\\},\\{");

        for(String e : edges){

            e = e.replace("{","").replace("}","");

            String fromTag = "\"from\":\"";
            int fromStart = e.indexOf(fromTag) + fromTag.length();
            int fromEnd = e.indexOf("\"", fromStart);
            String from = e.substring(fromStart, fromEnd);

            String toTag = "\"to\":\"";
            int toStart = e.indexOf(toTag) + toTag.length();
            int toEnd = e.indexOf("\"", toStart);
            String to = e.substring(toStart, toEnd);

            String wTag = "\"weight\":";
            int wStart = e.indexOf(wTag) + wTag.length();
            int wEnd = wStart;

            while(wEnd < e.length() && Character.isDigit(e.charAt(wEnd))){
                wEnd++;
            }

            int w = Integer.parseInt(e.substring(wStart, wEnd));

            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(new Edge(to, w));
        }

        HashMap<String,Integer> dist = new HashMap<>();
        HashMap<String,String> parent = new HashMap<>();

        PriorityQueue<Node> pq =
                new PriorityQueue<>((a,b) -> a.dist - b.dist);

        dist.put(start, 0);
        pq.offer(new Node(start, 0));

        while(!pq.isEmpty()){

            Node cur = pq.poll();

            if(cur.dist > dist.getOrDefault(cur.id, Integer.MAX_VALUE)){
                continue;
            }

            if(!graph.containsKey(cur.id)){
                continue;
            }

            for(Edge ed : graph.get(cur.id)){

                int nd = cur.dist + ed.w;

                if(nd < dist.getOrDefault(ed.to, Integer.MAX_VALUE)){

                    dist.put(ed.to, nd);
                    parent.put(ed.to, cur.id);

                    pq.offer(new Node(ed.to, nd));
                }
            }
        }

        ArrayList<String> path = new ArrayList<>();

        String cur = end;

        while(cur != null){
            path.add(cur);
            cur = parent.get(cur);
        }

        Collections.reverse(path);

        StringBuilder pathStr = new StringBuilder();

        for(int i=0;i<path.size();i++){
            pathStr.append(path.get(i));

            if(i < path.size()-1){
                pathStr.append("->");
            }
        }

        String answer = dist.get(end) + "|" + pathStr;

        System.out.println("Answer = " + answer);

        String submitUrl =
                baseUrl
                + "/submit"
                + "?studentCode=" + studentCode
                + "&qCode=" + qCode
                + "&requestId=" + requestId
                + "&answer=" + URLEncoder.encode(answer,"UTF-8");

        URL submit = new URL(submitUrl);

        HttpURLConnection submitConn =
                (HttpURLConnection)submit.openConnection();

        submitConn.setRequestMethod("GET");

        BufferedReader resultReader;

        try{
            resultReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    submitConn.getInputStream()));
        }catch(Exception ex){

            resultReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    submitConn.getErrorStream()));
        }

        StringBuilder result = new StringBuilder();

        while((line = resultReader.readLine()) != null){
            result.append(line);
        }

        resultReader.close();

        System.out.println(result.toString());
    }
}
//Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/path để xử lý các bài toán lựa chọn bản ghi và tìm đường đi. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.

//a. Gửi GET /api/rest/path?studentCode=<mã_sinh_viên>&qCode=<qAlias> để nhận JSON gồm requestId và data.

//b. Server trả về `data` là object gồm `nodes`, `edges`, `start`, `end`.

//c. Tính đường đi ngắn nhất giữa `start` và `end` bằng Dijkstra.

//d. Gửi POST /api/rest/path/submit với body JSON gồm studentCode, qCode, requestId và answer.

//e. Ví dụ: nếu chi phí là `21` và đường đi là `P1->P2->P5` thì `answer` là `21|P1->P2->P5`.
