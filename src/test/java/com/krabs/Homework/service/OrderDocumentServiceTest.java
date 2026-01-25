package com.krabs.Homework.service;

import com.customercontract.*;
import com.krabs.Homework.entity.AddressEntity;
import com.krabs.Homework.entity.CustomerDetailsEntity;
import com.krabs.Homework.entity.OrderDocumentEntity;
import com.krabs.Homework.entity.ServiceDetailsEntity;
import com.krabs.Homework.exception.OrderDocumentNotFoundException;
import com.krabs.Homework.mapper.OrderDocumentMapper;
import com.krabs.Homework.mapper.OrderDocumentUpdateMapper;
import com.krabs.Homework.repository.CustomerContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
public class OrderDocumentServiceTest {

    @Mock
    CustomerContractRepository repository;
    @Mock
    OrderDocumentMapper orderDocumentMapper;
    @Mock
    OrderDocumentUpdateMapper orderDocumentUpdateMapper;
    @InjectMocks
    OrderDocumentService service;
    OrderDocumentEntity orderDocumentCorrect = new OrderDocumentEntity();

    @BeforeEach
    void setUp() {
        ServiceDetailsEntity serviceDetailsEntityCorrect = ServiceDetailsEntity.builder()
                .planType("Plan Type 1")
                .dataLimit("10GB")
                .roamingEnabled(true)
                .additionalServices(List.of("Service1"))
                .build();

        AddressEntity addressEntity = AddressEntity.builder()
                .street("Fake street")
                .city("Faketown")
                .postalCode("00000")
                .country("Fakelandia")
                .build();

        CustomerDetailsEntity customerDetailsEntityCorrect = CustomerDetailsEntity.builder()
                .name("John Doe")
                .addressEntity(addressEntity)
                .contactNumber("0123456789")
                .build();

        orderDocumentCorrect = OrderDocumentEntity.builder()
                .serviceId("001")
                .serviceType("Internet")
                .customerId("112233")
                .subscriptionId("1")
                .serviceDetailsEntity(serviceDetailsEntityCorrect)
                .customerDetailsEntity(customerDetailsEntityCorrect) // extra  fields
                .build();
    }

        @Test
    void shouldCreateOrderDocument() {
        OrderDocument orderDocument = new OrderDocument();
        CreateOrderDocumentRequest request = new CreateOrderDocumentRequest();
        request.setOrderDocument(orderDocument);

        OrderDocumentEntity entity = new OrderDocumentEntity();
        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(orderDocument))
                .thenReturn(entity);

        service.createOrderDocument(request);

