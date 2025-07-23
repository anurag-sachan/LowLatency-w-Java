package example;

// 6. Reflection - Access Private Field
class Secret {
    String name = "anurag";
    private String hidden = "secret";

    private String hiddenFxn(){
        return "secret from function";
    }
}

class Reflect {
    public static void main(String[] args) throws Exception {
        var s = new Secret();
        // System.out.println(s.name);
        // System.out.println(s.hidden);
        // System.out.println(s.hiddenFxn());
        
        // getField() only works for public fields
        var field = Secret.class.getDeclaredField("hidden");
        var functionValue = Secret.class.getDeclaredMethod("hiddenFxn");

        // using reflection, bypasses Java's access control checks
        field.setAccessible(true);
        functionValue.setAccessible(true);

        System.out.println(field.get(s));
        System.out.println(functionValue.invoke(s));
    }
}