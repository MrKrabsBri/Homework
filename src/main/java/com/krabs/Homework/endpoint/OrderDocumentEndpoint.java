package com.krabs.Homework.endpoint;

import com.customercontract.*;
import com.krabs.Homework.exception.DuplicateOrderDocumentException;
import com.krabs.Homework.service.OrderDocumentService;
import com.krabs.Homework.transformator.OrderDocumentTransformationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class OrderDocumentEndpoint {
    private final Logger LOGGER = LoggerFactory.getLogger(OrderDocumentEndpoint.class);
    private final OrderDocumentService orderDocumentService;
    private final OrderDocumentTransformationService orderDocumentTransformationService;

    @PayloadRoot(namespace = "http://customercontract.com", localPart = "CreateOrderDocumentRequest")
    @ResponsePayload
    public CreateOrderDocumentResponse createOrderDocument(@RequestPayload CreateOrderDocumentRequest request){
        /*if (true)
        throw new DuplicateOrderDocumentException("a","b","c"); // shows nothing*/

        orderDocumentTransformationService.transform(request);
        CreateOrderDocumentResponse response = orderDocumentService.createOrderDocumentSoapResponse();
        LOGGER.info("Creating order document.");
        orderDocumentService.createOrderDocument(request);
        LOGGER.info("Order document created.");

        return response;
    }

    @PayloadRoot(namespace = "http://customercontract.com", localPart = "GetOrderDocumentListRequest")
    @ResponsePayload
    public GetOrderDocumentListResponse getOrderDocumentList(@RequestPayload GetOrderDocumentListRequest request){
        LOGGER.info("Retrieving Order document list.");

        return orderDocumentService.getOrderDocumentListSoapResponse();
    }

    @PayloadRoot(namespace = "http://customercontract.com", localPart = "GetOrderDocumentByIdRequest")
    @ResponsePayload
    public GetOrderDocumentByIdResponse getOrderDocumentById(@RequestPayload GetOrderDocumentByIdRequest request){
        LOGGER.info("Retrieving Order with ID {}", request.getServiceId());

        return orderDocumentService.getOrderDocumentByIdSoapResponse(request);
    }

    @PayloadRoot(namespace = "http://customercontract.com", localPart = "UpdateOrderDocumentByIdRequest")
    @ResponsePayload
    public UpdateOrderDocumentByIdResponse updateOrderDocumentById(@RequestPayload UpdateOrderDocumentByIdRequest request){
        LOGGER.info("Updating Order with ID {}", request.getOrderDocument().getServiceId());

        return orderDocumentService.updateOrderDocumentById(request);
    }
    //updateOrderDocumentById

    @PayloadRoot(namespace = "http://customercontract.com", localPart = "DeleteOrderDocumentByIdRequest")
    @ResponsePayload
    public DeleteOrderDocumentByIdResponse deleteOrderDocumentById(@RequestPayload DeleteOrderDocumentByIdRequest request){
        LOGGER.info("Deleting Order with ID {}", request);

        return orderDocumentService.deleteOrderDocumentById(request);
    }
    //updateOrderDocumentById


}
