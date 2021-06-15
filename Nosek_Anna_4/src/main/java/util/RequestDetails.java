package util;

public final class RequestDetails {
    public final long queryId;
    public final int firstSatId;
    public final int range;
    public final int timeout;

    public RequestDetails(long queryId, int firstSatId, int range, int timeout){
        this.queryId = queryId;

        this.firstSatId = firstSatId;
        this.range = range;
        this.timeout = timeout;
    }
}