package com.mehdi.oauth.controller;

import com.mehdi.oauth.model.Subscription;
import com.mehdi.oauth.service.JSONParser;
import com.mehdi.oauth.service.SubscriptionsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Base64;

@RestController("v1/subscriptions")
public class SubscriptionsController {
    private final SubscriptionsService subscriptionsService;

    SubscriptionsController(SubscriptionsService subscriptionsService) {
        this.subscriptionsService = subscriptionsService;
    }

    //CRUD
    //Create subscription
    @PostMapping(consumes = "application/json", produces = "application/json")
    public Subscription addNewSubscription(@RequestBody Subscription subscription) {
        return subscriptionsService.createSubscription(subscription);
    }


    @GetMapping("create_subscription")
    public RedirectView collectDataFromReturnUrl(@RequestParam("data64") String data64) {
        if (data64 != null && !data64.isEmpty()) {
            String decodedData = new String(Base64.getDecoder().decode(data64));
            System.out.println(decodedData);
            Subscription subscription = JSONParser.toSubscription(decodedData);
            subscriptionsService.createSubscription(subscription);

        }
        //redirect to localhost:5173
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://www.localhost:5173");
        return redirectView;
    }

}
