package io.imdb.moviecatalogservice.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import io.imdb.moviecatalogservice.models.Rating;
import io.imdb.moviecatalogservice.models.UserRating;

@Service
public class UserRatingInfo {

	@Lazy
	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "getFallbackUserRating")
	public UserRating getUserRating(String userId) {
		return restTemplate.getForObject(
				"http://rating-data-service/ratingsData/users/"
						+ userId,
				UserRating.class);
	}

	public UserRating getFallbackUserRating(String userId) {
		UserRating userRating = new UserRating();
		userRating.setUserRating(Arrays.asList(new Rating("0", 0)));

		return userRating;
	}
}
