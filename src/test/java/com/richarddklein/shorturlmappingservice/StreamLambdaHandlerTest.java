package com.richarddklein.shorturlmappingservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;

import static org.junit.jupiter.api.Assertions.*;

public class StreamLambdaHandlerTest {

    private static StreamLambdaHandler handler;
    private static Context lambdaContext;

    @BeforeAll
    public static void setUp() {
        handler = new StreamLambdaHandler();
        lambdaContext = new MockLambdaContext();
    }

//    @Test
//    public void ping_streamRequest_respondsWithHello() {
//        InputStream requestStream = new AwsProxyRequestBuilder(
//                "/shorturl/reservations/reserve/any", HttpMethod.GET)
//                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
//                .buildStream();
//        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
//
//        handle(requestStream, responseStream);
//
//        AwsProxyResponse dto = readResponse(responseStream);
//        assertNotNull(dto);
//        assertEquals(Response.Status.OK.getStatusCode(), dto.getStatusCode());
//
//        assertFalse(dto.isBase64Encoded());
//
//        assertTrue(dto.getBody().contains("status"));
//        assertTrue(dto.getBody().contains("shortUrlReservation"));
//
//        assertTrue(dto.getMultiValueHeaders().containsKey(HttpHeaders.CONTENT_TYPE));
//        assertTrue(dto.getMultiValueHeaders().getFirst(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.APPLICATION_JSON));
//    }

    @Test
    public void invalidResource_streamRequest() {
        InputStream requestStream = new AwsProxyRequestBuilder("/shorturl/not-mapped", HttpMethod.GET)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .buildStream();
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();

        handle(requestStream, responseStream);

        AwsProxyResponse response = readResponse(responseStream);
        assertNotNull(response);
        assertNotEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
    }

    private void handle(InputStream is, ByteArrayOutputStream os) {
        try {
            handler.handleRequest(is, os, lambdaContext);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private AwsProxyResponse readResponse(ByteArrayOutputStream responseStream) {
        try {
            return LambdaContainerHandler
                    .getObjectMapper()
                    .readValue(responseStream.toByteArray(), AwsProxyResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Error while parsing dto: " + e.getMessage());
        }
        return null;
    }
}
