
public class CommandLine {
    public static void main(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            System.out.println("args[" + i + "] = " + args[i]);

        }
        int i = 0;
        for (String value : args) {
            System.out.printf("args[%d] = %s\n", i, args[i]);
            ++i;
        }
    }
}