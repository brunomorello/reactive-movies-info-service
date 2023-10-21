package com.bmo.reactivemoviesinfoservice.controller;

import com.bmo.reactivemoviesinfoservice.domain.MovieInfo;
import com.bmo.reactivemoviesinfoservice.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/moviesInfo")
public class MovieInfoController {

    private MovieInfoService service;

    public MovieInfoController(MovieInfoService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<MovieInfo> getAll() {
        return service.getAllMoviesInfo();
    }

    @GetMapping("/{id}")
    public Mono<MovieInfo> getById(@PathVariable String id) {
        return service.getMovieInfoById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> create(@RequestBody MovieInfo movieInfo) {
        return service.createMovieInfo(movieInfo);
    }

    @PutMapping("/{id}")
    public Mono<MovieInfo> update(@PathVariable String id, @RequestBody MovieInfo movieInfo) {
        return service.updateMovieInfo(id, movieInfo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return service.deleteMovieInfo(id);
    }
}
