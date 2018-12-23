import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    int Generation = 1000;//进化代数
    int initSpeciesNum = 200;//初始种群个数
    float mutationProbability=0.3f;//变异概率
    int talentNum = 5; //将最大适应度物种复制talentNum个
    int facilitiesCount;//facility个数
    int customersCount;//消费者个数
    int[][] facilities;
    int[][] customers;
    int whichInstance;
    private List<List<Integer>> species = new ArrayList<>();
    private List<Integer> AllCost = new ArrayList<>();//记录所以个体的cost
    private List<Integer> AllFitness = new ArrayList<>();//记录所有个体的适应值，由AllCost通过 calAllFitness() 函数计算而来


    public GAResult GeneticAlgorithm(int[][] facilities, int[][] customers){
        facilitiesCount = facilities.length;
        customersCount = customers.length;
        this.facilities = facilities;
        this.customers = customers;
        int costSum = 0;
        //初始种群生成
        long startTime=System.currentTimeMillis();
        createBeginningSpecies();
        for(int i=1;i<=Generation;i++){
            //选择
            select(species);
            //交叉
            cross(species);
            //变异
            mutate(species);
        }
        int bestIndex = getBest(AllCost);
        costSum = AllCost.get(bestIndex);
        long endTime=System.currentTimeMillis();

        List<Integer> result = species.get(getBest(AllCost));
        int[] facilitiesStatus = new int[facilitiesCount];
        for(int i=0;i<customersCount;i++){
            facilitiesStatus[result.get(i)] = 1;
        }
        GAResult gaResult = new GAResult(endTime-startTime,facilitiesStatus,result,costSum);
        return gaResult;
    }

    //随机生成初始种群，坏的个体舍去，重新生成
    private void createBeginningSpecies(){
        GreedyAlgorithm greedy = new GreedyAlgorithm();
        GreedyResult greedyResult = greedy.Greedy(facilities,customers);
        List<Integer> greedyR = new ArrayList<>();

        for(int i=0;i<greedyResult.customersToFacilities.length;i++){
            greedyR.add(greedyResult.customersToFacilities[i]);
        }
        species.add(greedyR);
        for(int i=1;i<initSpeciesNum;i++){
            int[][] tempFacilities = copyFacility();
            List<Integer> createIndividual = new ArrayList<>();
            for(int j=0;j<customersCount;j++){
                Random r = new Random();
                int s = r.nextInt(facilitiesCount);
                while (tempFacilities[s][0] < customers[j][0]){
                    s = (s+1)%facilitiesCount;
                }
                tempFacilities[s][0] -= customers[j][0];
                createIndividual.add(s);
            }

            species.add(createIndividual);
            AllCost.add(calCost(createIndividual));
        }
        //更新适应值表AllFitness
        calAllFitness();
    }


    //选择
    private void select(List<List<Integer>> species){
        List<List<Integer>> newSpecies = new ArrayList<>();
        int bestIndex = getBest(AllCost);
        AllCost.clear();
        //复制最好个体talentNum次
        for(int i=0;i<talentNum;i++){
            newSpecies.add(species.get(bestIndex));
            AllCost.add(calCost(species.get(bestIndex)));
        }
        int fitnessSum = 0;//求出所有适应值之和
        for(int i=0;i<AllFitness.size();i++){
            fitnessSum += AllFitness.get(i);
        }

        int selectNum = species.size() - talentNum;
        //轮盘赌选择个体species.size() - talentNum次
        for(int i=0;i<selectNum;i++){
            int currentFitness = 0;
            //在0-fitnessSum之间产生一个随机数，用于轮盘赌选择个体
            Random r = new Random();
            int s;
            if(fitnessSum == 0)
                s = 0;
            else s = r.nextInt(fitnessSum);
            int index = 0;
            while (currentFitness<s){
                currentFitness += AllFitness.get(index);
                if(currentFitness > s)
                    break;
                index++;
            }
            newSpecies.add(species.get(index));
            AllCost.add(calCost(species.get(index)));
        }
        species.clear();
        species.addAll(newSpecies);//新种群替换掉原种群
        calAllFitness();//重新计算所有的适应值
    }

    //交叉
    private void cross(List<List<Integer>> species){
        int q=0;
        List<List<Integer>> nextGeneration = new ArrayList<>();
        List<Integer> nextGenerationCost = new ArrayList<>();
        while (q<species.size() && q+1<species.size()){
            Random rand=new Random();
            //在序列上随机取一个位置交叉
            int crossIndex=rand.nextInt(customersCount-1);
            List<Integer> offspring1 = new ArrayList<>();
            List<Integer> offspring2 = new ArrayList<>();
            List<Integer>  parent1 = species.get(q);
            List<Integer>  parent2 = species.get(q+1);
            for(int i=0;i<crossIndex;i++){
                offspring1.add(parent1.get(i));
                offspring2.add(parent2.get(i));
            }
            for(int i=crossIndex;i<customersCount;i++){
                offspring1.add(parent2.get(i));
                offspring2.add(parent1.get(i));
            }
            if(calCost(offspring1)!=888888){
                nextGeneration.add(offspring1);
                nextGenerationCost.add(calCost(offspring1));
            }
            else {
                nextGeneration.add(parent1);
                nextGenerationCost.add(calCost(parent1));
            }
            if(calCost(offspring2)!=888888){
                nextGeneration.add(offspring2);
                nextGenerationCost.add(calCost(offspring2));
            }
            else {
                nextGeneration.add(parent2);
                nextGenerationCost.add(calCost(parent2));
            }
            q+=2;
        }
        species.clear();
        species.addAll(nextGeneration);
        AllCost.clear();
        AllCost.addAll(nextGenerationCost);
        calAllFitness();
    }

    //变异
    private void mutate(List<List<Integer>> species){
        int i=0;
        //每个物种都可变异
        while (i<species.size()) {
            float rate=(float)Math.random();
            List<Integer> mutateOffspring = new ArrayList<>();
            if (rate < mutationProbability) {
                //寻找逆转左右端点
                Random rand = new Random();
                int left = rand.nextInt(customersCount);
                int right = rand.nextInt(customersCount);
                while (left == right) {
                    left = rand.nextInt(customersCount);
                    right = rand.nextInt(customersCount);
                }
                if (left > right) {
                    int temp;
                    temp = left;
                    left = right;
                    right = temp;
                }
                List<Integer> offspring = species.get(i);
                //逆转left-right的下标元素
                while (left < right) {
                    int temp = offspring.get(left);
                    offspring.set(left,offspring.get(right));
                    offspring.set(right,temp);
                    left++;
                    right--;
                }
                mutateOffspring = offspring;
                //变异出错误的个体直接淘汰
                //变异好的个体添加进种群，然后淘汰最差的个体,这样的收敛会快很多
                if (calCost(mutateOffspring) != 888888) {
                    species.add(mutateOffspring);
                    AllCost.add(calCost(mutateOffspring));
                    calAllFitness();
                    eliminateWorstIndividual();
                }
            }
            i++;
        }
        calAllFitness();
    }

    //淘汰种群中最差的个体
    void eliminateWorstIndividual(){
        int worstIndex = getWorst(AllCost);
        AllCost.remove(worstIndex);
        AllFitness.remove(worstIndex);
        species.remove(worstIndex);
    }

    //返回cost最低的个体的下标(也为fitness最高的个体的下标)
    int getBest(List<Integer> AllFitness){
        int temp = 0;
        int cost = AllFitness.get(0);
        int bestIndex = temp;
        for(temp = 1;temp<AllFitness.size();temp++){
            int costCurrent = AllFitness.get(temp);
            if(costCurrent < cost){
                cost = costCurrent;
                bestIndex = temp;
            }
        }
        return bestIndex;
    }

    //返回cost最高的个体的下标，选择时被淘汰
    Integer getWorst(List<Integer> AllFitness){
        int temp = 0;
        int cost = AllFitness.get(0);
        int worstIndex = temp;
        for(temp = 1;temp<AllFitness.size();temp++){
            int costCurrent = AllFitness.get(temp);
            if(costCurrent > cost){
                cost = costCurrent;
                worstIndex = temp;
            }
        }
        return worstIndex;
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
                return 888888;
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

    //计算所有的个体适应值——计算每一个个体的fitness = cost(max) - cost(currentIndividual)
    void calAllFitness(){
        AllFitness.clear();
        int maxCost = AllCost.get(0);
        for(int i=0;i<AllCost.size();i++){
            if(AllCost.get(i)>maxCost)
                maxCost = AllCost.get(i);
        }
        for(int i=0;i<AllCost.size();i++){
            AllFitness.add(maxCost-AllCost.get(i));
        }
    }
}