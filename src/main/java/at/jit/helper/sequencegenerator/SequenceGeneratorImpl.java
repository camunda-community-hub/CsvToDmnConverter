package at.jit.helper.sequencegenerator;

public class SequenceGeneratorImpl implements SequenceGenerator {
    private int value = 1;

    @Override
    public int getNext() {
        return value++;
    }
}
