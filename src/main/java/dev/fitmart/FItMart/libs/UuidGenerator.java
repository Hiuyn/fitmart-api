package dev.fitmart.FItMart.libs;

import java.util.*;

public class UuidGenerator {
    public static String generateCustomUuid() {
        // Generate UUID and customize it, for example by adding a prefix
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "FM" + uuid;
    }
}
