package com.mycompany.grpca;

import GRPC.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypedTelemetry {

    public static void main(String[] args) {

        String studentCode = "B22DCAT134";
        String qCode = "Oe1iOcCA";

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

        SensorTelemetryData data =
                response.getSensorTelemetry();

        double threshold =
                data.getThreshold();

        List<Double> values =
                new ArrayList<>();

        double sum = 0.0;
        int anomalyCount = 0;

        for (SensorReading r :
                data.getReadingsList()) {

            double v = r.getValue();

            values.add(v);
            sum += v;

            if (v > threshold) {
                anomalyCount++;
            }
        }

        int n = values.size();

        double average =
                sum / n;

        Collections.sort(values);

        int index =
                (int) Math.ceil(n * 0.95) - 1;

        double p95 =
                values.get(index);

        average =
                Math.round(
                        average * 100.0)
                / 100.0;

        p95 =
                Math.round(
                        p95 * 100.0)
                / 100.0;

        SensorTelemetryAnswer answer =
                SensorTelemetryAnswer
                        .newBuilder()
                        .setAverage(average)
                        .setP95(p95)
                        .setAnomalyCount(
                                anomalyCount)
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
                        .setSensorTelemetryAnswer(
                                answer)
                        .build();

        TypedSubmitResponse result =
                stub.submitTyped(submit);

        System.out.println(
                "AVG = " + average);

        System.out.println(
                "P95 = " + p95);

        System.out.println(
                "ANOMALIES = "
                + anomalyCount);

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

//b. Server trả về request_id và dữ liệu telemetry gồm threshold và danh sách readings, mỗi reading có sensor_id, timestamp, value.

//c. Tính average của tất cả value, p95 là phần tử tại vị trí ceil(n * 0.95) - 1 sau khi sắp xếp tăng dần, và anomaly_count là số reading có value > threshold.

//d. Gọi TypedJudgeService.SubmitTyped với average, p95, anomaly_count. Các giá trị số thực làm tròn 2 chữ số thập phân.

//e. Tính toán trên toàn bộ danh sách server trả về, không bỏ phần tử đầu hoặc cuối.
