package com.krabs.Homework;

import com.customercontract.CreateOrderDocumentRequest;
import com.customercontract.OrderDocument;
import com.krabs.Homework.entity.OrderDocumentEntity;
import com.krabs.Homework.mapper.OrderDocumentMapper;
import com.krabs.Homework.mapper.OrderDocumentUpdateMapper;
import com.krabs.Homework.repository.CustomerContractRepository;
import com.krabs.Homework.service.OrderDocumentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class HomeworkApplicationTests {

	@Mock
	CustomerContractRepository repository;

	@Mock
	OrderDocumentMapper orderDocumentMapper;

	@Mock
	OrderDocumentUpdateMapper orderDocumentUpdateMapper;

	@InjectMocks
	OrderDocumentService service;

	@Test
	void shouldCreateOrderDocument() {
		// given
		// use the actual generated class
		OrderDocument orderDocumentSoap = new OrderDocument();
		CreateOrderDocumentRequest request = new CreateOrderDocumentRequest();
		request.setOrderDocument(orderDocumentSoap);

		OrderDocumentEntity entity = new OrderDocumentEntity();
		when(orderDocumentMapper.mapOrderDocumentSoapToEntity(orderDocumentSoap))
				.thenReturn(entity);

		// when
		service.createOrderDocument(request);

		// then
		verify(repository).save(entity);
		verify(repository).flush();
	}



}
