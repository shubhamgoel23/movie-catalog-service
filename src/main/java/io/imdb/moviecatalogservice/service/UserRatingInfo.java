package io.imdb.moviecatalogservice.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import io.imdb.moviecatalogservice.models.Rating;
import io.imdb.moviecatalogservice.models.UserRating;

@Service
public class UserRatingInfo {

	@Lazy
	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "getFallbackUserRating", threadPoolKey = "userRatingInfoPool", threadPoolProperties = {
			@HystrixProperty(name = "coreSize", value = "10"),
			@HystrixProperty(name = "maxQueueSize", value = "5") }, commandProperties = {
					@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
					@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
					@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
					@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000") })
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
