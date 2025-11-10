//package com.otakumap;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//@SpringBootTest
//class OtakumapApplicationTests {
//
//	@Test
//	void test() throws InterruptedException {
//        ExecutorService executor = Executors.newFixedThreadPool(30);
//
//        for (int i = 0; i < 30; i++) {
//            executor.submit(() -> {
//                try {
//                    HttpClient client = HttpClient.newHttpClient();
//                    HttpRequest request = HttpRequest.newBuilder()
//                            .uri(URI.create("http://localhost:8080/api/events/popular"))
//                            .GET()
//                            .build();
//                    client.send(request, HttpResponse.BodyHandlers.ofString());
//                } catch (Exception e) {
//                    System.out.println("에러 발생: " + e.getMessage());
//                }
//            });
//        }
//
//        executor.shutdown();
//        executor.awaitTermination(1, TimeUnit.MINUTES);
//
//    }
//
//}
