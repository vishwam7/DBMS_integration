import java.io.File;
import java.io.PrintStream;
import it.unimi.dsi.webgraph.ImmutableGraph;
import java.util.Arrays;
import java.util.stream.IntStream;

public class KCoreWG_BZ {
    ImmutableGraph G;
    boolean printprogress = false;
    long E;
    int n;
    int md; // max degree

    // Initialization: Load the graph, compute basic graph properties
    public KCoreWG_BZ(String basename) throws Exception {
        initializeGraph(basename);
        G = ImmutableGraph.load(basename);
        n = G.numNodes();
        E = 0;
        md = 0;
        computeGraphProperties();
    }

    // Load the graph using Webgraph library
    private void initializeGraph(String basename) throws Exception {
        G = ImmutableGraph.load(basename);
        n = G.numNodes();
    }

    // Compute basic graph properties like maximum degree and total edges
    private void computeGraphProperties() {

        // Use stream and max to find the maximum degree
        md = IntStream.range(0, n).map(G::outdegree).max().orElse(0);

        // Use stream and sum to calculate the total outdegree
        E = IntStream.range(0, n).map(G::outdegree).sum();
    }

    // Core computation: Compute k-core decomposition
    public int[] KCoreCompute() {
        int[] vert = initializeIntArray(n);
        int[] pos = initializeIntArray(n);
        int[] deg = initializeIntArray(n);
        int[] bin = initializeIntArray(md + 1);

        // Initialize bins for degree distribution
        initializeDegreeBins(deg, bin);

        // Sort vertices by degree using bin-sort
        sortVerticesByDegree(vert, pos, deg, bin);

        // Main k-core decomposition algorithm
        long pctDoneLastPrinted = 0;

        IntStream.range(0, n).forEachOrdered(i -> {
            int v = vert[i]; // smallest degree vertex

            // Rest of the loop content remains unchanged
            if (deg[v] > 0) {
                updateVerticesAndBins(v, deg, bin, vert, pos);
            }

            long pctDone = Math.round((100.0 * (i + 1)) / n);
            printProgress(pctDone, pctDoneLastPrinted);
        });

        return deg;
    }

    // Helper method to initialize bins for degree distribution
    private void initializeDegreeBins(int[] deg, int[] bin) {
        for (int v = 0; v < n; v++) {
            int vertexDegree = G.outdegree(v);
            if (vertexDegree <= md) {
                bin[vertexDegree]++;
                deg[v] = vertexDegree;
            } else {
                // Handle unexpected degree values (print, log, or handle accordingly)
                System.err.println("Unexpected degree value: " + vertexDegree);
            }
        }
    }

    // Helper method to initialize an integer array
    private int[] initializeIntArray(int size) {
        return new int[size];
    }

    // Helper method to sort vertices by degree using bin-sort
    private void sortVerticesByDegree(int[] vert, int[] pos, int[] deg, int[] bin) {
        // Determine the correct size for the count array
        int maxDegree = Arrays.stream(deg).max().orElse(0);
        int[] count = new int[maxDegree + 1];

        // Use stream and forEach to update count array
        IntStream.range(0, n).forEach(v -> {
            int degree = deg[v];
            if (degree <= maxDegree) {
                pos[v] = count[degree];
                vert[pos[v]] = v;
                count[degree]++;
            } else {
                System.err.println("Degree out of bounds: " + degree);
            }
        });

    }

    // Helper method to update vertices and bins during k-core computation
    private void updateVerticesAndBins(int v, int[] deg, int[] bin, int[] vert, int[] pos) {
        int v_deg = G.outdegree(v);
        int[] N_v = G.successorArray(v);

        // Use IntStream.range to iterate over the range of v_deg
        IntStream.range(0, v_deg).forEach(j -> {
            int u = N_v[j];

            if (deg[u] > deg[v]) {
                swapVerticesAndBins(u, v, deg, bin, vert, pos);
                bin[deg[u]]++;
                deg[u]--;
            }
        });
    }

    // Helper method to swap vertices and bins during k-core computation
    private void swapVerticesAndBins(int u, int v, int[] deg, int[] bin, int[] vert, int[] pos) {
        int du = deg[u];
        int pu = pos[u];
        int pw = bin[du];

        // Ensure that pw is within the bounds of vert array
        if (pw >= vert.length) {
            // Handle the out-of-bounds case (print, log, or handle accordingly)
            System.err.println("Out of bounds: pw=" + pw + ", vert.length=" + vert.length);
            return;
        }

        int w = vert[pw];

        // Ensure that pu is within the bounds of vert array
        if (pu >= vert.length) {
            // Handle the out-of-bounds case (print, log, or handle accordingly)
            System.err.println("Out of bounds: pu=" + pu + ", vert.length=" + vert.length);
            return;
        }

        if (u != w) {
            // Inline the swap logic
            int temp1 = pos[u];
            pos[u] = pos[w];
            pos[w] = temp1;

            int temp2 = vert[pu];
            vert[pu] = vert[pw];
            vert[pw] = temp2;
        }
    }

    // Helper method to print progress during k-core computation
    private void printProgress(long pctDone, long pctDoneLastPrinted) {
        if (pctDone >= pctDoneLastPrinted + 10 || pctDone == 100) {
            String progressBar = createProgressBar(pctDone);
            System.out.println(String.format("Progress: %3d%% %s", pctDone, progressBar));
            pctDoneLastPrinted = pctDone;
        }
    }

    private String createProgressBar(long pctDone) {
        int progressBarLength = 20;
        int progressChars = (int) Math.round((progressBarLength * pctDone) / 100.0);
        int remainingChars = progressBarLength - progressChars;

        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < progressChars; i++) {
            progressBar.append("=");
        }
        for (int i = 0; i < remainingChars; i++) {
            progressBar.append(" ");
        }
        progressBar.append("]");

        return progressBar.toString();
    }

    // Main method
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        try {
            if (args.length != 1)
                throw new IllegalArgumentException("Usage: java KCoreWG_BZ basename");

            String basename = args[0];
            System.out.println("Starting " + basename);

            KCoreWG_BZ kc = new KCoreWG_BZ(basename);

            // Storing the core value for each node in a file.
            try (PrintStream ps = new PrintStream(new File(basename + ".cores"))) {
                int[] res = kc.KCoreCompute();

                int kmax = -1;
                double sum = 0;
                int cnt = 0;

                // Loop using enhanced for loop
                for (int i : res) {
                    printCoreInfo(ps, cnt, i);

                    if (i > kmax)
                        kmax = i;
                    sum += i;
                    cnt += i > 0 ? 1 : 0;
                }

                printFinalOutput(cnt, kc.E, kc.md, kmax, sum, basename, startTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to print core information
    private static void printCoreInfo(PrintStream ps, int vertex, int core) {
        ps.println(vertex + ":" + core);
    }

    // Helper method to print final output
    private static void printFinalOutput(int cnt, long edgeCount, int maxDegree, int kmax, double avgCore,
            String basename,
            long startTime) {
        System.out.println("|V|\t|E|\tdmax\tkmax\tkavg");
        System.out.println(cnt + "\t" + (edgeCount / 2) + "\t" + maxDegree + "\t" + kmax + "\t" + avgCore / cnt);

        System.out.println(basename + ": Time elapsed (sec) = " + (System.currentTimeMillis() - startTime) / 1000.0);
    }

}