package utils;

public enum Actions {
    ADD_CLIENT(1),
    LIST_CLIENTS(2),
    ADD_AUTHOR(3),
    LIST_AUTHORS(4),
    ADD_BOOK(5),
    LIST_BOOKS(6),
    CHECKOUT_BOOK(7),
    RETURN_BOOK(8),
    VIEW_CHECKOUT_HISTORY(9);


    private final int actionCode;

    Actions(int actionCode) {
        this.actionCode = actionCode;
    }

    private int getActionCode() {
        return actionCode;
    }

    public static Actions fromCode(int code) {
        for (Actions action : Actions.values()) {
            if (action.getActionCode() == code) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid action code: " + code);
    }
}
