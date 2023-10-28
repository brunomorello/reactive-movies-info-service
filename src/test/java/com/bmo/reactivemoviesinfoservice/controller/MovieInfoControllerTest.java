package com.bmo.reactivemoviesinfoservice.controller;

import com.bmo.reactivemoviesinfoservice.domain.MovieInfo;
import com.bmo.reactivemoviesinfoservice.repository.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MovieInfoControllerTest {

    private static final String MOVIES_INFO_URL = "/v1/moviesInfo";

    @Autowired
    private MovieRepository repository;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        List<MovieInfo> movieInfoList = List.of(
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
        repository.saveAll(movieInfoList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    void when_POST_with_valid_payload_then_create_movie_info() {
        MovieInfo movieInfo = MovieInfo.builder()
                .name("Test Movie")
                .year(2023)
                .cast(List.of("Actor1", "Actress"))
                .releaseDate(LocalDate.now())
                .build();

        webTestClient.post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(responseBody.getId());
                    assertEquals("Test Movie", responseBody.getName());
                });
    }

    @Test
    void when_GET_withoutId_then_return_all_movies_info() {
        webTestClient.get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void when_GET_with_query_params_to_find_by_year_then_return_related_movies_info() {
        var uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
                        .queryParam("year", 1977)
                        .buildAndExpand().toUri();

        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }

    @Test
    void when_GET_withId_then_return_correspondent_movie_info() {
        var movieInfoId = "1SW";
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
    void when_GET_with_invalid_id_then_return_not_found() {
        var movieInfoId = "12SW";
        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void when_PUT_then_update_correspondent_movie_info() {
        var movieInfoId = "1SW";
        MovieInfo movieInfo = MovieInfo.builder()
                .name("Test")
                .releaseDate(LocalDate.now())
                .cast(List.of("Test Actor"))
                .year(2023)
                .build();

        webTestClient.put()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assertEquals("Test", responseBody.getName());
                    assertEquals(LocalDate.now(), responseBody.getReleaseDate());
                    assertEquals(2023, responseBody.getYear());
                    assertEquals(List.of("Test Actor"), responseBody.getCast());
                });
    }

    @Test
    void when_PUT_with_not_existent_id_then_return_not_found() {
        var movieInfoId = "12SW";
        MovieInfo movieInfo = MovieInfo.builder()
                .name("Test")
                .releaseDate(LocalDate.now())
                .cast(List.of("Test Actor"))
                .year(2023)
                .build();

        webTestClient.put()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void when_DELETE_then_delete_correspondent_movie_info() {
        var movieInfoId = "1SW";

        webTestClient.delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void when_DELETE_with_not_existent_id_then_return_not_found() {
        var movieInfoId = "12SW";

        webTestClient.delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}