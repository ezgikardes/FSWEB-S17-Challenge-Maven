package com.workintech.spring17challenge.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    //ApiException türündeki exception'ları yakalayacak handler:
    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> hadleException(ApiException apiException){
        // metodun dönüş değeri ResponseEntity olacak.
        // ResponseEntity Http yanıt kodlarına (200, 201, 400 vb.) bir body ekleyerek response dönmeni sağlar
        // Biz de body olarak custom olarak oluşturduğumuz ApiErrorResponse'ı döneceğiz.

        ApiErrorResponse apiErrorResponse; //dönüş için bir ApiErrorResponse objesi declare ettim
        // custom olarak oluşturduğumuz bu obje'nin constructor'ında üç field vardı.
        //status, message, timestamp
        apiErrorResponse = new ApiErrorResponse( //ResponseEntity objesini initiate ettim
                apiException.getHttpStatus().value(),
                apiException.getMessage(),
                System.currentTimeMillis());

        //şimdi bu oluştruduğumuz objeyi, ResponseEntity içine ver, Http request tipini de eklemeyi unutma!
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    //Exception türündeki exception'ları yakalayacak handler:
    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleException(Exception exception){
        ApiErrorResponse apiErrorResponse;
        apiErrorResponse = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);

    }

}
