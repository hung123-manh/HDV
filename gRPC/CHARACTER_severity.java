package com.mycompany.grpca;

import GRPC.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypedTextBatch {

    public static void main(String[] args) {

        String studentCode = "B22DCAT134";
        String qCode = "vY9Riglc";

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

        Map<String, Integer> counts =
                new HashMap<>();

        String firstErrorCode = "";

        Pattern pattern =
                Pattern.compile("code=([A-Za-z0-9_-]+)");

        for (String line : data.getEntriesList()) {

            String severity = "";

            if (line.startsWith("INFO")) {
                severity = "INFO";
            } else if (line.startsWith("WARN")) {
                severity = "WARN";
            } else if (line.startsWith("ERROR")) {
                severity = "ERROR";
            }

            if (!severity.isEmpty()) {
                counts.put(
                        severity,
                        counts.getOrDefault(
                                severity,
                                0)
                        + 1);
            }

            if (firstErrorCode.isEmpty()) {

                Matcher matcher =
                        pattern.matcher(line);

                if (matcher.find()) {
                    firstErrorCode =
                            matcher.group(1);
                }
            }
        }

        TextBatchAnswer answer =
                TextBatchAnswer.newBuilder()
                        .putAllCounts(counts)
                        .addValues(firstErrorCode)
                        .build();

        TypedSubmitRequest submit =
                TypedSubmitRequest.newBuilder()
                        .setStudentCode(
                                studentCode)
                        .setQuestionAlias(
                                qCode)
                        .setRequestId(
                                response.getRequestId())
                        .setTextBatchAnswer(
                                answer)
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

//b. Server trả về request_id và text batch ở chế độ severity_counts, gồm danh sách dòng log trong entries.

//c. Đếm số dòng theo severity đầu dòng, ví dụ INFO, WARN, ERROR.

//d. Tìm mã lỗi đầu tiên xuất hiện trong danh sách theo mẫu code=....

//e. Gọi TypedJudgeService.SubmitTyped với counts là số dòng theo severity và values chỉ chứa mã lỗi đầu tiên.
