package Examples;

// 6. Reflection - Access Private Field
class Secret {
    String name = "anurag";
    private String hidden = "secret";
}

class Reflect {
    public static void main(String[] args) throws Exception {
        // System.out.println(s.name);
        // System.out.println(s.hidden);
        
        var s = new Secret();
        // getField() only works for public fields
        var field = Secret.class.getDeclaredField("hidden");
        // using reflection, bypasses Java's access control checks
        field.setAccessible(true);
        System.out.println(field.get(s));
    }
}