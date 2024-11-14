package utils;

public enum Actions {
    ADD_CLIENT(1),
    ADD_BOOK(2),
    LIST_BOOKS(3),
    CHECKOUT_BOOK(4),
    RETURN_BOOK(5),
    VIEW_CHECKOUT_HISTORY(6);


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
