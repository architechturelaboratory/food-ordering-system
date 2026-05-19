package com.food.ordering.system.saga;

public enum SagaStatus {
    STARTED, PROCESSING,
    COMPENSATING, COMPENSATED,
    FAILED, SUCCEEDED
}
