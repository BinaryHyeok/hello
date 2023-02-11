package com.ssafy.backend.global.file.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.backend.global.error.exception.ApiErrorException;
import com.ssafy.backend.global.file.service.FileService;
import com.ssafy.backend.global.util.enums.ApiStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "FileController v1")
@RequestMapping("/v1/file")
public class FileControllerV1 {

	private final FileService fileService;

	@PostMapping("/upload/{category}/{id}")
	@ApiOperation(value = "사용자 파일 업로드")
	public ResponseEntity<?> uploadFile(
		@PathVariable("category") String category,
		@PathVariable("id") Long id,
		@RequestParam(name = "file", required = false) MultipartFile source,
		HttpServletRequest request) {

		log.info("uploadFile - " + category);

		if ("user".equals(category)) {
			return ResponseEntity
				.ok(fileService.uploadUserFile(id, source, request));
		} else if ("animal".equals(category)) {
			return ResponseEntity
				.ok(fileService.uploadAnimalFile(id, source, request));
		} else {
			throw new ApiErrorException(ApiStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/uploadMultiple/{category}/{id}")
	@ApiOperation(value = "파일 업로드")
	public ResponseEntity<?> uploadMultipleFiles(
		@PathVariable("category") String category,
		@PathVariable("id") Long id,
		@RequestParam("files") MultipartFile[] files,
		HttpServletRequest request) {

		List<?> fileDownloadUriList = Arrays.asList(files)
			.stream()
			.map(file -> uploadFile(category, id, file, request))
			.collect(Collectors.toList());

		return ResponseEntity
			.ok(fileDownloadUriList);
	}

	@GetMapping("/download/{category}/{file:.+}")
	@ApiOperation(value = "파일 다운로드")
	public ResponseEntity<?> downloadFile(
		@PathVariable("category") String category,
		@PathVariable("file") String file,
		HttpServletRequest request) {

		return fileService.loadFile(category, file, request);
	}

	@DeleteMapping("/download/{category}/{file:.+}")
	@ApiOperation(value = "파일 삭제")
	public ResponseEntity<?> deleteFile(
		@PathVariable("category") String category,
		@PathVariable("file") String file,
		HttpServletRequest request) {

		log.info("deleteFile - " + category);

		return ResponseEntity
			.ok(fileService.updateExpire(category, file, true, request));
	}

}
