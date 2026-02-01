package com.bank.account_service.util;

public final class AppConstants {

    public static String COUNT;
    public static String REQUEST_ID;
    public static String REQUESTS;

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

    public static final String TITLE_REJECTED = "Application Rejected";
    public static final String MSG_REJECTED =
            "Your credit card application has been rejected";

    public static final String DESC_REJECTED =
            "Unfortunately, we cannot approve your application at this time";

    // ============ BALANCE & LIMIT MESSAGES ============
    public static final String BALANCE_FETCHED_MSG =
            "Balance fetched successfully";

    public static final String INSUFFICIENT_BALANCE_MSG =
            "Insufficient balance in your account";

    public static final String DAILY_LIMIT_EXCEEDED_MSG =
            "Daily transaction limit exceeded";

    public static final String PER_TXN_LIMIT_EXCEEDED_MSG =
            "Per transaction limit exceeded";

    // ============ PASSWORD MESSAGES ============
    public static final String PASSWORD_CHANGED_MSG =
            "Password changed successfully";

    public static final String OLD_PASSWORD_INCORRECT_MSG =
            "The old password you entered is incorrect";

    public static final String PASSWORD_SAME_MSG =
            "New password cannot be same as old password";

    // ============ COMMON KEYS ============
    public static final String SUCCESS = "success";
    public static final String STATUS = "status";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String DESCRIPTION = "description";
    public static final String NEXT_STEPS = "nextSteps";
    public static final String DATA = "data";
    public static final String ACCOUNT_ID = "accountId";
    public static final String CUSTOMER_ID = "customerId";
}