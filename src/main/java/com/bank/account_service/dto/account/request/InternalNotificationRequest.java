package com.bank.account_service.dto.account.request;
import lombok.Data;
import java.util.UUID;

@Data
public class InternalNotificationRequest {

    private UUID userId;
    private String message;
    private String type;
}