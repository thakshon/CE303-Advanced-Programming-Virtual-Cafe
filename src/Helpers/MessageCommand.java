package Helpers;

/**
 * All possible commands for a {@link Message}.
 */
public enum MessageCommand {
    EXIT,
    EXIT_OK,
    ORDER,
    ORDER_ACKNOWLEDGED,
    PING,
    PING_OK,
    UNKNOWN_COMMAND,
    ORDER_STATUS,
    ORDER_COMPLETE
}
