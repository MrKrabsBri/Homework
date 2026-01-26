package com.krabs.Homework.service;

import com.customercontract.*;

import com.krabs.Homework.entity.OrderDocumentEntity;
import com.krabs.Homework.exception.DuplicateServiceIdException;
import com.krabs.Homework.exception.OrderDocumentNotFoundException;
import com.krabs.Homework.mapper.OrderDocumentMapper;
import com.krabs.Homework.mapper.OrderDocumentUpdateMapper;
import com.krabs.Homework.repository.CustomerContractRepository;
import com.krabs.Homework.transformator.OrderDocumentTransformationService;
import com.krabs.Homework.validator.OrderDocumentValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDocumentService {

    private final CustomerContractRepository repository;
    private final OrderDocumentMapper orderDocumentMapper;
    private final OrderDocumentUpdateMapper orderDocumentUpdateMapper;
    private final OrderDocumentTransformationService orderDocumentTransformationService;
    private final OrderDocumentValidator orderDocumentValidator;

    private final Logger LOGGER = LoggerFactory.getLogger(OrderDocumentService.class);

    public CreateOrderDocumentResponse createOrderDocument(CreateOrderDocumentRequest request) {
        orderDocumentTransformationService.transform(request.getOrderDocument());
        String missingFieldError = orderDocumentValidator.validateMandatoryFields(request.getOrderDocument());

        CreateOrderDocumentResponse createOrderDocumentResponse = new CreateOrderDocumentResponse();
        if (missingFieldError != null) {
            Response errorResponse = new Response();
            errorResponse.setStatus("Error");
            errorResponse.setErrorCode("400");
            errorResponse.setMessage(missingFieldError);
            createOrderDocumentResponse.setResponse(errorResponse);
        } else {
            OrderDocumentEntity entity = orderDocumentMapper.mapOrderDocumentSoapToEntity(request.getOrderDocument());
            try {
                Response response = new Response();
                if (request.getOrderDocument().getError() != null) {
                    response.setStatus("Error");
                    response.setErrorCode("400");
                    response.setMessage("Invalid contact number format");
                } else {
                    repository.save(entity);
                    repository.flush();
                    response.setStatus("Success");
                    response.setMessage("Service activated successfully");
                }
                createOrderDocumentResponse.setResponse(response);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateServiceIdException(
                        "Service with ID " + entity.getServiceId() + " already exists", e
                );
            }
        }
        return createOrderDocumentResponse;
    }

    public GetOrderDocumentListResponse getOrderDocumentList() {
        GetOrderDocumentListResponse response = new GetOrderDocumentListResponse();
        List<OrderDocumentEntity> orderDocumentEntityList = repository.findAll();
        List<OrderDocument> orderDocumentSoapList = new ArrayList<>();

        for (OrderDocumentEntity entity : orderDocumentEntityList){
            orderDocumentSoapList.add(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity));
        }
        LOGGER.info("Orders were successfully retrieved");
        response.getOrderDocument().addAll(orderDocumentSoapList);

        return response;
    }

    public GetOrderDocumentByIdResponse getOrderDocumentById(GetOrderDocumentByIdRequest request){
        GetOrderDocumentByIdResponse response = new GetOrderDocumentByIdResponse();
        OrderDocumentEntity orderDocumentEntity = repository.findByServiceId(request.getServiceId())
                .orElseThrow(() ->
                        new OrderDocumentNotFoundException("Order with ID " + request.getServiceId() + " does not exist"));
        LOGGER.info("Order with ID {} was successfully retrieved", request.getServiceId());

        response.setOrderDocument(orderDocumentMapper.mapOrderDocumentEntityToSoap(orderDocumentEntity));
        return response;
    }

    public UpdateOrderDocumentByIdResponse updateOrderDocumentById(UpdateOrderDocumentByIdRequest request){
        OrderDocumentEntity orderDocumentEntityFromDB = repository.findByServiceId(request.getOrderDocument().getServiceId())
                .orElseThrow(() -> new OrderDocumentNotFoundException
                        ("Order document of id " + request.getOrderDocument().getServiceId() + " not found"));

        orderDocumentTransformationService.transform(request.getOrderDocument()); // should set request.error
        String missingFieldError = orderDocumentValidator.validateMandatoryFields(request.getOrderDocument()); // mandatories +

        UpdateOrderDocumentByIdResponse updateOrderDocumentByIdResponse = new UpdateOrderDocumentByIdResponse();

        if (missingFieldError != null) {
            Response errorResponse = new Response();
            errorResponse.setStatus("Error");
            errorResponse.setErrorCode("400");
            errorResponse.setMessage(missingFieldError);
            updateOrderDocumentByIdResponse.setResponse(errorResponse);
            return updateOrderDocumentByIdResponse;
        }
        if (request.getOrderDocument().getError() != null){
            Response errorResponse = new Response();
            errorResponse.setStatus("Error");
            errorResponse.setErrorCode("400");
            errorResponse.setMessage("Invalid contact number format");
            updateOrderDocumentByIdResponse.setResponse(errorResponse);
            return updateOrderDocumentByIdResponse;
        }
        OrderDocumentEntity newValues =
                orderDocumentMapper.mapOrderDocumentSoapToEntity(request.getOrderDocument());

        orderDocumentUpdateMapper.updateUserEntity(newValues, orderDocumentEntityFromDB);

        repository.save(orderDocumentEntityFromDB);
        Response success = new Response();
        success.setStatus("Success");
        success.setMessage("Service updated successfully");
        updateOrderDocumentByIdResponse.setResponse(success);

        return updateOrderDocumentByIdResponse;
    }

    public DeleteOrderDocumentByIdResponse deleteOrderDocumentById(DeleteOrderDocumentByIdRequest request){
        OrderDocumentEntity orderDocumentEntity = repository.findByServiceId(request.getServiceId())
                .orElseThrow(() ->
                        new OrderDocumentNotFoundException("Order with ID " + request.getServiceId() + " does not exist"));
        repository.delete(orderDocumentEntity);

        DeleteOrderDocumentByIdResponse response = new DeleteOrderDocumentByIdResponse();
        response.setOrderDeletedMessage("Entry with id " + request.getServiceId() + " was deleted successfully");
        return response;
    }
}