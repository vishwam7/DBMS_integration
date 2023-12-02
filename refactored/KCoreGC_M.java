import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import edu.cmu.graphchi.ChiFilenames;
import edu.cmu.graphchi.ChiLogger;
import edu.cmu.graphchi.ChiVertex;
import edu.cmu.graphchi.GraphChiContext;
import edu.cmu.graphchi.GraphChiProgram;
import edu.cmu.graphchi.datablocks.IntConverter;
import edu.cmu.graphchi.engine.GraphChiEngine;
import edu.cmu.graphchi.engine.VertexInterval;
import edu.cmu.graphchi.io.CompressedIO;
import edu.cmu.graphchi.preprocessing.EdgeProcessor;
import edu.cmu.graphchi.preprocessing.FastSharder;
import edu.cmu.graphchi.preprocessing.VertexIdTranslate;
import edu.cmu.graphchi.preprocessing.VertexProcessor;
import edu.cmu.graphchi.util.IdInt;
import edu.cmu.graphchi.util.Toplist;

public class KCoreGC_M implements GraphChiProgram<Integer, Integer> {

    public static final int INFINITY;

    protected int vertexValuesUpdated;
    protected int nVertexesScheduled;
    protected int nVertexes;
    private int nIterations;
    private static Logger logger;

    static{
        INFINITY = Integer.MAX_VALUE;
        logger = ChiLogger.getLogger("kCoreDecomposition");
    }

    {
        vertexValuesUpdated = 0;
        nVertexesScheduled = 0;
        nIterations = 0;
        nVertexes = 0;
    }

    public void update(ChiVertex<Integer, Integer> v, GraphChiContext context) {
    
        if (context.getIteration() == 0) {
            initializeVertexValue(v, v.numOutEdges(), context);
        } else {
            updateVertexValue(v, context);
        }
    }
    
    private void initializeVertexValue(ChiVertex<Integer, Integer> v, int degree, GraphChiContext context) {
        v.setValue(degree);
        broadcastValueToNeighbors(v, degree);
        updateCounters(context);
        scheduleVertex(v, context);
    }
    
    private void updateVertexValue(ChiVertex<Integer, Integer> v, GraphChiContext context) {
        int localEstimate = computeUpperBound(v);
        if (localEstimate < v.getValue()) {
            updateVertexWithBound(v, localEstimate, context);
        }
    }
    
    private void updateVertexWithBound(ChiVertex<Integer, Integer> v, int localEstimate, GraphChiContext context) {
        v.setValue(localEstimate);
        broadcastValueToNeighbors(v, localEstimate);
        updateCounters(context);
        scheduleUpdatedVertices(v, localEstimate, context);
    }
    
    private void updateCounters(GraphChiContext context) {
        vertexValuesUpdated++;
        nVertexesScheduled++;
    }

    private void broadcastValueToNeighbors(ChiVertex<Integer, Integer> vertex, int value) {
        updateOutEdges(vertex, value); 
    }   
    
