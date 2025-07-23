package Examples;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class DateTime {
    public static void main(String[] args) {
        var now = LocalDateTime.now();
        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dt = LocalDateTime.of(2025, 07, 22, 14, 30);
        // Duration duration = Duration.between(start,end);
        System.out.println(dt.format(format));
    }
}