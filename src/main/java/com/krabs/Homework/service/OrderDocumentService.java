package com.krabs.Homework.service;

import com.customercontract.*;

import com.krabs.Homework.entity.OrderDocumentEntity;
import com.krabs.Homework.exception.DuplicateServiceIdException;
import com.krabs.Homework.exception.OrderDocumentNotFoundException;
import com.krabs.Homework.mapper.OrderDocumentMapper;
import com.krabs.Homework.mapper.OrderDocumentUpdateMapper;
import com.krabs.Homework.repository.CustomerContractRepository;
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

    private final Logger LOGGER = LoggerFactory.getLogger(OrderDocumentService.class);

    public void createOrderDocument(CreateOrderDocumentRequest request) {
        OrderDocumentEntity entity = orderDocumentMapper.mapOrderDocumentSoapToEntity(request.getOrderDocument());
        try {
            repository.save(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateServiceIdException(
                    "Service with ID " + entity.getServiceId() + " already exists", e
            );
        }
    }

    public CreateOrderDocumentResponse createOrderDocumentSoapResponse(){
        CreateOrderDocumentResponse response = new CreateOrderDocumentResponse();

        response.setStatus("success");

        return response;
    }

    public List<OrderDocument> getOrderDocumentList() {
        List<OrderDocumentEntity> orderDocumentEntityList = repository.findAll();
        List<OrderDocument> orderDocumentSoapList = new ArrayList<>();

        for (OrderDocumentEntity entity : orderDocumentEntityList){
            orderDocumentSoapList.add(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity));
        }
        LOGGER.info("Orders were successfully retrieved");

        return orderDocumentSoapList;
    }

    public GetOrderDocumentListResponse getOrderDocumentListSoapResponse() {
        GetOrderDocumentListResponse response = new GetOrderDocumentListResponse();
        response.getOrderDocument().addAll(getOrderDocumentList());

        return response;
    }

    public OrderDocument getOrderDocumentById(GetOrderDocumentByIdRequest request){
        OrderDocumentEntity orderDocumentEntity = repository.findByServiceId(request.getServiceId())
                .orElseThrow(() ->
                        new OrderDocumentNotFoundException("Order with ID " + request.getServiceId() + " does not exist"));
        LOGGER.info("Order with ID {} was successfully retrieved", request.getServiceId());

        return orderDocumentMapper.mapOrderDocumentEntityToSoap(orderDocumentEntity);
    }

    public GetOrderDocumentByIdResponse getOrderDocumentByIdSoapResponse(GetOrderDocumentByIdRequest request){
        GetOrderDocumentByIdResponse response = new GetOrderDocumentByIdResponse();
        response.setOrderDocument(getOrderDocumentById(request));

        return response;
    }

    public UpdateOrderDocumentByIdResponse updateOrderDocumentById(UpdateOrderDocumentByIdRequest request){

        OrderDocumentEntity orderDocumentEntityFromDB = repository.findByServiceId(request.getOrderDocument().getServiceId())
                .orElseThrow(() -> new OrderDocumentNotFoundException
                        ("Order document of id " + request.getOrderDocument().getServiceId() + " not found"));

        //TODO:play around with customerID, break it

        orderDocumentUpdateMapper.updateUserEntity(orderDocumentMapper.mapOrderDocumentSoapToEntity(
                request.getOrderDocument()), orderDocumentEntityFromDB );
        orderDocumentEntityFromDB.setCustomerId(""); //TODO: This causes problems, TEST CASE: this is not validated during entry in DB
        //TODO: Here we check for data validity before persisting to db
        repository.save(orderDocumentEntityFromDB);

        UpdateOrderDocumentByIdResponse response = new UpdateOrderDocumentByIdResponse();
        response.setOrderDocument(orderDocumentMapper.mapOrderDocumentEntityToSoap(orderDocumentEntityFromDB));
        return response;
    }

    public GetOrderDocumentByIdResponse updateOrderDocumentByIdSoapResponse(GetOrderDocumentByIdRequest request){
        GetOrderDocumentByIdResponse response = new GetOrderDocumentByIdResponse();
        response.setOrderDocument(getOrderDocumentById(request));

        return response;
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

    //TODO: error response method that sets Error, ErrorCode, ErrodMessage to the response if request fails!!!

}
