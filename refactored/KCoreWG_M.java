import java.io.File;
import java.io.PrintStream;
import it.unimi.dsi.webgraph.ImmutableGraph;

public class KCoreWG_M {
    private ImmutableGraph graph;
    private int numNodes;
    private int numEdges;
    private int maxDegree;
    private int[] coreNumbers;
    private boolean[] isScheduled;
    private boolean showProgress = false;
    private int iterationCount = 0;
    private boolean hasChanged = false;

    public KCoreWG_M(String filename) throws Exception {
        loadGraph(filename);
        initializeGraphInfo();
    }

    private void loadGraph(String filename) throws Exception {
        this.graph = ImmutableGraph.loadMapped(filename);
    }

    private void initializeGraphInfo() {
        numNodes = graph.numNodes();
        coreNumbers = new int[numNodes];
        isScheduled = new boolean[numNodes];
        numEdges = calculateNumEdgesAndMaxDegree();
    }

    private int calculateNumEdgesAndMaxDegree() {
        int totalEdges = 0;
        for (int node = 0; node < numNodes; node++) {
            int degree = graph.outdegree(node);
            totalEdges += degree;
            maxDegree = Math.max(maxDegree, degree);
            isScheduled[node] = true;
        }
        return totalEdges / 2; // Divide by 2 since each edge is counted twice
    }

    private void updateMaxDegree(int degree) {
        if (degree > maxDegree) {
            maxDegree = degree;
        }
    }

    private void setNodeAsScheduled(int node) {
        if (node < isScheduled.length) {
            isScheduled[node] = true;
        }
    }

    private void updateCoreNumber(int node) {
        if (iterationCount == 0) {
            initializeCoreNumberOnFirstIteration(node);
        } else {
            updateCoreOnSubsequentIterations(node);
        }
    }

    private void initializeCoreNumberOnFirstIteration(int node) {
        int nodeDegree = graph.outdegree(node);
        coreNumbers[node] = nodeDegree;
        markNodeAsScheduled(node);
        setHasChanged(true);
    }

    private void markNodeAsScheduled(int node) {
        if (node < isScheduled.length) {
            isScheduled[node] = true;
        }
    }

    private void setHasChanged(boolean value) {
        hasChanged = value;
    }

    private void updateCoreOnSubsequentIterations(int node) {
        int nodeDegree;

        nodeDegree = graph.outdegree(node);
        int[] neighbors = graph.successorArray(node);
        int localEstimate = computeEstimatedCore(node, nodeDegree, neighbors);

        if (localEstimate < coreNumbers[node]) {
            coreNumbers[node] = localEstimate;
            setHasChanged(true);
            updateScheduledNodes(node, neighbors);
        }
    }

    private int computeEstimatedCore(int currentNode, int currentDegree, int[] adjacentNodes) {
        int[] coreFrequency = calculateCoreFrequency(currentNode, adjacentNodes);
        return calculateLocalEstimate(currentNode, coreFrequency);
    }

    private int[] calculateCoreFrequency(int currentNode, int[] adjacentNodes) {
        int maxCoreValue = coreNumbers[currentNode] + 1;
        int[] coreFrequency = new int[maxCoreValue];

        for (int neighborNode : adjacentNodes) {
            int minCoreValue = Math.min(coreNumbers[currentNode], coreNumbers[neighborNode]);
            if (minCoreValue < maxCoreValue) {
                coreFrequency[minCoreValue]++;
            }
        }

        return coreFrequency;
    }

    private int calculateLocalEstimate(int currentNode, int[] frequencyArray) {
        int currentNodeCore = coreNumbers[currentNode];

        for (int core = currentNodeCore; core >= 2; core--) {
            int cumulativeFrequency = calculateCumulativeFrequency(core, frequencyArray);

            if (cumulativeFrequency >= core) {
                return core;
            }
        }

        return currentNodeCore;
    }

    private int calculateUpperBound(int node, int nodeDegree, int[] neighbors) {
        int[] coreFrequency = calculateCoreFrequency(node, neighbors);
        return determineUpperBound(node, coreFrequency);
    }

    private int determineUpperBound(int node, int[] coreFrequency) {
        return 0;
    }

