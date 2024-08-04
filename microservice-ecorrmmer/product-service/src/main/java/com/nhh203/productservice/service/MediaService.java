package com.nhh203.productservice.service;

import com.nhh203.productservice.viewmodel.NoFileMediaVm;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface MediaService {
	NoFileMediaVm saveFile(MultipartFile multipartFile, String caption, String fileNameOverride);

	NoFileMediaVm getMedia(Long id);

	void removeMedia(Long id);
}
