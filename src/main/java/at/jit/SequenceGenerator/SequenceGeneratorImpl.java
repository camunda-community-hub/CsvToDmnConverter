package at.jit.SequenceGenerator;

public class SequenceGeneratorImpl implements SequenceGenerator{
    private int value = 1;

    @Override
    public int getNext() {
        return value++;
    }
}
