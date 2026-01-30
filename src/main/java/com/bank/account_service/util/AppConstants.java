package com.bank.account_service.util;

public final class AppConstants {

    private AppConstants() {}

    // ---------- COMMON KEYS ----------
    public static final String SUCCESS = "success";
    public static final String STATUS = "status";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String DESCRIPTION = "description";
    public static final String NEXT_STEPS = "nextSteps";
    public static final String REQUEST_ID = "requestId";
    public static final String COUNT = "count";
    public static final String REQUESTS = "requests";
    public static final String DATA = "data";
    public static final String REASON = "reason";
    public static final String NOTIFICATION = "notification";

    // ---------- APPLY CREDIT CARD ----------
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_PENDING = "PENDING";

    public static final String TITLE_APPROVED = "Congratulations 🎉";
    public static final String MSG_APPROVED =
            "Your credit card has been approved instantly";
    public static final String DESC_APPROVED =
            "Based on your transaction history, your credit card is approved automatically.";
    public static final String NEXT_APPROVED =
            "Check your dashboard to view card details and available limit";

    public static final String TITLE_PENDING = "Application Submitted";
    public static final String MSG_PENDING =
            "Your credit card application is under review";
    public static final String DESC_PENDING =
            "Our team is reviewing your application. Approval usually takes 2–3 business days.";
    public static final String NEXT_PENDING =
            "You will receive SMS and Email updates";

    public static final String NO_PENDING_MSG =
            "No pending credit card applications";
    public static final String NO_PENDING_DESC =
            "All applications have been processed";

    public static final String ADMIN_PENDING_DESC =
            "Review customer profile and credit history before approval";

    public static final String APPROVE_TITLE =
            "Credit Card Approved";
    public static final String APPROVE_MSG =
            "Credit card has been issued successfully";
    public static final String APPROVE_DESC =
            "Customer can now view credit card details in dashboard";

    public static final String REJECT_TITLE =
            "Application Rejected";
    public static final String REJECT_MSG =
            "Credit card application rejected";
    public static final String REJECT_DESC =
            "Customer has been notified about the rejection";

    public static final String NOTIFICATION_SENT =
            "Customer notified via SMS and Email";


    public static final String LOAN_ACTIVE_MSG =
            "Your loan is active";

    public static final String LOAN_REQUESTED_MSG =
            "Loan application under review";

    public static final String LOAN_REJECTED_MSG =
            "Loan application rejected";

    public static final String LOAN_CLOSED_MSG =
            "Loan closed successfully";

    public static final String LOAN_DEFAULTED_MSG =
            "Please contact support immediately regarding your loan";

    public static final String INSURANCE_ACTIVE_MSG =
            "Your insurance policy is active";

    public static final String INSURANCE_REQUESTED_MSG =
            "Insurance request under review";

    public static final String INSURANCE_REJECTED_MSG =
            "Insurance request rejected";

    public static final String INSURANCE_CANCELLED_MSG =
            "Insurance policy cancelled";

    public static final String INSURANCE_EXPIRED_MSG =
            "Insurance policy expired";
}