        verify(repository).save(entity);
        verify(repository).flush();
    }

    @Test
    void shouldPersistOrderDocument() {
        // Arrange
        OrderDocument requestDoc = new OrderDocument();
        requestDoc.setServiceId("SVC-1"); // set some data

        CreateOrderDocumentRequest request = new CreateOrderDocumentRequest();
        request.setOrderDocument(requestDoc);

        // Stub the mapper to return an entity
        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("SVC-1");

        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(requestDoc))
                .thenReturn(entity);

        // Act
        service.createOrderDocument(request);

        // Assert
        verify(orderDocumentMapper).mapOrderDocumentSoapToEntity(requestDoc);
        verify(repository, times(1)).save(entity);
        verify(repository, times(1)).flush();
    }

    @Test
    void createOrderDocument_shouldSaveEntity() {
        // given
        OrderDocument soapDoc = new OrderDocument();
        soapDoc.setServiceId("001");

        CreateOrderDocumentRequest request = new CreateOrderDocumentRequest();
        request.setOrderDocument(soapDoc);

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("001");

        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(soapDoc))
                .thenReturn(entity);

        service.createOrderDocument(request);

        verify(orderDocumentMapper).mapOrderDocumentSoapToEntity(soapDoc);
        verify(repository).save(entity);
        verify(repository).flush();
    }

    @Test
    void createOrderDocumentSoapResponse_shouldReturnSuccess() {
        CreateOrderDocumentResponse response =
                service.createOrderDocumentSoapResponse();

        // then
        assertNotNull(response);
        assertEquals("success", response.getStatus());
    }

    @Test
    void getOrderDocumentById_shouldReturnOrderDocument() {
        GetOrderDocumentByIdRequest request = new GetOrderDocumentByIdRequest();
        request.setServiceId("12345");

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("12345");

        OrderDocument soapDoc = new OrderDocument();
        soapDoc.setServiceId("12345");

        when(repository.findByServiceId("12345"))
                .thenReturn(Optional.of(entity));

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity))
                .thenReturn(soapDoc);

        OrderDocument result = service.getOrderDocumentById(request);

        assertNotNull(result);
        assertEquals("12345", result.getServiceId());

        verify(repository).findByServiceId("12345");
        verify(orderDocumentMapper).mapOrderDocumentEntityToSoap(entity);
    }

    @Test
    void getOrderDocumentById_shouldReturnOrderDocument_andVIP() {
        GetOrderDocumentByIdRequest request = new GetOrderDocumentByIdRequest();
        request.setServiceId("123456789");

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("123456789");
        if ("123456789".equals(entity.getServiceId())){
            entity.setIsVipCustomer(true);
        }

        OrderDocument soapDoc = new OrderDocument();
        soapDoc.setServiceId("123456789");
        if (soapDoc.getServiceId().equals("123456789")){
            soapDoc.setVIPCustomer(true);
        }

        when(repository.findByServiceId("123456789"))
                .thenReturn(Optional.of(entity));

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity))
                .thenReturn(soapDoc);

        OrderDocument result = service.getOrderDocumentById(request);

        assertNotNull(result);
        System.out.println(result.getCustomerId());
        System.out.println(result.isVIPCustomer());
        assertEquals("123456789", result.getServiceId());
        assertEquals(true, result.isVIPCustomer());

        verify(repository).findByServiceId("123456789");
        verify(orderDocumentMapper).mapOrderDocumentEntityToSoap(entity);
    }

    @Test
    void getOrderDocumentById_shouldReturnOrderDocument_andNotVIP() {
        GetOrderDocumentByIdRequest request = new GetOrderDocumentByIdRequest();
        request.setServiceId("1111111111");

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("1111111111");
        if ("123456789".equals(entity.getServiceId())){
            entity.setIsVipCustomer(true);
        }

        OrderDocument soapDoc = new OrderDocument();
        soapDoc.setServiceId("1111111111");
        if (soapDoc.getServiceId().equals("123456789")){
            soapDoc.setVIPCustomer(true);
        }

        when(repository.findByServiceId("1111111111"))
                .thenReturn(Optional.of(entity));

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity))
                .thenReturn(soapDoc);

        OrderDocument result = service.getOrderDocumentById(request);

        assertNotNull(result);
        assertNull(result.isVIPCustomer());

        verify(repository).findByServiceId("1111111111");
        verify(orderDocumentMapper).mapOrderDocumentEntityToSoap(entity);
    }

    @Test
    void getOrderDocumentById_shouldReturnOrderDocument_andSpecialOffer() {
        GetOrderDocumentByIdRequest request = new GetOrderDocumentByIdRequest();
        request.setServiceId("150");

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("150");
        ServiceDetailsEntity serviceDetailsEntity = new ServiceDetailsEntity();
        serviceDetailsEntity.setPlanType("5G");
        entity.setServiceDetailsEntity(serviceDetailsEntity);
        if ("5G".equals(entity.getServiceDetailsEntity().getPlanType())
        && (entity.getServiceDetailsEntity().getDataLimit() == null)){
            entity.setSpecialOffer("ExtraData");
        }

        OrderDocument soapDoc = new OrderDocument();
        soapDoc.setServiceId("150");
        ServiceDetails serviceDetails = new ServiceDetails();
        serviceDetails.setPlanType("5G");
        soapDoc.setServiceDetails(serviceDetails);
        if ("5G".equals(soapDoc.getServiceDetails().getPlanType()) &&
        soapDoc.getServiceDetails().getDataLimit() == null){
            soapDoc.setSpecialOffer("ExtraData");
        }

        when(repository.findByServiceId("150"))
                .thenReturn(Optional.of(entity));

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity))
                .thenReturn(soapDoc);

        OrderDocument result = service.getOrderDocumentById(request);

        assertNotNull(result);
        assertEquals("ExtraData", result.getSpecialOffer());

        verify(repository).findByServiceId("150");
        verify(orderDocumentMapper).mapOrderDocumentEntityToSoap(entity);
    }

    @Test
    void getOrderDocumentList_shouldReturnAllMappedOrderDocuments() {
        OrderDocumentEntity entity1 = new OrderDocumentEntity();
        entity1.setServiceId("SVC-1");

        OrderDocumentEntity entity2 = new OrderDocumentEntity();
        entity2.setServiceId("SVC-2");

        OrderDocument soap1 = new OrderDocument();
        soap1.setServiceId("SVC-1");

        OrderDocument soap2 = new OrderDocument();
        soap2.setServiceId("SVC-2");

        when(repository.findAll())
                .thenReturn(List.of(entity1, entity2));

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity1))
                .thenReturn(soap1);

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity2))
                .thenReturn(soap2);

        List<OrderDocument> result = service.getOrderDocumentList();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("SVC-1", result.get(0).getServiceId());
        assertEquals("SVC-2", result.get(1).getServiceId());

        verify(repository).findAll();
        verify(orderDocumentMapper, times(2))
                .mapOrderDocumentEntityToSoap(any(OrderDocumentEntity.class));
    }

    @Test
    void updateOrderDocumentById_shouldUpdateAndReturnUpdatedDocument() {
        OrderDocument requestSoap = new OrderDocument();
        requestSoap.setServiceId("SVC-100");

        UpdateOrderDocumentByIdRequest request =
                new UpdateOrderDocumentByIdRequest();
        request.setOrderDocument(requestSoap);

        OrderDocumentEntity entityFromDb = new OrderDocumentEntity();
        entityFromDb.setServiceId("SVC-100");
        entityFromDb.setCustomerId("OLD");

        OrderDocumentEntity mappedFromSoap = new OrderDocumentEntity();
        mappedFromSoap.setServiceId("SVC-100");

        OrderDocument updatedSoap = new OrderDocument();
        updatedSoap.setServiceId("SVC-100");

        when(repository.findByServiceId("SVC-100"))
                .thenReturn(Optional.of(entityFromDb));

        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(requestSoap))
                .thenReturn(mappedFromSoap);

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entityFromDb))
                .thenReturn(updatedSoap);

        UpdateOrderDocumentByIdResponse response =
                service.updateOrderDocumentById(request);

        assertNotNull(response);
        assertNotNull(response.getOrderDocument());
        assertEquals("SVC-100",
                response.getOrderDocument().getServiceId());

        verify(repository).findByServiceId("SVC-100");
        verify(orderDocumentUpdateMapper)
                .updateUserEntity(mappedFromSoap, entityFromDb);
        verify(repository).save(entityFromDb);
        verify(orderDocumentMapper)
                .mapOrderDocumentEntityToSoap(entityFromDb);
    }

    @Test
    void updateOrderDocumentById_shouldThrowException_whenNotFound() {
        // given
        OrderDocument requestSoap = new OrderDocument();
        requestSoap.setServiceId("MISSING");

        UpdateOrderDocumentByIdRequest request =
                new UpdateOrderDocumentByIdRequest();
        request.setOrderDocument(requestSoap);

        when(repository.findByServiceId("MISSING"))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderDocumentNotFoundException.class,
                () -> service.updateOrderDocumentById(request)
        );

        verify(repository).findByServiceId("MISSING");
        verifyNoInteractions(orderDocumentMapper);
        verifyNoInteractions(orderDocumentUpdateMapper);
    }

    @Test
    void deleteOrderDocumentById_shouldDeleteEntityAndReturnResponse() {
        // given
        DeleteOrderDocumentByIdRequest request =
                new DeleteOrderDocumentByIdRequest();
        request.setServiceId("SVC-999");

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("SVC-999");

        when(repository.findByServiceId("SVC-999"))
                .thenReturn(Optional.of(entity));

        // when
        DeleteOrderDocumentByIdResponse response =
                service.deleteOrderDocumentById(request);

        // then
        assertNotNull(response);
        assertEquals(
                "Entry with id SVC-999 was deleted successfully",
                response.getOrderDeletedMessage()
        );

        verify(repository).findByServiceId("SVC-999");
        verify(repository).delete(entity);
    }

    @Test
    void deleteOrderDocumentById_shouldThrowException_whenNotFound() {
        // given
        DeleteOrderDocumentByIdRequest request =
                new DeleteOrderDocumentByIdRequest();
        request.setServiceId("MISSING");

        when(repository.findByServiceId("MISSING"))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(
                OrderDocumentNotFoundException.class,
                () -> service.deleteOrderDocumentById(request)
        );

        verify(repository).findByServiceId("MISSING");
        verify(repository, never()).delete(any());
    }
}
