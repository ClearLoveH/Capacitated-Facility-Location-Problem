import java.util.List;

public class GAResult {
    long time;
    int[] facilitiesStatus;
    List<Integer> customersToFacilities;
    int cost;

    public GAResult(long time,int[] facilitiesStatus, List<Integer> customersToFacilities, int cost){
        this.cost = cost;
        this.customersToFacilities = customersToFacilities;
        this.facilitiesStatus = facilitiesStatus;
        this.time = time;
    }
}
