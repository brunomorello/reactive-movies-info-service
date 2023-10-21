package com.bmo.reactivemoviesinfoservice.repository;

import com.bmo.reactivemoviesinfoservice.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MovieRepository extends ReactiveMongoRepository<MovieInfo, String> {
}
