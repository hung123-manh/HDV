package com.mycompany.grpca;

import GRPC.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.*;

public class TypedTextBatch {

    public static void main(String[] args) {

        String studentCode = "B22DCAT134";
        String qCode = "RQEoeocF";

        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("36.50.135.242", 2240)
                        .usePlaintext()
                        .build();

        TypedJudgeServiceGrpc.TypedJudgeServiceBlockingStub stub =
                TypedJudgeServiceGrpc.newBlockingStub(channel);

        TypedJudgeRequest request =
                TypedJudgeRequest.newBuilder()
                        .setStudentCode(studentCode)
                        .setQuestionAlias(qCode)
                        .build();

        TypedJudgeResponse response =
                stub.requestTyped(request);

        TextBatchData data =
                response.getTextBatch();

        String[] tags = {
            "account",
            "payment",
            "refund",
            "shipping"
        };

        Map<String, Integer> counts =
                new HashMap<>();

        for (String tag : tags) {
            counts.put(tag, 0);
        }

        for (String entry : data.getEntriesList()) {

            String lower =
                    entry.toLowerCase();

            for (String tag : tags) {

                if (lower.contains(tag)) {

                    counts.put(
                            tag,
                            counts.get(tag) + 1);
                }
            }
        }

        List<String> values =
                new ArrayList<>();

        for (String tag : tags) {

            if (counts.get(tag) > 0) {

                values.add(tag);
            }
        }

        Collections.sort(values);

        Map<String, Integer> finalCounts =
                new HashMap<>();

        for (String tag : values) {

            finalCounts.put(
                    tag,
                    counts.get(tag));
        }

        TextBatchAnswer answer =
                TextBatchAnswer.newBuilder()
                        .putAllCounts(finalCounts)
                        .addAllValues(values)
                        .build();

        TypedSubmitRequest submit =
                TypedSubmitRequest.newBuilder()
                        .setStudentCode(studentCode)
                        .setQuestionAlias(qCode)
                        .setRequestId(
                                response.getRequestId())
                        .setTextBatchAnswer(answer)
                        .build();

        TypedSubmitResponse result =
                stub.submitTyped(submit);

        System.out.println(
                "STATUS = "
                + result.getStatus());

        System.out.println(
                "MESSAGE = "
                + result.getMessage());

        channel.shutdown();
    }
}
//Một dịch vụ gRPC TypedJudgeService được triển khai trên server tại <Exam_IP>:2240 để xử lý các bài toán về chuỗi và ký tự có cấu trúc qua Protocol Buffers.

//Yêu cầu: Viết chương trình tại máy trạm (gRPC client, plaintext, không TLS) để giao tiếp với TypedJudgeService và thực hiện các công việc sau.

//a. Tạo gRPC client plaintext tới cổng 2240 và gọi TypedJudgeService.RequestTyped với mã sinh viên và alias runtime được giao.

//b. Server trả về request_id và text batch ở chế độ ticket_tags, gồm danh sách entries.

//c. Đếm số entry chứa từng nhãn account, payment, refund, shipping. So khớp không phân biệt hoa thường.

//d. Gọi TypedJudgeService.SubmitTyped với counts là map nhãn sang số lần xuất hiện và values là danh sách các nhãn có xuất hiện, theo thứ tự tăng dần theo tên nhãn.

//e. Không thêm nhãn có số lần xuất hiện bằng 0.
