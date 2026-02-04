package com.bank.account_service.util;

public final class AppConstants {


    private AppConstants() {}

    // ============ ACCOUNT STATUS MESSAGES ============
    public static final String ACCOUNT_ACTIVE_MSG =
            "Your account is active";

    public static final String ACCOUNT_DORMANT_MSG =
            "Account is dormant due to inactivity. Please contact support to reactivate";

    public static final String ACCOUNT_BLOCKED_MSG =
            "Account has been blocked. Please contact customer support";

    public static final String ACCOUNT_CLOSED_MSG =
            "This account has been permanently closed";

    // ============ ACCOUNT TYPE MESSAGES ============
    public static final String ACC_TYPE_SAVINGS_DESC =
            "Regular savings account with interest benefits";

    public static final String ACC_TYPE_CURRENT_DESC =
            "Business current account with unlimited transactions";

    public static final String ACC_TYPE_FD_DESC =
            "Fixed deposit with guaranteed returns";

    public static final String ACC_TYPE_RD_DESC =
            "Recurring deposit for systematic savings";

    // ============ CARD STATUS MESSAGES ============
    public static final String CARD_ACTIVE_MSG =
            "Your card is active and ready to use";

    public static final String CARD_BLOCKED_MSG =
            "Card has been blocked for security reasons";

    public static final String CARD_EXPIRED_MSG =
            "Card has expired. Please request a new card";

    public static final String CARD_NOT_ISSUED_MSG =
            "Contact support to issue a new card";

    // ============ LOAN STATUS MESSAGES ============
    public static final String LOAN_ACTIVE_MSG =
            "Your loan is active. Next EMI due on";

    public static final String LOAN_REQUESTED_MSG =
            "Loan application under review. You'll be notified within 2-3 business days";

    public static final String LOAN_REJECTED_MSG =
            "Loan application rejected. Please contact support for details";

    public static final String LOAN_CLOSED_MSG =
            "Congratulations! Your loan has been fully repaid";

    public static final String LOAN_DEFAULTED_MSG =
            "Please contact support immediately regarding your loan";

    // ============ INSURANCE STATUS MESSAGES ============
    public static final String INSURANCE_ACTIVE_MSG =
            "Your insurance policy is active and providing coverage";

    public static final String INSURANCE_REQUESTED_MSG =
            "Insurance request under review";

    public static final String INSURANCE_REJECTED_MSG =
            "Insurance request rejected";

    public static final String INSURANCE_CANCELLED_MSG =
            "Insurance policy has been cancelled";

    public static final String INSURANCE_EXPIRED_MSG =
            "Insurance policy has expired. Please renew to continue coverage";

    // ============ CREDIT CARD STATUS MESSAGES ============
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_REJECTED = "REJECTED";

    public static final String TITLE_APPROVED = "Congratulations 🎉";
    public static final String MSG_APPROVED =
            "Your credit card has been approved instantly";

    public static final String DESC_APPROVED =
            "Based on your transaction history, your credit card is approved automatically";

    public static final String NEXT_APPROVED =
            "Check your dashboard to view card details and available limit";

    public static final String TITLE_PENDING = "Application Submitted";
    public static final String MSG_PENDING =
            "Your credit card application is under review";

    public static final String DESC_PENDING =
            "Our team is reviewing your application. Approval usually takes 2–3 business days";

    public static final String NEXT_PENDING =
            "You will receive SMS and Email updates";
    public static final String COUNT = "count";
    public static final String REQUEST_ID = "requestId";
    public static final String REQUESTS = "requests";


    // ============ COMMON KEYS ============
    public static final String SUCCESS = "success";
    public static final String STATUS = "status";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String DESCRIPTION = "description";
    public static final String NEXT_STEPS = "nextSteps";
    public static final String DATA = "data";

}