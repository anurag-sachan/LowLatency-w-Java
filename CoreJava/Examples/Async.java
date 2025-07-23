package Examples;

class Async {
    public static void main(String[] args) {
        // starts the function but immediately return completable future > that holds the result "Hello"
        java.util.concurrent.CompletableFuture.supplyAsync(() -> "Hello")
            // this waits(asynchronously) for result from completable future
            .thenApply(s -> s+" World")
            .thenAccept(System.out::println);
            // .thenAccept(s->System.out.println(s));
    }
}
