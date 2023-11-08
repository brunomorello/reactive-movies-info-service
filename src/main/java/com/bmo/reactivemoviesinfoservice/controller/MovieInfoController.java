package com.bmo.reactivemoviesinfoservice.controller;

import com.bmo.reactivemoviesinfoservice.domain.MovieInfo;
import com.bmo.reactivemoviesinfoservice.service.MovieInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/v1/moviesInfo")
public class MovieInfoController {

    private MovieInfoService service;

    private Sinks.Many<MovieInfo> moviesInfoSink = Sinks.many().replay().all();

    public MovieInfoController(MovieInfoService service) {
        this.service = service;
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> getMovieInfoStream() {
        return moviesInfoSink.asFlux();
    }

    @GetMapping
    public Flux<MovieInfo> getAll(@RequestParam(value = "year", required = false) Integer year) {
        if (year != null) {
            return service.getMoviesByYear(year);
        }
        return service.getAllMoviesInfo();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MovieInfo>> getById(@PathVariable String id) {
        return service.getMovieInfoById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> create(@RequestBody @Valid MovieInfo movieInfo) {
        return service.createMovieInfo(movieInfo)
                .doOnNext(moviesInfoSink::tryEmitNext);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<MovieInfo>> update(@PathVariable String id, @RequestBody MovieInfo movieInfo) {
        return service.updateMovieInfo(id, movieInfo)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return service.deleteMovieInfo(id);
    }
}
