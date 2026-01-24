package com.krabs.Homework.transformator;

import com.customercontract.CreateOrderDocumentRequest;
import com.customercontract.OrderDocument;
import org.springframework.stereotype.Component;

@Component
//@Service?
public class OrderDocumentTransformationService {

    private String vipCustomerId = "123456789";

    public void transform(CreateOrderDocumentRequest request){
        OrderDocument orderDocument = request.getOrderDocument();
        applyVipCustomer(orderDocument);
        applySpecialOffer(orderDocument);
        applyRoamingEnabledRemoval(orderDocument);
    }

    private void applyVipCustomer(OrderDocument orderDocument){
        if (vipCustomerId.equals(orderDocument.getCustomerId())){
            orderDocument.setVIPCustomer(true);
        }
    };

    //TODO: edgecase: after update if datalimit or plantype changes: remove special offer, change to null
    private void applySpecialOffer(OrderDocument orderDocument){
        if ("5G".equalsIgnoreCase(orderDocument.getServiceDetails().getPlanType())
                && (orderDocument.getServiceDetails().getDataLimit() == null
                || orderDocument.getServiceDetails().getDataLimit().isEmpty())){
            orderDocument.setSpecialOffer("ExtraData");
        }
    }

    //TODO: edgecase: after update if country changes : RoamingEnabled must reappear.
    private void applyRoamingEnabledRemoval(OrderDocument orderDocument){
        if (!"Sweden".equalsIgnoreCase(orderDocument.getCustomerDetails().getAddress().getCountry())){
            orderDocument.getServiceDetails().setRoamingEnabled(false); // TODO: FIGURE OUT HOW!
        }
    }
    private void applyRoamingEnabledRemoval2 (CreateOrderDocumentRequest createOrderDocumentRequest){
        if (!"Sweden".equalsIgnoreCase(createOrderDocumentRequest.
                getOrderDocument().
                getCustomerDetails().
                getAddress().getCountry())){

        }
    }


}
