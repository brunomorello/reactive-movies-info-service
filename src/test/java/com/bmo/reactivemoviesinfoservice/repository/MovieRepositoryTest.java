package com.bmo.reactivemoviesinfoservice.repository;

import com.bmo.reactivemoviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ActiveProfiles("test")
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

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
        movieRepository.saveAll(movieInfoList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieRepository.deleteAll().block();
    }

    @Test
    void when_findAll_then_return_list_of_all_movies() {
        Flux<MovieInfo> movieInfoFlux = movieRepository.findAll().log();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void when_findById_then_return_correspondent_movie() {
        Mono<MovieInfo> movieInfoMono = movieRepository.findById("1SW").log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals("Start Wars VI", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void when_save_then_persists_moveInfo() {
        MovieInfo movieInfo = MovieInfo.builder()
                .name("Test")
                .year(2023)
                .cast(List.of("1", "2"))
                .releaseDate(LocalDate.now())
                .build();

        Mono<MovieInfo> movieInfoMono = movieRepository.save(movieInfo).log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfoResp -> {
                    assertNotNull(movieInfoResp.getId());
                    assertEquals("Test", movieInfoResp.getName());
                })
                .verifyComplete();
    }

    @Test
    void when_update_then_persists_moveInfo() {
        MovieInfo movieInfoFound = movieRepository.findById("1SW").log().block();
        movieInfoFound.setYear(2023);

        Mono<MovieInfo> movieInfoUpdateMono = movieRepository.save(movieInfoFound).log();

        StepVerifier.create(movieInfoUpdateMono)
                .assertNext(movieInfo -> assertEquals(2023, movieInfo.getYear()))
                .verifyComplete();
    }

    @Test
    void when_delete_then_do_it() {
        movieRepository.deleteById("1SW").block();
        Flux<MovieInfo> moviesInfoFlux = movieRepository.findAll();

        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void when_findByYear_then_return_accordingly() {
        Flux<MovieInfo> movieInfoFlux = movieRepository.findByYear(1977).log();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void when_findByName_then_return_accordingly() {
        Flux<MovieInfo> movieInfoFlux = movieRepository.findByName("Start Wars V");

        StepVerifier.create(movieInfoFlux)
                .assertNext(movieInfo -> assertEquals("Start Wars V", movieInfo.getName()))
                .verifyComplete();
    }

}