import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulatedAnnealing {
    int facilitiesCount;//facility个数
    int customersCount;//消费者个数`
    int[][] facilities;
    int[][] customers;
    int Iteration = 200;
    double Temperature = 150;//初温
    double EPS=1e-6;   //温度下届
    double coolingCoefficient = 0.99;
    static int INVALID = 888888; //判断产生的新解是否可用

    public SAResult SimulatedAnnealing(int[][] facilities, int[][] customers){
        this.facilities = facilities;
        this.customers = customers;
        facilitiesCount = facilities.length;
        customersCount = customers.length;
        int[][] tempFacilities = copyFacility();
        int[] facilitiesStatus = new int[tempFacilities.length];
        int[] customersToFacilities = new int[customers.length];
        long startTime=System.currentTimeMillis();
        int costCurrent = 0;

        //使用贪心算法来求初值
        GreedyAlgorithm greedy = new GreedyAlgorithm();
        GreedyResult greedyResult = greedy.Greedy(facilities,customers);
        List<Integer> initList = new ArrayList<>();
        for(int i=0;i<greedyResult.customersToFacilities.length;i++){
            initList.add(greedyResult.customersToFacilities[i]);
        }

        //使用随机解产生初值
//        for(int i=1;i<customersCount;i++){
//            int[][] tempFacilities1 = copyFacility();
//            for(int j=0;j<customersCount;j++){
//                Random r = new Random();
//                int s = r.nextInt(facilitiesCount);
//                while (tempFacilities1[s][0] < customers[j][0]){
//                    s = (s+1)%facilitiesCount;
//                }
//                tempFacilities1[s][0] -= customers[j][0];
//                initList.add(s);
//            }
//        }

        costCurrent = calCost(initList);

        int qwer = 0;
//        int count = 0;//降温次数
        while (Temperature > EPS){
            int count = 0;// 迭代次数
            while (count < Iteration){
                List<Integer> tempList = new ArrayList<>();
                tempList.addAll(initList);
                qwer++;
                count++;
                Random r = new Random();

                int newResult = INVALID;
                int oldResult = costCurrent;
//                int randomTime = 0;//随机次数
//                while (randomTime < 10){
                while (newResult == INVALID){
                    //随机产生新解
                    int n = r.nextInt(customersCount);
                    int m = r.nextInt(facilitiesCount);

                    //领域操作——随机变换一个新的设备选择
                    tempList.set(n,m);
                    newResult = calCost(tempList);
                }
//                    randomTime++;
//                }
                float accessProbability = r.nextFloat();
//                System.out.println("new Result:" + newResult);
                //以1的概率接受新解
                if(newResult < oldResult){
                    initList.clear();
                    initList.addAll(tempList);
                    costCurrent = newResult;
                }
                //以exp(-ΔT/T)接受新解，用以跳出局部最优
                else if(accessProbability < Math.exp((oldResult - newResult) / Temperature)){
                    initList.clear();
                    initList.addAll(tempList);
                    costCurrent = newResult;
                }
//                System.out.println("Current Temperature: "+ Temperature);
//                System.out.println("Current Result:" + initList);
//                System.out.println("Current Cost: "+ costCurrent);
//                System.out.println("Cool times:" + qwer);
//                System.out.println("----------------");
            }
            Temperature *= coolingCoefficient;
        }
        long endTime=System.currentTimeMillis();
        for(int i=0;i<customersCount;i++){
            customersToFacilities[i] = initList.get(i);
            facilitiesStatus[initList.get(i)] = 1;
        }
        return new SAResult(endTime-startTime,facilitiesStatus,customersToFacilities,costCurrent);
    }

    int[][] copyFacility(){
        int[][] temp = new int[facilitiesCount][2];
        for(int i=0;i<facilitiesCount;i++){
            for(int j=0;j<2;j++)
                temp[i][j] = facilities[i][j];
        }
        return temp;
    }

    //计算个体的cost
    int calCost(List<Integer> individualSpecies){
        int fitness = 0;
        int[] facilitiesStatus = new int[facilitiesCount];
        int[][] tempFacilities = copyFacility();


        for (int i=0;i<customersCount;i++){
            int toWhichFacility = individualSpecies.get(i);
            //如果出了坏种(facility的capacity超出了)，给其添上惩罚值
            if(tempFacilities[toWhichFacility][0] < 0){
                return INVALID;
            }
            if(facilitiesStatus[toWhichFacility] == 0){
                facilitiesStatus[toWhichFacility] = 1;
                fitness += tempFacilities[toWhichFacility][1];
            }
            fitness += customers[i][toWhichFacility+1];
            tempFacilities[toWhichFacility][0] -= customers[i][0];
        }
        return fitness;
    }
}
