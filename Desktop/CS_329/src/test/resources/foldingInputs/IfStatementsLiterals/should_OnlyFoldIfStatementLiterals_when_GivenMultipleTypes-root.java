package foldingInputs.IfStatementsLiterals;

public class should_OnlyFoldIfStatementLiterals_when_GivenMultipleTypes {
    public int name(final int y) {
        final int x = 3 + 5;
        final boolean b = ((true));
        final Integer i = (null);
        final char c = 'c';
        final String s = new String("Hello");
        final int z = 4 - 3;
        if (true) {
            int t = 10;
        }

        if (false) {
            int t = 20;
        }
        return x;
    }
}
