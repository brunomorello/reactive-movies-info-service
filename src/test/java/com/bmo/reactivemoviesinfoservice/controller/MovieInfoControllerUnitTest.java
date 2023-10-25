package com.bmo.reactivemoviesinfoservice.controller;

import com.bmo.reactivemoviesinfoservice.domain.MovieInfo;
import com.bmo.reactivemoviesinfoservice.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {

    private static final String MOVIES_INFO_URL = "/v1/moviesInfo";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService service;

    @Test
    void when_GET_withoutId_then_return_all_movies_info() {
        var moviesListFlux = List.of(
                MovieInfo.builder()
                        .name("Start Wars IV")
                        .year(1977)
                        .cast(List.of("Luke", "Obiwan"))
                        .releaseDate(LocalDate.parse("1977-01-01"))
                        .build(),
                MovieInfo.builder()
                        .name("Start Wars V")
                        .year(1980)
                        .cast(List.of("Luke", "Obiwan"))
                        .releaseDate(LocalDate.parse("1980-01-01"))
                        .build(),
                MovieInfo.builder()
                        .id("1SW")
                        .name("Start Wars VI")
                        .year(1983)
                        .cast(List.of("Luke", "Obiwan"))
                        .releaseDate(LocalDate.parse("1983-01-01"))
                        .build()
        );

        when(service.getAllMoviesInfo()).thenReturn(Flux.fromIterable(moviesListFlux));

        webTestClient.get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void when_GET_withId_then_return_correspondent_movie_info() {
        var movieInfoId = "1SW";
        var movie = MovieInfo.builder()
                .id("1SW")
                .name("Start Wars VI")
                .year(1983)
                .cast(List.of("Luke", "Obiwan"))
                .releaseDate(LocalDate.parse("1983-01-01"))
                .build();

        when(service.getMovieInfoById(movieInfoId)).thenReturn(Mono.just(movie));

        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("Start Wars VI");
    }

    @Test
    void when_POST_with_valid_payload_then_create_movie_info() {
        var movieInfoRequest = MovieInfo.builder()
                .name("Test Movie")
                .year(2023)
                .cast(List.of("Actor1", "Actress"))
                .releaseDate(LocalDate.now())
                .build();

        var movieInfoResponse = MovieInfo.builder()
                .id("testId")
                .name("Test Movie")
                .year(2023)
                .cast(List.of("Actor1", "Actress"))
                .releaseDate(LocalDate.now())
                .build();

        when(service.createMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(movieInfoResponse));

        webTestClient.post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfoRequest)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody.getId());
                    assertEquals("Test Movie", responseBody.getName());
                    assertEquals("testId", responseBody.getId());
                });
    }

    @Test
    void when_PUT_then_update_correspondent_movie_info() {
        var movieInfoId = "1SW";
        var movieInfoRequest = MovieInfo.builder()
                .name("Test1")
                .releaseDate(LocalDate.now())
                .cast(List.of("Test Actor"))
                .year(2021)
                .build();

        var movieInfoResponse = MovieInfo.builder()
                .name("Test1")
                .releaseDate(LocalDate.now())
                .cast(List.of("Test Actor"))
                .year(2021)
                .build();

        when(service.updateMovieInfo(isA(String.class), isA(MovieInfo.class))).thenReturn(Mono.just(movieInfoResponse));

        webTestClient.put()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(movieInfoRequest)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assertEquals("Test1", responseBody.getName());
                    assertEquals(LocalDate.now(), responseBody.getReleaseDate());
                    assertEquals(2021, responseBody.getYear());
                    assertEquals(List.of("Test Actor"), responseBody.getCast());
                });
    }

    @Test
    void when_DELETE_then_delete_correspondent_movie_info() {
        var movieInfoId = "1SW";

        when(service.deleteMovieInfo(anyString())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
