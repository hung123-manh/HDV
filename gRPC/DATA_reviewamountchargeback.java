package com.mycompany.grpca;

import GRPC.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;

public class TypedTransactionRisk {

    public static void main(String[] args) {

        String studentCode = "B22DCAT134";
        String qCode = "3r4qdqdA";

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

        TransactionRiskBatchData data =
                response.getTransactionRiskBatch();

        List<String> highRiskIds =
                new ArrayList<>();

        double totalHighRiskAmount = 0.0;

        for (TransactionRecord t :
                data.getTransactionsList()) {

            boolean highRisk =
                    t.getAmount() >= 5000
                    || t.getChargebackCount() >= 2
                    || (t.getNewDevice()
                        && !t.getCountry().equals("VN"));

            if (highRisk) {

                highRiskIds.add(
                        t.getTransactionId());

                totalHighRiskAmount +=
                        t.getAmount();
            }
        }

        totalHighRiskAmount =
                Math.round(
                        totalHighRiskAmount * 100.0)
                / 100.0;

        int reviewCount =
                highRiskIds.size();

        TransactionRiskAnswer answer =
                TransactionRiskAnswer
                        .newBuilder()
                        .addAllHighRiskTransactionIds(
                                highRiskIds)
                        .setReviewCount(
                                reviewCount)
                        .setTotalHighRiskAmount(
                                totalHighRiskAmount)
                        .build();

        TypedSubmitRequest submit =
                TypedSubmitRequest
                        .newBuilder()
                        .setStudentCode(
                                studentCode)
                        .setQuestionAlias(
                                qCode)
                        .setRequestId(
                                response.getRequestId())
                        .setTransactionRiskAnswer(
                                answer)
                        .build();

        TypedSubmitResponse result =
                stub.submitTyped(submit);

        System.out.println(
                "REVIEW COUNT = "
                + reviewCount);

        System.out.println(
                "TOTAL HIGH RISK AMOUNT = "
                + totalHighRiskAmount);

        System.out.println(
                "STATUS = "
                + result.getStatus());

        System.out.println(
                "MESSAGE = "
                + result.getMessage());

        channel.shutdown();
    }
}
//Một dịch vụ gRPC TypedJudgeService được triển khai trên server tại <Exam_IP>:2240 để xử lý các bài toán với dữ liệu có cấu trúc qua Protocol Buffers.

//Yêu cầu: Viết chương trình tại máy trạm (gRPC client, plaintext, không TLS) để giao tiếp với TypedJudgeService và thực hiện các công việc sau.

//a. Tạo gRPC client plaintext tới cổng 2240 và gọi TypedJudgeService.RequestTyped với mã sinh viên và alias runtime được giao.

//b. Server trả về request_id và batch giao dịch, mỗi giao dịch có transaction_id, amount, currency, country, chargeback_count, new_device.

//c. Một giao dịch cần review nếu amount >= 5000, hoặc chargeback_count >= 2, hoặc new_device=true và country khác VN.

//d. Gọi TypedJudgeService.SubmitTyped với answer gồm danh sách high_risk_transaction_ids theo thứ tự ban đầu, review_count -- số lượng giao dịch cần review (bằng độ dài high_risk_transaction_ids, và total_high_risk_amount làm tròn 2 chữ số thập phân.

//e. Không sắp xếp lại danh sách giao dịch rủi ro cao.
