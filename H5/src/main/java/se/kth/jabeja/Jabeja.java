package se.kth.jabeja;

import org.apache.log4j.Logger;
import se.kth.jabeja.config.Config;
import se.kth.jabeja.config.NodeSelectionPolicy;
import se.kth.jabeja.io.FileIO;
import se.kth.jabeja.rand.RandNoGenerator;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Jabeja {
  final static Logger logger = Logger.getLogger(Jabeja.class);
  private final Config config;
  private final HashMap<Integer/*id*/, Node/*neighbors*/> entireGraph;
  private final List<Integer> nodeIds;
  private int numberOfSwaps;
  private int round;
  //private float T;
  private boolean resultFileCreated = false;

  // added
  private double T;
  private final double min_t = Math.pow(10, -5); // part 2: based on example code
  private boolean annealing = true;
  private int reset_rounds = 0; 
  private boolean bonus = true;



  //-------------------------------------------------------------------
  public Jabeja(HashMap<Integer, Node> graph, Config config) {
    this.entireGraph = graph;
    this.nodeIds = new ArrayList(entireGraph.keySet());
    this.round = 0;
    this.numberOfSwaps = 0;
    this.config = config;
    // modified by if-statement
    if (this.annealing){
        this.T = 1; // usually starts at 1
        config.setDelta((float) 0.9); // part 2: delta typically between 0.8 and 0.99
    }
    else{
        this.T = config.getTemperature(); // without annealing of part 2
    }
  }


  //-------------------------------------------------------------------
  public void startJabeja() throws IOException {
    for (round = 0; round < config.getRounds(); round++) {
      for (int id : entireGraph.keySet()) {
        sampleAndSwap(id);
      }

      //one cycle for all nodes have completed.
      //reduce the temperature
      saCoolDown();
      report();
    }
  }

  /**
  * - Added for acceptance probability
  */
  private double computeAcceptance(double new_val, double old_val){
      if (bonus){
        return Math.exp(((1/old_val) - (1/new_val)) / T)
          //return 1 / (1 + Math.exp((new_val - old_val) / T)) ; // gave bad performance
      }
      else{
          return Math.exp((new_val - old_val) / T); // acceptance probability for part 2
      }
  }

  /**
   * Simulated analealing cooling function
   */
  private void saCoolDown(){
    // TODO for second task - done
    if (annealing){ // part 2
        T *= config.getDelta(); // update T by multiplying with delta
        if (T < min_t){
            T = min_t; // lowest point
        }
        if (T == min_t){ // reset when lowest point has reached 
            reset_rounds++;
            if (reset_rounds == 400){ //restart simulated-annealing again after 400 rounds
                T = 1;
                reset_rounds = 0;
            }
        }
    }
    else{ // part 1
        if (T > 1) 
        T -= config.getDelta(); // update T by substracting delta (line 10 of algo)
        if (T < 1) // (line 11 of algo)
        T = 1; // always min value for T (line 12 of algo)
    }
  }

  /**
   * Sample and swap algorith at node p
   * @param nodeId
   */
  private void sampleAndSwap(int nodeId) {
    Node partner = null;
    Node nodep = entireGraph.get(nodeId);

    if (config.getNodeSelectionPolicy() == NodeSelectionPolicy.HYBRID
            || config.getNodeSelectionPolicy() == NodeSelectionPolicy.LOCAL) { // hybrid is first applying local
      // swap with random neighbors
      // TODO - done by adding partner
      partner = findPartner(nodeId, getNeighbors(nodep)); // (line 3 of algo)
    }

    if (config.getNodeSelectionPolicy() == NodeSelectionPolicy.HYBRID
            || config.getNodeSelectionPolicy() == NodeSelectionPolicy.RANDOM) { // hybrid is secondly applying random
      // if local policy fails then randomly sample the entire graph
      // TODO - done by adding if-statement
      if (partner == null){ // (line 4 of algo)
          partner = findPartner(nodeId, getSample(nodeId)); // (line 5 of algo)
      }
    }

    // swap the colors
    // TODO - done by adding if-statement
    if (partner != null){ //  (line 7 of algo)
        int aux = partner.getColor(); // color exchange handshake between p and partner
        partner.setColor(nodep.getColor());
        nodep.setColor(aux);
        numberOfSwaps++; // add color exchange to the number of swaps
    }
  }

  public Node findPartner(int nodeId, Integer[] nodes){ // find the best node as swap partner for node p

    Node nodep = entireGraph.get(nodeId); // obtain a node from graph and call it p

    Node bestPartner = null; 
    double highestBenefit = 0; 

    // TODO - done 
    for(Integer q: nodes){ // for q in nodes do (line 19 of algo)
        Node nodeq = entireGraph.get(q); // obtain a node from graph and call it q
        int degree_pp = getDegree(nodep, nodep.getColor()); // #neighbors of node p with same color as p (line 20 of algo)
        int degree_qq = getDegree(nodeq, nodeq.getColor()); // #neighbors of node q with same color as q (line 21 of algo)

        double old_d = Math.pow(degree_pp, config.getAlpha()) + Math.pow(degree_qq, config.getAlpha()); // pairwise utility with alpha as energy function parameter (line 22 of algo)

        int degree_pq = getDegree(nodep, nodeq.getColor()); // #neighbors of node p with same color as q (line 23 of algo)
        int degree_qp = getDegree(nodeq, nodep.getColor()); // #neighbors of node q with same color as p (line 24 of algo)

        double new_d = Math.pow(degree_pq, config.getAlpha()) + Math.pow(degree_qp, config.getAlpha()); // pairwise utility with alpha as energy function parameter (line 25 of algo)

        if (annealing){ // added for annealing
            Random random = new Random(); // first generate a random solution
            double prob = random.nextDouble(); // 
            double acceptance = computeAcceptance(new_d, old_d); // compute the new acceptance

            if (new_d != old_d && acceptance > prob && acceptance > highestBenefit){ // when the values are different, we move
                bestPartner = nodeq;  
                highestBenefit = acceptance; // updating values for highest benefit
            }
        }
        else{
            if (new_d * T > old_d && new_d > highestBenefit){ // annealing function of the paper (line 26 of algo)
                bestPartner = nodeq; // update best partner (line 27 of algo)
                highestBenefit = new_d; // update highest benefit (line 28 of algo)
            }
        }
    }

    return bestPartner; // return best partner (line 31 of algo)
  }

  /**
   * The the degreee on the node based on color
   * @param node
   * @param colorId
   * @return how many neighbors of the node have color == colorId
   */
  private int getDegree(Node node, int colorId){
    int degree = 0;
    for(int neighborId : node.getNeighbours()){
      Node neighbor = entireGraph.get(neighborId);
      if(neighbor.getColor() == colorId){
        degree++;
      }
    }
    return degree;
  }

  /**
   * Returns a uniformly random sample of the graph
   * @param currentNodeId
   * @return Returns a uniformly random sample of the graph
   */
  private Integer[] getSample(int currentNodeId) {
    int count = config.getUniformRandomSampleSize();
    int rndId;
    int size = entireGraph.size();
    ArrayList<Integer> rndIds = new ArrayList<Integer>();

    while (true) {
      rndId = nodeIds.get(RandNoGenerator.nextInt(size));
      if (rndId != currentNodeId && !rndIds.contains(rndId)) {
        rndIds.add(rndId);
        count--;
      }

      if (count == 0)
        break;
    }

    Integer[] ids = new Integer[rndIds.size()];
    return rndIds.toArray(ids);
  }

  /**
   * Get random neighbors. The number of random neighbors is controlled using
   * -closeByNeighbors command line argument which can be obtained from the config
   * using {@link Config#getRandomNeighborSampleSize()}
   * @param node
   * @return
   */
  private Integer[] getNeighbors(Node node) {
    ArrayList<Integer> list = node.getNeighbours();
    int count = config.getRandomNeighborSampleSize();
    int rndId;
    int index;
    int size = list.size();
    ArrayList<Integer> rndIds = new ArrayList<Integer>();

    if (size <= count)
      rndIds.addAll(list);
    else {
      while (true) {
        index = RandNoGenerator.nextInt(size);
        rndId = list.get(index);
        if (!rndIds.contains(rndId)) {
          rndIds.add(rndId);
          count--;
        }

        if (count == 0)
          break;
      }
    }

    Integer[] arr = new Integer[rndIds.size()];
    return rndIds.toArray(arr);
  }


  /**
   * Generate a report which is stored in a file in the output dir.
   *
   * @throws IOException
   */
  private void report() throws IOException {
    int grayLinks = 0;
    int migrations = 0; // number of nodes that have changed the initial color
    int size = entireGraph.size();

    for (int i : entireGraph.keySet()) {
      Node node = entireGraph.get(i);
      int nodeColor = node.getColor();
      ArrayList<Integer> nodeNeighbours = node.getNeighbours();

      if (nodeColor != node.getInitColor()) {
        migrations++;
      }

      if (nodeNeighbours != null) {
        for (int n : nodeNeighbours) {
          Node p = entireGraph.get(n);
          int pColor = p.getColor();

          if (nodeColor != pColor)
            grayLinks++;
        }
      }
    }

    int edgeCut = grayLinks / 2;

    logger.info("round: " + round +
            ", edge cut:" + edgeCut +
            ", swaps: " + numberOfSwaps +
            ", migrations: " + migrations);

    saveToFile(edgeCut, migrations);
  }

  private void saveToFile(int edgeCuts, int migrations) throws IOException {
    String delimiter = "\t\t";
    String outputFilePath;

    //output file name
    File inputFile = new File(config.getGraphFilePath());
    outputFilePath = config.getOutputDir() +
            File.separator +
            inputFile.getName() + "_" +
            "NS" + "_" + config.getNodeSelectionPolicy() + "_" +
            "GICP" + "_" + config.getGraphInitialColorPolicy() + "_" +
            "T" + "_" + config.getTemperature() + "_" +
            "D" + "_" + config.getDelta() + "_" +
            "RNSS" + "_" + config.getRandomNeighborSampleSize() + "_" +
            "URSS" + "_" + config.getUniformRandomSampleSize() + "_" +
            "A" + "_" + config.getAlpha() + "_" +
            "R" + "_" + config.getRounds() + ".txt";

    if (!resultFileCreated) {
      File outputDir = new File(config.getOutputDir());
      if (!outputDir.exists()) {
        if (!outputDir.mkdir()) {
          throw new IOException("Unable to create the output directory");
        }
      }
      // create folder and result file with header
      String header = "# Migration is number of nodes that have changed color.";
      header += "\n\nRound" + delimiter + "Edge-Cut" + delimiter + "Swaps" + delimiter + "Migrations" + delimiter + "Skipped" + "\n";
      FileIO.write(header, outputFilePath);
      resultFileCreated = true;
    }

    FileIO.append(round + delimiter + (edgeCuts) + delimiter + numberOfSwaps + delimiter + migrations + "\n", outputFilePath);
  }
}