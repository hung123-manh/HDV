package com.mycompany.grpca;

import GRPC.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Arrays;

public class GRPCA {

    public static void main(String[] args) {

        String host = "36.50.135.242";
        int port = 2240;

        String studentCode = "B22DCAT134";
        String qCode = "Z66auonM";

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
        String data = response.getData();

        System.out.println("REQUEST ID = " + requestId);
        System.out.println("DATA = " + data);

        String[] words = data.split(",");

        Arrays.sort(words, String.CASE_INSENSITIVE_ORDER);

        String answer = String.join(",", words);

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

        System.out.println("STATUS = " + submitResponse.getStatus());
        System.out.println("MESSAGE = " + submitResponse.getMessage());

        channel.shutdown();
    }
}
//Một dịch vụ gRPC JudgeService được triển khai trên server tại <Exam_IP>:2240 để xử lý các bài toán về chuỗi và ký tự qua Protocol Buffers.

//Yêu cầu: Viết chương trình tại máy trạm (gRPC client, plaintext, không TLS) để giao tiếp với JudgeService và thực hiện các công việc sau.

//a. Tạo gRPC client plaintext tới cổng 2240 và gọi JudgeService.Request với mã sinh viên và alias runtime được giao.

//b. Server trả về request_id và data là chuỗi các từ phân tách bằng dấu phẩy.

//c. Tách danh sách từ và sắp xếp theo thứ tự từ điển không phân biệt hoa thường.

//d. Gọi JudgeService.Submit với answer là các từ đã sắp xếp, nối bằng dấu phẩy.

//e. Ví dụ: data="banana,apple,cherry" thì answer="apple,banana,cherry".
