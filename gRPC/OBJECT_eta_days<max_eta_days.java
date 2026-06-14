package com.mycompany.grpca;

import GRPC.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TypedShipping {

    public static void main(String[] args) {

        String studentCode = "B22DCAT134";
        String qCode = "5NVAuABJ";

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

        ShippingQuoteData data =
                response.getShippingQuote();

        double weight = data.getWeightKg();
        int maxEta = data.getMaxEtaDays();

        CarrierQuote best = null;
        double bestFee = Double.MAX_VALUE;

        for (CarrierQuote q : data.getQuotesList()) {

            if (q.getEtaDays() > maxEta) {
                continue;
            }

            double fee =
                    q.getBaseFee()
                    + weight * q.getPerKgFee();

            fee =
                    Math.round(fee * 100.0)
                    / 100.0;

            if (best == null
                    || fee < bestFee
                    || (Math.abs(fee - bestFee) < 1e-9
                        && q.getReliability()
                           > best.getReliability())) {

                best = q;
                bestFee = fee;
            }
        }

        ShippingQuoteAnswer answer =
                ShippingQuoteAnswer.newBuilder()
                        .setCarrier(
                                best.getCarrier())
                        .setTotalFee(
                                bestFee)
                        .setEtaDays(
                                best.getEtaDays())
                        .build();

        TypedSubmitRequest submit =
                TypedSubmitRequest.newBuilder()
                        .setStudentCode(
                                studentCode)
                        .setQuestionAlias(
                                qCode)
                        .setRequestId(
                                response.getRequestId())
                        .setShippingQuoteAnswer(
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
//Một dịch vụ gRPC TypedJudgeService được triển khai trên server tại <Exam_IP>:2240 để xử lý các bài toán với đối tượng có cấu trúc qua Protocol Buffers.

//Yêu cầu: Viết chương trình tại máy trạm (gRPC client, plaintext, không TLS) để giao tiếp với TypedJudgeService và thực hiện các công việc sau.

//a. Tạo gRPC client plaintext tới cổng 2240 và gọi TypedJudgeService.RequestTyped với mã sinh viên và alias runtime được giao.

//b. Server trả về request_id và dữ liệu báo giá vận chuyển gồm order_id, weight_kg, max_eta_days, quotes.

//c. Chỉ xét quote có eta_days <= max_eta_days. Tính total_fee = base_fee + weight_kg * per_kg_fee, làm tròn 2 chữ số thập phân.

//d. Chọn quote có total_fee nhỏ nhất; nếu bằng nhau, chọn quote có reliability cao hơn.

//e. Gọi TypedJudgeService.SubmitTyped với carrier, total_fee, eta_days.
