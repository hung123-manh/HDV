package com.mycompany.grpca;

import GRPC.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.*;

public class TypedTest {

    public static void main(String[] args) {

        String studentCode = "B22DCAT134";
        String qCode = "oMqX9Owe";

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

        EnrollmentData data =
                response.getEnrollment();

        Set<String> completed =
                new HashSet<>(
                        data.getCompletedCoursesList());

        List<String> missing =
                new ArrayList<>();

        for (String course :
                data.getRequiredCoursesList()) {

            if (!completed.contains(course)) {
                missing.add(course);
            }
        }

        Collections.sort(missing);

        double gpaGap =
                Math.max(
                        0.0,
                        data.getMinGpa()
                        - data.getGpa());

        gpaGap =
                Math.round(gpaGap * 100.0)
                / 100.0;

        boolean eligible =
                missing.isEmpty()
                && gpaGap == 0.0;

        EnrollmentAnswer answer =
                EnrollmentAnswer.newBuilder()
                        .setEligible(eligible)
                        .addAllMissingCourses(missing)
                        .setGpaGap(gpaGap)
                        .build();

        TypedSubmitRequest submit =
                TypedSubmitRequest.newBuilder()
                        .setStudentCode(studentCode)
                        .setQuestionAlias(qCode)
                        .setRequestId(
                                response.getRequestId())
                        .setEnrollmentAnswer(answer)
                        .build();

        TypedSubmitResponse result =
                stub.submitTyped(submit);

        System.out.println("STATUS = "
                + result.getStatus());

        System.out.println("MESSAGE = "
                + result.getMessage());

        channel.shutdown();
    }
}
//Một dịch vụ gRPC TypedJudgeService được triển khai trên server tại <Exam_IP>:2240 để xử lý các bài toán với đối tượng có cấu trúc qua Protocol Buffers.

//Yêu cầu: Viết chương trình tại máy trạm (gRPC client, plaintext, không TLS) để giao tiếp với TypedJudgeService và thực hiện các công việc sau.

//a. Tạo gRPC client plaintext tới cổng 2240 và gọi TypedJudgeService.RequestTyped với mã sinh viên và alias runtime được giao.

//b. Server trả về request_id và dữ liệu xét điều kiện đăng ký gồm completed_courses, required_courses, gpa, min_gpa.

//c. Tính danh sách missing_courses là các môn bắt buộc chưa hoàn thành, sắp xếp tăng dần theo mã môn.

//d. Tính gpa_gap = max(0, min_gpa - gpa), làm tròn 2 chữ số thập phân. eligible đúng khi không thiếu môn và gpa_gap bằng 0.

//e. Gọi TypedJudgeService.SubmitTyped với eligible, missing_courses, gpa_gap.
