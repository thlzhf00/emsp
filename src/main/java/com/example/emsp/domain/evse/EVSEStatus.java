package com.example.emsp.domain.evse;

public enum EVSEStatus {
    AVAILABLE,    // Ready to be used
    BLOCKED,      // Reserved or temporarily unavailable
    INOPERATIVE,  // Out of service due to malfunction or maintenance
    REMOVED;      // Permanently decommissioned (terminal state)

    /**
     * Checks if a transition from the current status to a new status is valid according to defined rules.
     *
     * @param currentStatus The current status of the EVSE.
     * @param newStatus     The desired new status for the EVSE.
     * @return true if the transition is valid, false otherwise.
     */
    public static boolean isValidTransition(EVSEStatus currentStatus, EVSEStatus newStatus) {
        // Any state can transition to REMOVED (irreversible)
        if (newStatus == REMOVED) {
            return true;
        }

        // INITIAL -> AVAILABLE (handled during creation, implicitly valid)
        if (currentStatus == null && newStatus == AVAILABLE) {
            return true;
        }

        if (currentStatus == null && (newStatus == BLOCKED || newStatus == INOPERATIVE)) {
            return false;
        }

        return switch (currentStatus) {
            case AVAILABLE -> newStatus == BLOCKED || newStatus == INOPERATIVE;
            case BLOCKED, INOPERATIVE -> newStatus == AVAILABLE;
            case REMOVED -> false; // Cannot transition out of REMOVED
            default -> false; // Should not happen for unhandled states
        };
    }
}