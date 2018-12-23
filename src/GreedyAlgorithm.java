public class GreedyAlgorithm {
    int facilitiesCount;//facility个数
    int customersCount;//消费者个数`
    int[][] facilities;
    int[][] customers;
    public GreedyAlgorithm(){}

    //贪心算法，优先选择当前满足要求的facility中assignment cost最小的。
    public GreedyResult Greedy(int[][] facilities, int[][] customers){
        this.facilities = facilities;
        this.customers = customers;
        facilitiesCount = facilities.length;
        customersCount = customers.length;
        int[][] tempFacilities = copyFacility();
        int[] facilitiesStatus = new int[tempFacilities.length];
        int[] customersToFacilities = new int[customers.length];
        long startTime=System.currentTimeMillis();
        int costSum = 0;

        for(int i = 0;i<customers.length;i++){
            int minCost = 99999;
            int selectIndex = -1;
            for(int j =0;j<tempFacilities.length;j++){
                int currentCost = 0;
                //facility的capacity需要大于当前customer的需求
                if(tempFacilities[j][0]<customers[i][0]){
                    continue;
                }
//                if(facilitiesStatus[j]==0){
//                    currentCost += tempFacilities[j][1];
//                }
                currentCost += customers[i][j+1];
                if(currentCost <= minCost){
                    minCost = currentCost;
                    selectIndex = j;
                }
            }
            if(facilitiesStatus[selectIndex] == 0){
                facilitiesStatus[selectIndex] = 1;
                costSum += tempFacilities[selectIndex][1];
            }
            customersToFacilities[i] = selectIndex;
            tempFacilities[selectIndex][0] -= customers[i][0];
            costSum += minCost;
        }
        try {
            Thread.sleep(1000 * 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime=System.currentTimeMillis();
        GreedyResult greedyResult = new GreedyResult(endTime-startTime,facilitiesStatus,customersToFacilities,costSum);
        return greedyResult;
    }

    int[][] copyFacility(){
        int[][] temp = new int[facilitiesCount][2];
        for(int i=0;i<facilitiesCount;i++){
            for(int j=0;j<2;j++)
                temp[i][j] = facilities[i][j];
        }
        return temp;
    }
}
