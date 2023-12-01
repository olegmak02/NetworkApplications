package com.lab4;

class HttpReq {
    private String destination;
    private String url;
    private boolean cachable = false;

    private HttpReq(String destination, boolean cachable) {
        this.destination = destination;
        this.cachable = cachable;
    }

    public String getDestination() {
        return destination;
    }

    public static HttpReq parse(String request) {
        String[] lines = request.split("\n");
        String[] firstLine = lines[0].split("\\s+");
        String url = firstLine[1].trim();
        String destination = url.startsWith("http") ? url.split("/")[2].split(":")[0] : url.split(":")[0];
        HttpReq req = new HttpReq(destination, isCachable(request));
        req.url = url;
        return req;
    }

    private static boolean isCachable(String req) {
        if (!req.startsWith("GET")) {
            return false;
        }

        for (String line: req.split("\r\n")) {
            if (line.startsWith("Cache-Control:") && !line.contains("public") && !line.contains("max-age")) {
                return false;
            }
        }
        return true;
    }

    public boolean getCachable() {
        return this.cachable;
    }

    public String getUrl() {
        return this.url;
    }
}
