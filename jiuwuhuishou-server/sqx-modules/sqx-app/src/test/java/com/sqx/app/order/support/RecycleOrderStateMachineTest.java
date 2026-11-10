package com.sqx.app.order.support;

import com.sqx.app.order.enums.RecycleOrderStatus;
import com.sqx.common.core.exception.BizException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecycleOrderStateMachineTest {

    @Test
    void shouldAllowWaitAssignToCancelled() {
        assertDoesNotThrow(() -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.WAIT_ASSIGN.getCode(), RecycleOrderStatus.CANCELLED));
    }

    @Test
    void shouldRejectCompletedToCancelled() {
        assertThrows(BizException.class, () -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.COMPLETED.getCode(), RecycleOrderStatus.CANCELLED));
    }

    @Test
    void shouldAllowWaitAssignToExceptionPending() {
        assertDoesNotThrow(() -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.WAIT_ASSIGN.getCode(), RecycleOrderStatus.EXCEPTION_PENDING));
    }

    @Test
    void canCancelWhenWaitAssign() {
        assertTrue(RecycleOrderStateMachine.canCancel(RecycleOrderStatus.WAIT_ASSIGN.getCode()));
    }

    @Test
    void cannotCancelWhenCompleted() {
        assertFalse(RecycleOrderStateMachine.canCancel(RecycleOrderStatus.COMPLETED.getCode()));
    }

    @Test
    void shouldAllowWaitAssignToInProgress() {
        assertDoesNotThrow(() -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.WAIT_ASSIGN.getCode(), RecycleOrderStatus.IN_PROGRESS));
    }

    @Test
    void canCancelWhenWaitSettlementConfirm() {
        assertTrue(RecycleOrderStateMachine.canCancel(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode()));
    }

    @Test
    void cannotRescheduleWhenWaitSettlementConfirm() {
        assertFalse(RecycleOrderStateMachine.canReschedule(RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode()));
    }

    @Test
    void shouldAllowWaitSettlementConfirmToCancelled() {
        assertDoesNotThrow(() -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode(), RecycleOrderStatus.CANCELLED));
    }

    @Test
    void shouldAllowWaitSettlementConfirmToWaitPayout() {
        assertDoesNotThrow(() -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getCode(), RecycleOrderStatus.WAIT_PAYOUT));
    }

    @Test
    void shouldRejectWaitAssignToCompleted() {
        assertThrows(BizException.class, () -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.WAIT_ASSIGN.getCode(), RecycleOrderStatus.COMPLETED));
    }

    @Test
    void cannotRescheduleWhenCompleted() {
        assertFalse(RecycleOrderStateMachine.canReschedule(RecycleOrderStatus.COMPLETED.getCode()));
    }

    @Test
    void canRescheduleWhenWaitAssign() {
        assertTrue(RecycleOrderStateMachine.canReschedule(RecycleOrderStatus.WAIT_ASSIGN.getCode()));
    }

    @Test
    void shouldAllowExceptionPendingBackToWaitAssign() {
        assertDoesNotThrow(() -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.EXCEPTION_PENDING.getCode(), RecycleOrderStatus.WAIT_ASSIGN));
    }

    @Test
    void shouldAllowWaitPayoutToPayoutProcessing() {
        assertDoesNotThrow(() -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.WAIT_PAYOUT.getCode(), RecycleOrderStatus.PAYOUT_PROCESSING));
    }

    @Test
    void shouldAllowPayoutProcessingToCompleted() {
        assertDoesNotThrow(() -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.PAYOUT_PROCESSING.getCode(), RecycleOrderStatus.COMPLETED));
    }

    @Test
    void shouldRejectCompletedToWaitAssign() {
        assertThrows(BizException.class, () -> RecycleOrderStateMachine.assertCanTransition(
                RecycleOrderStatus.COMPLETED.getCode(), RecycleOrderStatus.WAIT_ASSIGN));
    }

    @Test
    void statusKeyShouldMatchFrontendContract() {
        assertEquals("waitAssign", RecycleOrderStatus.WAIT_ASSIGN.getStatusKey());
        assertEquals("pendingConfirm", RecycleOrderStatus.WAIT_SETTLEMENT_CONFIRM.getStatusKey());
        assertEquals("running", RecycleOrderStatus.IN_PROGRESS.getStatusKey());
        assertEquals("done", RecycleOrderStatus.COMPLETED.getStatusKey());
    }
}
