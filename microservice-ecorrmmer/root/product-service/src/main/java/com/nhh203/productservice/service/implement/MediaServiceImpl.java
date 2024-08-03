package com.nhh203.productservice.service.implement;

import com.nhh203.productservice.config.ServiceUrlConfig;
import com.nhh203.productservice.service.MediaService;
import com.nhh203.productservice.viewmodel.NoFileMediaVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
	private final RestClient restClient;
	private final ServiceUrlConfig serviceUrlConfig;

	@Override
	public NoFileMediaVm saveFile(MultipartFile multipartFile, String caption, String fileNameOverride) {
		final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.media()).path("/medias").build().toUri();
		final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();

		final MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("multipartFile", multipartFile.getResource());
		builder.part("caption", caption);
		builder.part("fileNameOverride", fileNameOverride);

		return restClient.post()
				.uri(url)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.headers(h -> h.setBearerAuth(jwt))
				.body(builder.build())
				.retrieve()
				.body(NoFileMediaVm.class);
	}

	@Override
	public NoFileMediaVm getMedia(Long id) {
		if (id == null) {
			//TODO return default no image url
			return new NoFileMediaVm(null, "", "", "", "");
		}
		final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.media()).path("/medias/{id}").buildAndExpand(id).toUri();
		return restClient.get()
				.uri(url)
				.retrieve()
				.body(NoFileMediaVm.class);
	}

	@Override
	public void removeMedia(Long id) {
		final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.media()).path("/medias/{id}").buildAndExpand(id).toUri();
		final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
		restClient.delete()
				.uri(url)
				.headers(h -> h.setBearerAuth(jwt))
				.retrieve()
				.body(Void.class);
	}
}
