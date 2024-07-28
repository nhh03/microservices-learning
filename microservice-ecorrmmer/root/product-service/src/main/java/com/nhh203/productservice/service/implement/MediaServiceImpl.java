package com.nhh203.productservice.service.implement;

import com.nhh203.productservice.service.MediaService;
import com.nhh203.productservice.viewmodel.NoFileMediaVm;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaServiceImpl implements MediaService {
	@Override
	public NoFileMediaVm saveFile(MultipartFile multipartFile, String caption, String fileNameOverride) {
		return null;
	}

	@Override
	public NoFileMediaVm getMedia(Long id) {
		return null;
	}

	@Override
	public void removeMedia(Long id) {

	}
}
