package com.mycompany.grpca;

import GRPC.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GRPCA {

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 2240;

        String studentCode = "B22DCAT134";
        String qCode = "lUZzyEZ8";

        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress(host, port)
                        .usePlaintext()
                        .build();

        JudgeServiceGrpc.JudgeServiceBlockingStub stub =
                JudgeServiceGrpc.newBlockingStub(channel);

        JudgeRequest request =
                JudgeRequest.newBuilder()
                        .setStudentCode(studentCode)
                        .setQuestionAlias(qCode)
                        .build();

        JudgeResponse response =
                stub.request(request);

        String requestId = response.getRequestId();
        String json = response.getData();

        System.out.println("REQUEST ID = " + requestId);
        System.out.println("DATA = " + json);

        double price = parseValue(json, "price");
        double taxRate = parseValue(json, "taxRate");
        double discount = parseValue(json, "discount");

        double finalPrice =
                price * (1 + taxRate / 100.0)
                - discount;

        String answer =
                String.format("%.2f", finalPrice);

        System.out.println("ANSWER = " + answer);

        SubmitRequest submitRequest =
                SubmitRequest.newBuilder()
                        .setStudentCode(studentCode)
                        .setQuestionAlias(qCode)
                        .setRequestId(requestId)
                        .setAnswer(answer)
                        .build();

        SubmitResponse submitResponse =
                stub.submit(submitRequest);

        System.out.println("STATUS = "
                + submitResponse.getStatus());

        System.out.println("MESSAGE = "
                + submitResponse.getMessage());

        channel.shutdown();
    }

    private static double parseValue(
            String json,
            String key) {

        String pattern =
                "\"" + key + "\":";

        int start =
                json.indexOf(pattern)
                + pattern.length();

        int end =
                json.indexOf(",", start);

        if (end == -1) {
            end = json.indexOf("}", start);
        }

        return Double.parseDouble(
                json.substring(start, end)
                        .trim());
    }
}
//Một dịch vụ gRPC JudgeService được triển khai trên server tại <Exam_IP>:2240 để xử lý các bài toán với đối tượng qua Protocol Buffers.

//Yêu cầu: Viết chương trình tại máy trạm (gRPC client, plaintext, không TLS) để giao tiếp với JudgeService và thực hiện các công việc sau.

//a. Tạo gRPC client plaintext tới cổng 2240 và gọi JudgeService.Request với mã sinh viên và alias runtime được giao.

//b. Server trả về request_id và data là chuỗi JSON sản phẩm gồm name, price, taxRate, discount.

//c. Tính finalPrice = price * (1 + taxRate / 100) - discount. Trong bài này discount là số tiền giảm trực tiếp, không phải phần trăm.

//d. Gọi JudgeService.Submit với answer là finalPrice dạng chuỗi, làm tròn 2 chữ số thập phân.

//e. Sai số chấp nhận tối đa 0.01.
