package com.mycompany.grpca;

import GRPC.JudgeRequest;
import GRPC.JudgeResponse;
import GRPC.SubmitRequest;
import GRPC.SubmitResponse;
import GRPC.JudgeServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GRPCA {

    public static void main(String[] args) {

        String studentCode = "B22DCAT134";
        String qCode = "lo7ooSML";

        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("36.50.135.242", 2240)
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

        long sum = 0;

        for (String s : data.split(",")) {
            sum += Long.parseLong(s.trim());
        }

        String answer = String.valueOf(sum);

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
//Một dịch vụ gRPC JudgeService được triển khai trên server tại <Exam_IP>:2240 để xử lý các bài toán với dữ liệu nguyên thủy qua Protocol Buffers.

//Yêu cầu: Viết chương trình tại máy trạm (gRPC client, plaintext, không TLS) để giao tiếp với JudgeService và thực hiện các công việc sau.

//a. Tạo gRPC client plaintext tới server trên cổng 2240 và gọi JudgeService.Request với student_code là mã sinh viên và question_alias là alias runtime được giao.

//b. Server trả về request_id và data là chuỗi số nguyên phân tách bằng dấu phẩy.

//c. Tách data thành các số nguyên và tính tổng.

//d. Gọi JudgeService.Submit với đúng student_code, question_alias, request_id và answer là tổng dạng chuỗi.

//e. Ví dụ: data="1,2,3,4" thì answer="10".
