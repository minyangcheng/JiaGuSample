public class HelloWorld {
    int a = 0;
    static String b = "HelloDalvik";

    public int getNumber(int i, int j) {
        int e = 3;
        return e + i + j;
    }

    public static void main(String[] args) {
        int c = 1;
        int d = 2;
        HelloWorld helloWorld = new HelloWorld();
        String sayNumber = String.valueOf(helloWorld.getNumber(c, d));
        System.out.println("HelloDex!" + sayNumber);
    }
}