    private int determineUpperBound(int node, int[] coreFrequency, int nodeDegree) {
        int currentNodeCore = coreNumbers[node];

        for (int core = currentNodeCore; core >= 2; core--) {
            int cumulative = calculateCumulativeFrequency(core, coreFrequency);

            if (cumulative >= core) {
                return core;
            }
        }

        return nodeDegree;
    }

    private int calculateCumulativeFrequency(int core, int[] coreFrequency) {
        int cumulative = 0;

        for (int i = core; i < coreFrequency.length; i++) {
            cumulative += coreFrequency[i];
        }

        return cumulative;
    }

    private void updateScheduledNodes(int node, int[] neighbors) {
        for (int neighborNode : neighbors) {
            if (coreNumbers[node] <= coreNumbers[neighborNode]) {
                scheduleNeighborNode(neighborNode);
            }
        }
    }

    private void scheduleNeighborNode(int neighborNode) {
        if (neighborNode < coreNumbers.length) {
            isScheduled[neighborNode] = true;
        }
    }

    public int[] computeKCore() {
        int maxIterations = numNodes;
        boolean isConverged = false;

        while (iterationCount < maxIterations && !isConverged) {
            System.out.print("Iteration " + iterationCount);

            int scheduledCount = 0;
            boolean[] currentScheduled = isScheduled.clone();
            resetIsScheduledArray();

            for (int node = 0; node < numNodes; node++) {
                if (currentScheduled[node]) {
                    scheduledCount++;
                    updateCoreNumber(node);
                }
            }

            displayScheduledNodesPercentage(scheduledCount);
            iterationCount++;

            isConverged = !hasChanged;
            hasChanged = false;
        }

        return coreNumbers;
    }

    private void resetIsScheduledArray() {
        for (int i = 0; i < numNodes; i++) {
            isScheduled[i] = false;
        }
    }

    private void displayScheduledNodesPercentage(int scheduledCount) {
        System.out.println(
                "\t\t" + ((100.0 * scheduledCount) / numNodes) + "%\t of nodes were scheduled this iteration.");
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        //String basename = "amazon0601"; // Set the basename directly

        System.out.println("Starting " + basename);
        KCoreWG_M kCore = null;
        PrintStream ps = null;
        int[] result = null;

        try {
            kCore = new KCoreWG_M(basename);
            ps = new PrintStream(new File(basename + ".cores"));
            result = kCore.computeKCore();

            int maxCore = computeMaxCore(result);

            double averageCore = computeAverageCore(result);

            printCoreStatistics(result, kCore.numEdges, kCore.maxDegree, maxCore, averageCore, ps);

            System.out
                    .println(basename + ": Time elapsed (sec) = " + (System.currentTimeMillis() - startTime) / 1000.0);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static int computeMaxCore(int[] coreArray) {
        int max = -1;
        for (int core : coreArray) {
            if (core > max) {
                max = core;
            }
        }
        return max;
    }

    private static double computeAverageCore(int[] coreArray) {
        double sum = 0;
        int count = 0;
        for (int core : coreArray) {
            sum += core;
            if (core > 0) {
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
    }

    private static void printCoreStatistics(int[] result, int numEdges, int maxDegree, int maxCore, double averageCore,
            PrintStream ps) {
        double sumCores = calculateSumCores(result, ps);
        int countNodes = countPositiveNodes(result);

        System.out.println(String.format(
                "Vertex Count (|V|)\tEdge Count (|E|)\tMax Degree (dmax)\tMax Core (kmax)\tAverage Core (kavg)\n%d\t%d\t%d\t%d\t%.6f",
                countNodes, (numEdges / 2), maxDegree, maxCore, averageCore));
    }

    private static double calculateSumCores(int[] result, PrintStream ps) {
        double sum = 0;
        for (int i = 0; i < result.length; i++) {
            sum += result[i];
        }
        printResultValues(result, ps);
        return sum;
    }

    private static void printResultValues(int[] result, PrintStream ps) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < result.length; i++) {
            stringBuilder.append(i).append(":").append(result[i]).append(" \n");
        }

        ps.print(stringBuilder.toString());
    }

    private static int countPositiveNodes(int[] result) {
        int count = 0;
        for (int i = 0; i < result.length; i++) {
            if (result[i] > 0) {
                count++;
            }
        }
        return count;
    }

}
