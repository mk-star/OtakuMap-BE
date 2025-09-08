package com.otakumap;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelApiTest {

    public static void main(String[] args) throws Exception {
        int poolSize = 20;             // HikariCP maximumPoolSize와 맞추거나 조금 더 크게
        int concurrentRequests = 30;   // 풀보다 많은 요청으로 에러 재현

        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);

        for (int i = 0; i < concurrentRequests; i++) {
            executor.submit(() -> {
                try {
                    HttpClient client = HttpClient.newHttpClient();

                    // 인기 이벤트 API 호출
                    HttpRequest popularEventsRequest = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/events/popular"))
                            .GET()
                            .build();

                    // Top7 리뷰 API 호출
                    HttpRequest top7ReviewsRequest = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/reviews/top7"))
                            .GET()
                            .build();

                    // 이벤트 배너 API 호출
                    HttpRequest bannerRequest = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/events/banner"))
                            .GET()
                            .build();

                    // 순차 호출 (각 요청이 DB 커넥션 점유)
                    client.send(popularEventsRequest, HttpResponse.BodyHandlers.ofString());
                    client.send(top7ReviewsRequest, HttpResponse.BodyHandlers.ofString());
                    client.send(bannerRequest, HttpResponse.BodyHandlers.ofString());

                } catch (Exception e) {
                    System.out.println("에러 발생: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);

        System.out.println("테스트 완료!");
    }
}
