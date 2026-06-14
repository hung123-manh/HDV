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
