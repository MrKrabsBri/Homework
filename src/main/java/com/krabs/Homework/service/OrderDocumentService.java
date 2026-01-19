package com.krabs.Homework.service;

import com.customercontract.*;

import com.krabs.Homework.entity.OrderDocumentEntity;
import com.krabs.Homework.exception.DuplicateServiceIdException;
import com.krabs.Homework.exception.OrderDocumentNotFoundException;
import com.krabs.Homework.mapper.OrderDocumentMapper;
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
    private final OrderDocumentMapper mapper;
    private final Logger LOGGER = LoggerFactory.getLogger(OrderDocumentService.class);

    public void createOrderDocument(CreateOrderDocumentRequest request) {
        OrderDocumentEntity entity = mapper.mapOrderDocumentSoapToEntity(request.getOrderDocument());
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
            orderDocumentSoapList.add(mapper.mapOrderDocumentEntityToSoap(entity));
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

        return mapper.mapOrderDocumentEntityToSoap(orderDocumentEntity);
    }

    public GetOrderDocumentByIdResponse getOrderDocumentByIdSoapResponse(GetOrderDocumentByIdRequest request){
        GetOrderDocumentByIdResponse response = new GetOrderDocumentByIdResponse();
        response.setOrderDocument(getOrderDocumentById(request));

        return response;
    }

}
