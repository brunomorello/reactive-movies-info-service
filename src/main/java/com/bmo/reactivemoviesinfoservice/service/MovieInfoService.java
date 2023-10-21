package com.bmo.reactivemoviesinfoservice.service;

import com.bmo.reactivemoviesinfoservice.domain.MovieInfo;
import com.bmo.reactivemoviesinfoservice.repository.MovieRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
public class MovieInfoService {

    private MovieRepository repository;

    public MovieInfoService(MovieRepository repository) {
        this.repository = repository;
    }

    public Mono<MovieInfo> createMovieInfo(MovieInfo movieInfo) {
        return repository.save(movieInfo).log();
    }

    public Flux<MovieInfo> getAllMoviesInfo() {
        return repository.findAll().log();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return repository.findById(id).log();
    }

    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo updatedMovieInfo) {
        return repository.findById(id)
                .flatMap(updateMovieInfoMonoFunction(updatedMovieInfo));
    }

    private Function<MovieInfo, Mono<MovieInfo>> updateMovieInfoMonoFunction(MovieInfo updatedMovieInfo) {
        return movieInfoResp -> {
            movieInfoResp.setName(updatedMovieInfo.getName());
            movieInfoResp.setYear(updatedMovieInfo.getYear());
            movieInfoResp.setCast(updatedMovieInfo.getCast());
            movieInfoResp.setReleaseDate(updatedMovieInfo.getReleaseDate());
            return repository.save(movieInfoResp);
        };
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return repository.deleteById(id);
    }
}
