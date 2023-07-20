package it.babyloncloud.chat.filter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class MessageFilter {

	private final RestTemplate restTemplate = new RestTemplate();
	private final String apiUrl = "http://localhost:5000/classify"; // L'URL dell'API REST Python

	public float classifyMessage(String message) {
		// Crea l'oggetto di richiesta con il corpo del messaggio
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(String.format("{\"message\":\"%s\"}", message), headers);

		// Invia la richiesta POST all'API REST Python
		ResponseEntity<FilterResponse> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, FilterResponse.class);

		// Ottieni il risultato dalla risposta JSON
		FilterResponse filterResponse = response.getBody();

		if (filterResponse != null) {
			return filterResponse.getProbability();
		}

		return 0.0f;
	}

	private static class FilterResponse {
		private float probability;

		public float getProbability() {
			return probability;
		}

		public void setProbability(float probability) {
			this.probability = probability;
		}
	}

}