    private void updateOutEdges(ChiVertex<Integer, Integer> vertex, int value){
        int i = 0;
        while (i < vertex.numOutEdges()) {
            vertex.outEdge(i).setValue(value);
            i++;
        }
    }

private void scheduleVertex(ChiVertex<Integer, Integer> v, GraphChiContext context) {
    context.getScheduler().addTask(v.getId());
}

private void scheduleUpdatedVertices(ChiVertex<Integer, Integer> v, int localEstimate, GraphChiContext context) {
    int d_v = v.numOutEdges();
    for (int i = 0; i < d_v; i++) {
        int core_u = v.inEdge(i).getValue();
        if (localEstimate <= core_u) {
            scheduleVertexUpdate(v.inEdge(i).getVertexId(), context);
        }
    }
}

private void scheduleVertexUpdate(int vertexId, GraphChiContext context) {
    context.getScheduler().addTask(vertexId);
    nVertexesScheduled++;
}

private int computeUpperBound(ChiVertex<Integer, Integer> v) {
    int[] coreCount = calculateCoreCounts(v);
    return findUpperBound(coreCount, v.numOutEdges(), v.getValue());
}

private int[] calculateCoreCounts(ChiVertex<Integer, Integer> v) {
    int[] coreCounts = computeCoreCounts(v);
    return coreCounts;
}

private int[] computeCoreCounts(ChiVertex<Integer, Integer> v) {
    int[] coreCount = initializeCoreCountArray(v.getValue());

    for (int i = 0; i < v.numOutEdges(); i++) {
        int core_u = v.inEdge(i).getValue();
        int j = Math.min(v.getValue(), core_u);
        coreCount[j]++;
    }

    return coreCount;
}

private int[] initializeCoreCountArray(int size) {
    int[] coreCount = new int[size + 1];
    Arrays.fill(coreCount, 0);
    return coreCount;
}

private int findUpperBound(int[] coreCount, int d_v, int core_v) {
    return calculateUpperBound(coreCount, core_v, determineThreshold(coreCount, core_v), d_v);
}

private int calculateUpperBound(int[] coreCount, int core_v, int threshold, int d_v) {
    for (int i = core_v; i >= 2; i--) {
        if (coreCount[i] >= threshold) {
            return i;
        }
        threshold -= coreCount[i];
    }
    return d_v;
}

private int determineThreshold(int[] coreCount, int core_v) {
    int cumulativeCount = 0;
    for (int i = core_v; i >= 2; i--) {
        cumulativeCount += coreCount[i];
    }
    return cumulativeCount;
}

public void broadcastValue(ChiVertex<Integer, Integer> vertex, int value) {
    updateOutEdges(vertex, value);
}

public void beginIteration(GraphChiContext ctx) {
    resetIterationVariables();
}

private void resetIterationVariables() {
    vertexValuesUpdated = 0;
    nVertexesScheduled = 0;
}

public void endIteration(GraphChiContext ctx) {
    printIterationSummary(ctx);
    updateIterations(ctx);
}

private void printIterationSummary(GraphChiContext ctx) {
    printUpdates();
    printScheduledVertices();
    printIterationEnd(ctx);
}

private void printUpdates() {
    System.out.println(vertexValuesUpdated + " updates.");
}

private void printScheduledVertices() {
    System.out.println(nVertexesScheduled + " vertices scheduled for the next iteration.");
}

private void printIterationEnd(GraphChiContext ctx) {
    System.out.println("iteration " + ctx.getIteration() + " ends.");
}

private void updateIterations(GraphChiContext ctx) {
    nIterations++;
    checkForNoUpdates(ctx);
}

private void checkForNoUpdates(GraphChiContext ctx) {
    if (areUpdatesZero()) {
        handleNoUpdates(ctx);
    }
}

private boolean areUpdatesZero() {
    return vertexValuesUpdated == 0;
}

private void handleNoUpdates(GraphChiContext ctx) {
    System.out.println("no updates in this round. No more rounds .. KCore-montresor terminates!");
    removeAllTasks(ctx);
}

private void removeAllTasks(GraphChiContext ctx) {
    ctx.getScheduler().removeAllTasks();
}

public void beginInterval(GraphChiContext ctx, VertexInterval interval) {
    try {
        
        if (ctx != null && interval != null) {
            System.out.println("Begin Interval: " + ctx.toString() + ", " + interval.toString());
        } else {
            System.out.println("Invalid context or interval.");
        }
    } catch (Exception e) {
        System.err.println("An error occurred but handled.");
    }
}

public void endInterval(GraphChiContext ctx, VertexInterval interval) {
    try {
        
        if (ctx != null || interval != null) {
            System.out.println("End Interval: " + ctx.toString() + ", " + interval.toString());
        } else {
            System.out.println("Both context and interval are null.");
        }
    } catch (Exception e) {
        System.err.println("An error occurred but handled.");
    }
}

public void beginSubInterval(GraphChiContext ctx, VertexInterval interval) {
    try {
        
        if (ctx == null && interval == null) {
            System.out.println("Begin SubInterval: " + ctx.toString() + ", " + interval.toString());
        } else {
            System.out.println("Context or interval is not null.");
        }
    } catch (Exception e) {
        System.err.println("An error occurred but handled.");
    }
}

public void endSubInterval(GraphChiContext ctx, VertexInterval interval) {
    try {
    
        if (ctx != null && interval == null) {
            System.out.println("End SubInterval: " + ctx.toString() + ", " + interval.toString());
        } else {
            System.out.println("Context is not null or interval is null.");
        }
    } catch (Exception e) {
        System.err.println("An error occurred but handled.");
    }
}


protected static FastSharder createSharder(String graphName, int numShards) throws IOException {
    return initializeSharder(graphName, numShards);
}

private static FastSharder initializeSharder(String graphName, int numShards) throws IOException {
    return new FastSharder<Integer, Integer>(graphName, numShards, getVertexProcessor(), getEdgeProcessor(), new IntConverter(), new IntConverter());
}

private static VertexProcessor<Integer> getVertexProcessor() {
    return new VertexProcessor<Integer>() {
        public Integer receiveVertexValue(int vertexId, String token) {
            return 0;
        }
    };
}

private static EdgeProcessor<Integer> getEdgeProcessor() {
    return new EdgeProcessor<Integer>() {
        public Integer receiveEdge(int from, int to, String token) {
            return 0;
        }
    };
}

public static void main(String[] args) throws FileNotFoundException, IOException {
    KCoreGC_M kCoreGC_M = new KCoreGC_M();
    long startTime = System.currentTimeMillis();

    handleArguments(args);

    //String fileName = args[0];
    //int nShards = Integer.parseInt(args[1]);
    //String fileType = args[2];

    String fileName = args[0];
    String fileType = args[2];
    int nShards = 0;

try {
    nShards = Integer.parseInt(args[1]);
} catch (NumberFormatException e) {
    System.err.println("Error: Invalid number of shards. Please provide a valid integer.");
    printUsage();
    System.exit(1);
}

if (nShards <= 0) {
    System.err.println("Error: Number of shards must be a positive integer.");
    printUsage();
    System.exit(1);
}

    disableCompression();

    preprocessGraph(fileName, nShards, fileType);

    GraphChiEngine<Integer, Integer> engine = runGraphChi(fileName, nShards, kCoreGC_M);

    BufferedWriter bw = createBufferedWriter(fileName);
    outputCoreValues(fileName, engine, kCoreGC_M, bw);

    printStatistics(engine, kCoreGC_M, startTime);
}

private static void handleArguments(String[] args) {
    if (args.length != 3) {
        printUsage();
        System.exit(1);
    }
}

private static void printUsage() {
    System.err.println("Usage: java -Xmx4g -cp \"bin:lib/*\" -Dnum_threads=4 KCoreGC_M filename nbrOfShards filetype\n" +
            "Example: java -Xmx4g -cp \"bin:lib/*\" -Dnum_threads=4 KCoreGC_M " +
            "./graphchidata/simplegraph.txt 1 edgelist");
}

private static void outputCoreValues(String fileName, GraphChiEngine<Integer, Integer> engine, KCoreGC_M kCoreGC_M, BufferedWriter bw) throws IOException {
    BufferedWriter bufferedWriter = createBufferedWriter(fileName);
    TreeSet<IdInt> topToBottom = getTopList(fileName, engine);

    SortedMap<Integer, Integer> result = prepareResult(topToBottom, engine);

    writeResultToBufferedWriter(result, bufferedWriter);
    closeBufferedWriter(bufferedWriter);
}

private static BufferedWriter createBufferedWriter(String fileName) throws IOException {
    return new BufferedWriter(new FileWriter(fileName + ".cores"));
}

private static TreeSet<IdInt> getTopList(String fileName, GraphChiEngine<Integer, Integer> engine) throws IOException {
    VertexIdTranslate trans = engine.getVertexIdTranslate();
    return Toplist.topListInt(fileName, engine.numVertices(), engine.numVertices());
}

private static SortedMap<Integer, Integer> prepareResult(TreeSet<IdInt> topToBottom, GraphChiEngine<Integer, Integer> engine) {
    SortedMap<Integer, Integer> result = new TreeMap<>();
    VertexIdTranslate trans = engine.getVertexIdTranslate();

    for (IdInt walker : topToBottom) {
        result.put(trans.backward(walker.getVertexId()), (int) walker.getValue());
    }

    return result;
}

private static void writeResultToBufferedWriter(SortedMap<Integer, Integer> result, BufferedWriter bufferedWriter) throws IOException {
    for (Integer v : result.keySet()) {
        bufferedWriter.write(v + ":" + result.get(v) + "\n");
    }
}

private static void closeBufferedWriter(BufferedWriter bufferedWriter) throws IOException {
    bufferedWriter.close();
}

private static void printStatistics(GraphChiEngine<Integer, Integer> engine, KCoreGC_M kCoreGC_M, long startTime) {
    printProcessedVertices(engine);
    printProcessedEdges(engine);
    printIterations(kCoreGC_M);
    printSuccessMessage();
    printElapsedTime(startTime);
}

private static void printProcessedVertices(GraphChiEngine<Integer, Integer> engine) {
    System.out.println("Vertexes Processed: " + engine.numVertices());
}

private static void printProcessedEdges(GraphChiEngine<Integer, Integer> engine) {
    System.out.println("Edges Processed: " + engine.numEdges());
}

private static void printIterations(KCoreGC_M kCoreGC_M) {
    System.out.println("nIterations: " + kCoreGC_M.nIterations);
}

private static void printSuccessMessage() {
    System.out.println("Success!");
}

private static void printElapsedTime(long startTime) {
    long estimatedTime = System.currentTimeMillis() - startTime;
    System.out.println("Time elapsed = " + estimatedTime / 1000.0);
}

private static void disableCompression() {
    CompressedIO.disableCompression();
}

private static void preprocessGraph(String fileName, int nShards, String fileType) throws IOException {
    FastSharder sharder = createSharder(fileName, nShards);
    
    if (fileName.equals("pipein")) {
        sharder.shard(System.in, fileType);
    } else {
        processFile(fileName, nShards, sharder, fileType);
    }
}

private static void processFile(String fileName, int nShards, FastSharder sharder, String fileType) throws IOException {
    String intervalsFileName = ChiFilenames.getFilenameIntervals(fileName, nShards);
    File intervalsFile = new File(intervalsFileName);
    
    if (!intervalsFile.exists()) {
        sharder.shard(new FileInputStream(new File(fileName)), fileType);
    } else {
        logger.info("Found shards -- no need to preprocess");
    }
}

private static GraphChiEngine<Integer, Integer> runGraphChi(String fileName, int nShards, KCoreGC_M kCoreGC_M) throws FileNotFoundException, IOException {
    GraphChiEngine<Integer, Integer> engine = createGraphChiEngine(fileName, nShards);
    setupGraphChiEngine(engine);
    executeGraphChi(engine, kCoreGC_M);

    return engine;
}

private static GraphChiEngine<Integer, Integer> createGraphChiEngine(String fileName, int nShards) throws IOException, FileNotFoundException {
    GraphChiEngine<Integer, Integer> engine = new GraphChiEngine<>(fileName, nShards);
    configureEngine(engine);

    return engine;
}

private static void configureEngine(GraphChiEngine<Integer, Integer> engine) {
    engine.setSkipZeroDegreeVertices(true);
    engine.setEnableScheduler(true);
    engine.setEdataConverter(new IntConverter());
    engine.setVertexDataConverter(new IntConverter());
}

private static void setupGraphChiEngine(GraphChiEngine<Integer, Integer> engine) {
    // Any additional setup if needed
}

private static void executeGraphChi(GraphChiEngine<Integer, Integer> engine, KCoreGC_M kCoreGC_M) throws IOException {
    engine.run(kCoreGC_M, INFINITY);
    logger.info("Ready.");
}

}