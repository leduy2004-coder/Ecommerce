package com.ecommerce.search.exception;



import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@Slf4j
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_RANGE(6000, "Invalid request: price range is invalid", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY(6001, "Invalid request: categoryId is invalid", HttpStatus.BAD_REQUEST),
    SERVER_ERROR(6002, "Internal Server Error: Elasticsearch", HttpStatus.BAD_REQUEST),
    INVALID_SORT_BY(6003, "Invalid request: invalid sort by", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN), //403
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED), //401
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}