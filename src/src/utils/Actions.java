package utils;

public enum Actions {
    ADD_CLIENT(1),
    LIST_CLIENTS(2),
    ADD_BOOK(3),
    LIST_BOOKS(4),
    CHECKOUT_BOOK(5),
    RETURN_BOOK(6),
    VIEW_CHECKOUT_HISTORY(7);


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
