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
