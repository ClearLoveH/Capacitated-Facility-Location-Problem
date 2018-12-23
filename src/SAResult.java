public class SAResult {
    long time;
    int[] facilitiesStatus;
    int[] customersToFacilities;
    int cost;

    public SAResult(long time,int[] facilitiesStatus, int[] customersToFacilities, int cost){
        this.cost = cost;
        this.customersToFacilities = customersToFacilities;
        this.facilitiesStatus = facilitiesStatus;
        this.time = time;
    }
}
