package io.imdb.moviecatalogservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import io.imdb.moviecatalogservice.models.CatalogItem;
import io.imdb.moviecatalogservice.models.Movie;
import io.imdb.moviecatalogservice.models.Rating;

@Service
public class MovieInfo {

	@Lazy
	@Autowired
	private WebClient.Builder webClientBuilder;

	@HystrixCommand(fallbackMethod = "getFallbackCatalogItem", threadPoolKey = "movieInfoPool", threadPoolProperties = {
			@HystrixProperty(name = "coreSize", value = "30"),
			@HystrixProperty(name = "maxQueueSize", value = "10") }, commandProperties = {
					@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
					@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
					@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
					@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000") })
	public CatalogItem getCatalogItem(Rating rating) {
		Movie movie = webClientBuilder.build().get()
				.uri("http://movie-info-service/movies/"
						+ rating.getMovieId())
				.retrieve().bodyToMono(Movie.class).block();
		return new CatalogItem(movie.getMovieId(), movie.getName(),
				rating.getRating());
	}

	public CatalogItem getFallbackCatalogItem(Rating rating) {
		return new CatalogItem("Movie name not found", "",
				rating.getRating());
	}
}
