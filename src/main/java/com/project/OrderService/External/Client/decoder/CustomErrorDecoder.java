package com.project.OrderService.External.Client.decoder;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.OrderService.External.Client.response.ErrorResponse;
import com.project.OrderService.exception.CustomException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		ObjectMapper mapper = new ObjectMapper();

		log.info("::{}", response.request().url());
		log.info("::{}", response.request().headers());

		try {
			ErrorResponse errorResponse = mapper.readValue(response.body().asInputStream(), ErrorResponse.class);

			return new CustomException(errorResponse.getErrorMessage(), errorResponse.getErrorCode(),
					response.status());
		} catch (IOException e) {
			throw new CustomException("IntenalServerError", "INTERNAL_SERVER_ERROR", 500);
		}

	}

}
