import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
    private static List<List<Integer>> numsList = new ArrayList<>();
    private static int[] resultCostGreedy = new int[71];
    private static long[] resultTimeGreedy = new long[71];
    private static int[] resultCostGenetic = new int[71];
    private static long[] resultTimeGenetic = new long[71];
    private static int[] resultCostSimulated = new int[71];
    private static long[] resultTimeSimulated = new long[71];
    private static int[][][] facility = new int[71][][];
    private static int[][][] customer = new int[71][][];
    static List<File> fileList = new ArrayList<>();

    public static void main(String args[]) {
        for (int i = 0; i <= 70; i++) {
            File file = new File("instances/p" + (i + 1));
            fileList.add(file);
            decodeFile(file,i);
        }
        System.out.println("----------------------------");
        System.out.println("--------  贪心算法   --------");
        for (int i = 0; i <= 70; i++) {
            //贪心
            GreedyAlgorithm greedy = new GreedyAlgorithm();

            //以下为获取并输出结果的detail部分
            GreedyResult greedyResult = greedy.Greedy(facility[i], customer[i]);
            resultCostGreedy[i] = greedyResult.cost;
            long time=greedyResult.time;
            resultTimeGreedy[i] = time;
            System.out.println("----------------------------");
            System.out.println("P" + (i+1) +": 总的开销：" + greedyResult.cost);
            System.out.println("Status of facilities: ");
            for (int k=0;k<greedyResult.facilitiesStatus.length;k++){
                System.out.print(greedyResult.facilitiesStatus[k] + " ");
            }
            System.out.println();
            System.out.println("The assignment of customers to facilities: ");
            for (int k=0;k<greedyResult.customersToFacilities.length;k++){
                System.out.print(greedyResult.customersToFacilities[k] + " ");
            }
            System.out.println();
        }
        System.out.println("----------------------------");

        //输出Result Table csv文件
        createCSVFile createCSVFile = new createCSVFile();
        List<Result> results = new ArrayList<>();
        String[] header = {"","Result","Time(ms)"};
        String[] properties = {"id","costSum","costTime"};
        for (int i = 0; i <= 70; i++) {
            Result result = new Result();
            result.setId("P"+(i+1));
            result.setCostSum(resultCostGreedy[i]);
            result.setCostTime(resultTimeGreedy[i]);
            results.add(result);
        }
        try {
            createCSVFile.exportCsv("GreedyResult",header,properties,results);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        System.out.println("\n\n\n----------------------------");
        System.out.println("--------  GA算法   ---------");

        for(int j=0;j<=70;j++){
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

            //以下为获取并输出结果的detail部分
            GAResult gaResult = geneticAlgorithm.GeneticAlgorithm(facility[j], customer[j]);
            resultCostGenetic[j] = gaResult.cost;
            resultTimeGenetic[j] = gaResult.time;
            System.out.println("----------------------------");
            System.out.println("P" + (j+1) +": 总的开销：" + gaResult.cost);
            System.out.println("Status of facilities: ");
            for (int k=0;k<gaResult.facilitiesStatus.length;k++){
                System.out.print(gaResult.facilitiesStatus[k] + " ");
            }
            System.out.println();
            System.out.println("The assignment of customers to facilities: ");
            for (int k=0;k<gaResult.customersToFacilities.size();k++){
                System.out.print(gaResult.customersToFacilities.get(k) + " ");
            }
            System.out.println();
        }
        System.out.println("----------------------------");


        //输出Result Table csv文件
        results.clear();
        for (int j = 0; j <= 70; j++) {
            Result result = new Result();
            result.setId("P"+(j+1));
            result.setCostSum(resultCostGenetic[j]);
            result.setCostTime(resultTimeGenetic[j]);
            results.add(result);
        }
        try {
            createCSVFile.exportCsv("GAResult",header,properties,results);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        System.out.println("\n\n\n----------------------------");
        System.out.println("--------  SA算法   ---------");

        for(int j=0;j<=70;j++){
            SimulatedAnnealing simulatedAnnealing =new SimulatedAnnealing();

            //以下为获取并输出结果的detail部分
            SAResult saResult = simulatedAnnealing.SimulatedAnnealing(facility[j],customer[j]);
            resultCostSimulated[j] = saResult.cost;
            resultTimeSimulated[j] = saResult.time;
            System.out.println("----------------------------");
            System.out.println("P" + (j+1) +": 总的开销：" + saResult.cost);
            System.out.println("Status of facilities: ");
            for (int k=0;k<saResult.facilitiesStatus.length;k++){
                System.out.print(saResult.facilitiesStatus[k] + " ");
            }
            System.out.println();
            System.out.println("The assignment of customers to facilities: ");
            for (int k=0;k<saResult.customersToFacilities.length;k++){
                System.out.print(saResult.customersToFacilities[k] + " ");
            }
            System.out.println();
        }
        System.out.println("----------------------------");


        //输出Result Table csv文件
        results.clear();
        for (int j = 0; j <= 70; j++) {
            Result result = new Result();
            result.setId("P"+(j+1));
            result.setCostSum(resultCostSimulated[j]);
            result.setCostTime(resultTimeSimulated[j]);
            results.add(result);
        }
        try {
            createCSVFile.exportCsv("SAResult",header,properties,results);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //封装好用于解析文件
    public static void decodeFile(File file,int i){
        List<Integer> integers = new ArrayList<>();
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int facilityCount = sc.nextInt();
        int customerCount = sc.nextInt();
        int count = 0;
        int size = facilityCount * 2;
        int doubleSize = customerCount * (facilityCount + 1);
        while (count < size + doubleSize) {
            count++;
            integers.add((int) sc.nextDouble());
        }
        facility[i] = new int[facilityCount][2];
        customer[i] = new int[customerCount][facilityCount + 1];
        int temp = 0;
        for (int j = 0; j < facilityCount * 2; j++) {
            facility[i][j / 2][j % 2] = integers.get(j);
            temp = j;
        }
        temp++;
        for (int j = temp; j < temp + customerCount; j++) {
            customer[i][j - temp][0] = integers.get(j);
        }
        temp = temp + customerCount;
        for (int j = temp; j < integers.size(); j++) {
            customer[i][(j - temp) / facilityCount][(j - temp) % facilityCount + 1] = integers.get(j);
        }
    }
}
