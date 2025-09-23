package com.marketer.affiliate_agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketer.affiliate_agent.controller.AffiliateController;
import com.marketer.affiliate_agent.dto.ContentType;
import com.marketer.affiliate_agent.dto.CreateLinkRequest;
import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.service.AffiliateLinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AffiliateController.class)
class AffiliateAgentApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AffiliateLinkService affiliateLinkService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void whenCreateLink_thenReturns200() throws Exception {
		CreateLinkRequest request = new CreateLinkRequest();
		request.setLongUrl("https://example.com");
		request.setContentType(ContentType.TWEET);
		request.setScheduledAt(LocalDateTime.now().plusHours(1)); // Schedule for 1 hour from now

		when(affiliateLinkService.createLink(any(), any(ContentType.class), any(LocalDateTime.class))).thenReturn(new AffiliateLink());

		mockMvc.perform(post("/api/v1/links")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}
}
