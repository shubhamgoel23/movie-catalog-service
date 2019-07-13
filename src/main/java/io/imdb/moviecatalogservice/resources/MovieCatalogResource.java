package io.imdb.moviecatalogservice.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.imdb.moviecatalogservice.models.CatalogItem;
import io.imdb.moviecatalogservice.models.UserRating;
import io.imdb.moviecatalogservice.service.MovieInfo;
import io.imdb.moviecatalogservice.service.UserRatingInfo;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Autowired
	private MovieInfo movieInfo;

	@Autowired
	private UserRatingInfo userRatingInfo;

	private DiscoveryClient discoveryClient;

	@RequestMapping("/{userId}")
//	@HystrixCommand(fallbackMethod = "getFallbackCatalog")
	public List<CatalogItem> getCatalog(
			@PathVariable("userId") String userId) {

//		WebClient.Builder builder = WebClient.builder();

//		RestTemplate restTemplate = new RestTemplate();
		// get all rated movie IDs
//		List<Rating> ratings = Arrays.asList(new Rating("123", 4), new Rating("223", 5));

//		ParameterizedTypeReference<List<Rating>> ratingList = new ParameterizedTypeReference<List<Rating>>() {};

//		UserRating userRating = restTemplate.getForObject("http://localhost:8083/ratingsData/users/" + userId,
//				UserRating.class);

		// eureka way
		UserRating userRating = userRatingInfo.getUserRating(userId);

		// for each movie ID, call movie info service and get details
		return userRating.getUserRating().stream().map(rating -> {
//			Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);

			// eureka way
//			Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

//			Movie movie = webClientBuilder.build().get().uri("http://localhost:8082/movies/" + rating.getMovieId())
//					.retrieve().bodyToMono(Movie.class).block();

			// eureka way
			return movieInfo.getCatalogItem(rating);

		}).collect(Collectors.toList());

		// put them all together

//		return Collections.singletonList(new CatalogItem("Transformers", "test", 4));
	}

}
