public enum Turn {
    MIN,
    MAX;

    public Turn next_turn() {
        return (this == MAX) ? MIN : MAX;
    }
}
