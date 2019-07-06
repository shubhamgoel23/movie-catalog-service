package io.imdb.moviecatalogservice.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.imdb.moviecatalogservice.models.CatalogItem;
import io.imdb.moviecatalogservice.models.Movie;
import io.imdb.moviecatalogservice.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Lazy
	@Autowired
	private RestTemplate restTemplate;

	@Lazy
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	private DiscoveryClient discoveryClient;

	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

//		WebClient.Builder builder = WebClient.builder();

//		RestTemplate restTemplate = new RestTemplate();
		// get all rated movie IDs
//		List<Rating> ratings = Arrays.asList(new Rating("123", 4), new Rating("223", 5));

//		ParameterizedTypeReference<List<Rating>> ratingList = new ParameterizedTypeReference<List<Rating>>() {};
		
//		UserRating userRating = restTemplate.getForObject("http://localhost:8083/ratingsData/users/" + userId,
//				UserRating.class);
		
		//eureka way
		UserRating userRating = restTemplate.getForObject("http://rating-data-service/ratingsData/users/" + userId,
				UserRating.class);
		// for each movie ID, call movie info service and get details
		return userRating.getUserRating().stream().map(rating -> {
//			Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
			
			//eureka way
//			Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
			
			
//			Movie movie = webClientBuilder.build().get().uri("http://localhost:8082/movies/" + rating.getMovieId())
//					.retrieve().bodyToMono(Movie.class).block();
			
			//eureka way
			Movie movie = webClientBuilder.build().get().uri("http://movie-info-service/movies/" + rating.getMovieId())
					.retrieve().bodyToMono(Movie.class).block();
			return new CatalogItem(movie.getName(), "", rating.getRating());
		}).collect(Collectors.toList());

		// put them all together

//		return Collections.singletonList(new CatalogItem("Transformers", "test", 4));
	}

}
