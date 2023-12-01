package com.lab4;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    public volatile static Map<String, byte[]> cachedFiles = new HashMap<>();
}
