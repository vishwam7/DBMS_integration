# K-Core Decomposition of Large Networks on a Single PC

## Description

#### The primary challenge addressed in this research is to devise efficient methodologies for computing the k-core decomposition of massive networks while operating within the constraints of a single consumer-grade PC. The k-core decomposition serves as a pivotal graph mining technique, integral for identifying cohesive substructures or core components within complex networks.

## Algorithms

### 1. Batagelj-Zaversnik Algorithm on Webgraph (WG BZ):  
#### The Batagelj-Zaversnik algorithm, implemented on the Webgraph framework, emerges as the most efficient solution among those evaluated in this research. It demonstrates impressive running times, particularly on datasets like UK and TW, completing computations within minutes. However, it necessitates that both graph vertices and edges fit entirely within the available memory, which restricts its applicability for extremely large datasets like TW under various memory budgets.  

### 2. Montresor et. al. Algorithm on Webgraph (WG M):  
#### This algorithm, also implemented on the Webgraph framework, stands as the second fastest approach in this study. It exhibits commendable performance, completing computations within minutes for datasets like UK and within a few tens of minutes for TW. Unlike WG BZ, WG M solely necessitates the graph's vertices to fit within memory, which presents a more feasible constraint even for substantial datasets like TW. WG M succeeded in running for all memory budgets considered in the study.

### 3. Montresor et. al. Algorithm on GraphChi (GC M):  
#### Implemented on the GraphChi framework, this algorithm operates relatively slower compared to the preceding methods. Despite being slower, it stands out for not mandating that vertices or edges fit entirely in memory. GC M trades off speed for the advantage of yielding satisfactory approximate results with fewer iterations, making it a viable alternative when computational resources are limited.  

## Implementation: 
### Dataset link:  
#### [Stanford Dataset](http://snap.stanford.edu/data/index.html)
#### [twitter and uk dataset](https://law.di.unimi.it/) 

### Input to Algorithm:
To run the algorithm successfully, ensure you have the following input files in your project directory: 

#### 1). Graph File (.graph):

The graph file contains the structural information of the graph in a specific format. Ensure the file extension is .graph.

#### 2). Properties File (.properties):

The properties file contains additional attributes or metadata associated with the graph. Make sure the file extension is .properties.

#### 3). Offsets File (.offsets):

The offsets file provides offset information and is crucial for certain operations. If the offsets file is missing, you can generate it using the following command:

`java -cp "lib/*" it.unimi.dsi.webgraph.BVGraph -o -O -L cnr-2000`

The given command utilizes the `BVGraph` utility from the `it.unimi.dsi.webgraph` package to perform operations on a graph dataset named `cnr-2000`. Specifically, it is generating an offset file (`-o`), incorporating optimization strategies (`-O`) during the process. The `-cp "lib/*"` ensures that the necessary Java classpath includes all required libraries (`*.jar` files) within the `lib/` directory, providing essential dependencies for the operation.

#### Parameters Explanation:

- `java`: The command to execute a Java application.

- `-cp "lib/*"`: Specifies the classpath for Java to include all required libraries (*.jar files) within the lib/ directory. The * wildcard implies all JAR files within that directory will be included.

- `it.unimi.dsi.webgraph.BVGraph`: This is the main class or entry point for the application. BVGraph is a class within the it.unimi.dsi.webgraph package responsible for various graph-related operations.

- `-o`: Represents the -o flag, used to indicate the operation of building an offset file.

- `-O`: Indicates optimizing the resulting data structure. This flag is often used to specify additional optimizations during file creation or processing.

- `-L cnr-2000`: Specifies the name of the graph or dataset (cnr-2000) for which the operation is being performed. This might represent the name of the graph or a specific dataset identifier.

### Input Data Preparation for K-Core Decomposition:

#### Graph Representation and Processing 

The k-core decomposition process requires precise graph representation and preprocessing steps to yield accurate results. The input files and their respective processing play a vital role in achieving the desired decomposition efficiently.

**1). Graph Files:**

**basename.graph**: This file encapsulates the structural information of the graph, representing its nodes and edges.

**basename.properties**: Holds additional metadata or attributes associated with the graph.

**basename.offsets**: Contains essential offset details crucial for specific operations within the algorithm.

These files collectively define the graph's structure, attributes, and essential offsets, forming the backbone for the k-core decomposition process.

**2). Edgelist Preparation:**

**edgelistfile**: Represents the initial, unsorted list of graph edges.

**edgelistsortedfile**: Resulting file after sorting and obtaining unique edges from the edgelistfile.

The sorting and uniqueness extraction ensure a well-organized dataset, a prerequisite for subsequent input generation.

#### Processing Steps for Input Generation

**1). Sorting Edgelist:**

**Command**: `sort -nk 1 edgelistfile | uniq > edgelistsortedfile`

**Purpose**: Sorting and extracting unique edges create a refined dataset for further processing.

**sort Command Flags:**

`-n`: Indicates a numerical sort, treating the first field of each line as a numeric value.

`-k 1`: Specifies the sort key to be the first field. In this case, the sorting is based on the content found in the first column or field of each line.

**uniq Command Flags:**

No specific flags used in the command: However, the uniq command, when used without flags, typically ensures that only adjacent identical lines are displayed once. It removes duplicate adjacent lines and outputs only unique lines.

**Combined Explanation:**

`sort -nk 1 edgelistfile`: Sorts the contents of the edgelistfile numerically based on the first column or field of each line.

`|`: Represents a pipe and is used to redirect the output of the sort command to the uniq command as input.

`uniq`: Filters the sorted output to display only unique lines, ensuring that repeated adjacent lines are removed.

`> edgelistsortedfile`: Redirects the final output (sorted and unique) to a file named edgelistsortedfile.

**2). Generating Graph Input Files:**

**Command**: `java -cp "lib/*" it.unimi.dsi.webgraph.BVGraph -1 -g ArcListASCIIGraph dummy basename < edgelistsortedfile`

**Objective**: Converts the sorted edgelist into the required input files (basename.graph, basename.properties, basename.offsets) for subsequent algorithmic execution.

**Command Explanation:**

`java`: Initiates the execution of a Java application.

`-cp "lib/*"`: Specifies the classpath to include all necessary libraries (*.jar files) within the lib/ directory.

`it.unimi.dsi.webgraph.BVGraph`: Represents the main class or entry point for the application. In this case, it's referring to the BVGraph utility from the it.unimi.dsi.webgraph package, responsible for various graph-related operations.

`-1`: Indicates a specific operation or flag for the BVGraph utility. However, without the context of the BVGraph utility, the precise function of this flag can't be determined.

`-g ArcListASCIIGraph`: Represents another operation or flag specific to the BVGraph utility. This flag likely denotes a format specification for the graph representation, potentially indicating an Arc List ASCII format for the graph data.

`dummy basename`: These are placeholders or arguments passed to the BVGraph utility. The meaning and purpose of these arguments depend on the utility's functionality and requirements.

`< edgelistsortedfile`: Redirects the contents of the edgelistsortedfile as input for the command, providing data or parameters required by the BVGraph utility for its operations. 

### offset commands:

**KCoreWG_BZ** and **KCoreWG_M**:

**For files**: `basename.graph` and `basename.properties`

**Command**:`java -cp "lib/*" it.unimi.dsi.webgraph.BVGraph -o -O –L basename`

**Command Explanation:**

`-cp "lib/*"`: Specifies the classpath for Java to include all necessary libraries (*.jar files) within the lib/ directory.

`it.unimi.dsi.webgraph.BVGraph`: Refers to the main class or entry point for the Java application. In this case, it's pointing to the BVGraph utility from the it.unimi.dsi.webgraph package, responsible for various graph-related operations.

`-o`: Indicates an operation flag, typically used to denote the creation or generation of an offset file. This flag might specify that the command is intended to build an offset file.

`-O`: Represents another operation or flag specific to the BVGraph utility. This flag might signify additional optimization strategies or settings during file creation or processing. However, the exact meaning might require specific context from the utility's documentation or usage.

`-L basename`: This flag might be used to specify the name of the graph or dataset (basename) for which the operation is being performed. It likely denotes the identifier or label for the dataset being processed by the command.

**For example for files:**

`cnr-2000.graph`  
`cnr-2000.properties`  
`cnr-2000-t.graph`
`cnr-2000-t.properties`

**Command:** `java -cp "lib/*" it.unimi.dsi.webgraph.BVGraph -o -O -L cnr-2000`

### Java Library Dependencies and Compilation

#### Major .jar File Dependencies

The algorithms rely on several .jar file dependencies to function efficiently:

**1). WebGraph Library (WebGraph.jar):**

**Purpose**: Facilitates graph compression techniques and operations used extensively in KCoreWG_BZ and KCoreWG_M.

**Reason for Dependency**: Enables efficient loading and manipulation of graphs in memory and memory-mapped files.

**2. GraphChi Library (GraphChi.jar):**

**Purpose**: Essential for KCoreGC_M algorithm, aiding large-scale graph computations.

**Reason for Dependency**: Provides necessary functionalities to handle graph computations efficiently, particularly suited for large-scale graphs.


#### Compilation Details:

The compilation process involves specific files:

**1). Refactored Files:**

Located in the `refactored/` directory.

Contains the algorithmic implementations (**KCoreWG_BZ.java**, **KCoreWG_M.java**, **KCoreGC_M.java**).

**2). Compilation Command:**

**Command**: `javac -cp "lib/*" -d bin refactored/*`

**Command Explanation:**

`-cp "lib/*"`: Specifies the classpath to include all .jar file dependencies.

`-d bin`: Directs the compiled output to the bin/ directory.

`refactored/*`: Indicates compilation of all Java source files in the refactored/ directory.

### Algorithms command to run:

**KCoreWG_BZ:**

**Command**: `java -cp "bin:lib/*" KCoreWG_BZ basename`

**Command Explanation:**

`java`: Initiates the execution of a Java application.

`-cp "bin:lib/*"`: Specifies the classpath for Java to include the compiled bytecode (bin/) and necessary libraries (*.jar files) within the lib/ directory.

`KCoreWG_BZ`: Refers to the main class or entry point for the Java application. In this case, it's the KCoreWG_BZ class, suggesting a specific functionality or operation within the program.

`basename`: This is an argument being passed to the KCoreWG_BZ program. It could be a parameter or input data needed for the program's execution, likely representing the identifier or name of a dataset or file.

e.g. `java -cp "bin:lib/*" KCoreWG_BZ simplegraph`

**KCoreWG_M:**

**Command**: `java -cp "bin:lib/*" KCoreWG_M basename`

**Command Explanation:**

`java`: Launches the Java Virtual Machine (JVM) to execute a Java program.

`-cp "bin:lib/*"`: Sets the classpath for Java, indicating where to find compiled bytecode (bin/) and necessary external libraries (lib/ directory containing *.jar files).

`KCoreWG_M`: Refers to the main class or entry point for the Java application. In this case, it's KCoreWG_M, likely representing a specific functionality or operation within the program.

`basename`: This is an argument being passed to the KCoreWG_M program. It could represent a parameter or input data needed for the program's execution, typically indicating the name or identifier of a dataset or file.

e.g. `java -cp "bin:lib/*" KCoreWG_M simplegraph`

**KCoreGC_M:**

**Command**: `java -Xmx4g -cp "bin:lib/*" -Dnum_threads=4 KCoreGC_M filename nbrOfShards filetype`

**Command Explanation:**

`java`: Initiates the Java Virtual Machine (JVM) to execute a Java program.

`-Xmx4g`: Sets the maximum heap size for the Java process to 4 gigabytes (4g). This parameter defines the maximum amount of memory that Java can use.

`-cp "bin:lib/*"`: Specifies the classpath for Java, indicating where to find compiled bytecode (bin/) and external libraries (lib/ directory containing *.jar files).

`-Dnum_threads=4`: Sets a system property named num_threads with a value of 4. This property might be used by the Java program for configuring the number of threads or controlling concurrent operations.

`KCoreGC_M`: Refers to the main class or entry point for the Java application. It indicates the specific functionality or operation within the program.

`filename nbrOfShards filetype`: These are command-line arguments being passed to the KCoreGC_M program. They likely represent parameters or input data required for the program's execution. The exact interpretation of these arguments would depend on the program's implementation.

e.g. `java -Xmx4g -cp "bin:lib/*" -Dnum_threads=4 KCoreGC_M ./graphchidata/simplegraph.txt 1 edgelist` 

## Results and Analysis:

### Results:

**Ours:**

| Graph                | Algorithm    | V         | E           | dmax      | kmax      | kavg              | Time (sec)  |
|----------------------|--------------|-----------|-------------|-----------|-----------|-------------------|-------------|
| **CA-AstroPh**       | KCoreWG_BZ   | 18772     | 198080      | 504       | 478       | 20.5              | 0.809       |
| **CA-AstroPh**       | KCoreWG_M    | 18772     | 99040       | 504       | 56        | 12.618528         | 0.374       |
| **CA-AstroPh**       | KCoreGC_M    | 133281    | 396100      | 533124    | 6         | N/A               | 0.599       |
| **ca-condmat**       | KCoreWG_BZ   | 23133     | 93468       | 280       | 247       | 7.5451519         | 0.714       |
| **ca-condmat**       | KCoreWG_M    | 23133     | 46734       | 280       | 25        | 4.907881          | 0.309       |
| **ca-condmat**       | KCoreGC_M    | 108301    | 186878      | 433204    | 6         | N/A               | 0.529       |
| **p2p-gnutella31**   | KCoreWG_BZ   | 16387     | 73946       | 78        | 68        | 8.166473          | 0.536       |
| **p2p-gnutella31**   | KCoreWG_M    | 16387     | 36973       | 78        | 21        | 3.264661          | 0.267       |
| **p2p-gnutella31**   | KCoreGC_M    | 62587     | 147892      | 250348    | 5         | N/A               | 0.375       |
| **soc-Slashdot0902** | KCoreWG_BZ   | 78441     | 474232      | 2511      | 2485      | 11.5903           | 0.729       |
| **soc-Slashdot0902** | KCoreWG_M    | 78441     | 237116      | 2511      | 62        | 6.293635          | 0.567       |
| **soc-Slashdot0902** | KCoreGC_M    | 82169     | 870161      | 328676    | 3         | N/A               | 0.512       |
| **Amazon0601**       | KCoreWG_BZ   | 402439    | 1693694     | 10        | 10        | 7.665164          | 2.108       |
| **Amazon0601**       | KCoreWG_M    | 402439    | 846847      | 10        | 10        | 6.547084          | 1.64        |
| **Amazon0601**       | KCoreGC_M    | 403395    | 3387388     | 1613580   | 2         | N/A               | 1.994       |
| **web-BerkStan**     | KCoreWG_BZ   | 680486    | 3800297     | 249       | 249       | 11.0515           | 2.955       |
| **web-BerkStan**     | KCoreWG_M    | 680486    | 1900148     | 249       | 249       | 6.759513          | 2.127       |
| **web-BerkStan**     | KCoreGC_M    | 685232    | 7600595     | 2740928   | 3         | N/A               | 1.378       |
| **roadNet-TX**       | KCoreWG_BZ   | 1379917   | 1921660     | 12        | 12        | 2.20696           | 5.619       |
| **roadNet-TX**       | KCoreWG_M    | 1379917   | 960830      | 12        | 6         | 1.831505          | 1.576       |
| **roadNet-TX**       | KCoreGC_M    | 1393384   | 3843320     | 5573536   | 22        | N/A               | 3.18        |
| **roadNet-CA**       | KCoreWG_BZ   | 1965206   | 2766607     | 12        | 12        | 2.26222           | 7.852       |
| **roadNet-CA**       | KCoreWG_M    | 1965206   | 1383303     | 12        | 6         | 1.851169          | 2.051       |
| **roadNet-CA**       | KCoreGC_M    | 1971282   | 5533214     | 7885128   | 23        | N/A               | 5.093       |
| **wiki-Talk**        | KCoreWG_BZ   | 147602    | 2510705     | 100022    | 100017    | 33.426            | 9.298       |
| **wiki-Talk**        | KCoreWG_M    | 147602    | 1255352     | 100022    | 456       | 4.84174           | 5.754       |
| **wiki-Talk**        | KCoreGC_M    | 2394386   | 5021410     | 9577544   | 4         | N/A               | 2.875       |
| **LiveJournal**      | KCoreWG_BZ   | 4308452   | 34496886    | 20293     | 20281     | 15.5602           | 19.869      |
| **LiveJournal**      | KCoreWG_M    | 4308452   | 17248443    | 20293     | 254       | 8.19374           | 35.799      |
| **LiveJournal**      | KCoreGC_M    | 4847572   | 68475391    | 19390288  | 531       | N/A               | 597.624     |
| **Uk-2005**          | KCoreWG_BZ   | 35051939  | 468182141   | 5213      | 5213      | 26.6057           | 771.48      |
| **Uk-2005**          | KCoreWG_M    | 35051939  | 234091070   | 5213      | 5213      | 19.4829           | 257.491     |
| **Twitter-2010**     | KCoreWG_BZ   | 40103281  | 734182591   | 2997469   | 2997469   | 36.08963989754354 | 1905.405    |

**Author:**

| Graph                | Algorithm     | V         | E           | dmax      | kmax      | kavg       | Time (sec)  |
|----------------------|---------------|-----------|-------------|-----------|-----------|------------|-------------|
| **CA-AstroPh**       | KCoreWG_BZ    | 18772     | 0           | 504       | 56        | 12.615598  | 0.452       |
| **CA-AstroPh**       | KCoreWG_M     | 18772     | 0           | 504       | 56        | 12.618528  | 0.634       |
| **CA-AstroPh**       | KCoreGC_M     | 133281    | 396100      | 533124    | 17        | N/A        | 1.002       |
| **ca-condmat**       | KCoreWG_BZ    | 23133     | 0           | 280       | 25        | 4.900618   | 0.419       |
| **ca-condmat**       | KCoreWG_M     | 23133     | 0           | 280       | 25        | 4.907881   | 0.47        |
| **ca-condmat**       | KCoreGC_M     | 108301    | 186878      | 433204    | 15        | N/A        | 0.823       |
| **p2p-gnutella31**   | KCoreWG_BZ    | 16387     | 0           | 78        | 68        | 8.089034   | 0.305       |
| **p2p-gnutella31**   | KCoreWG_M     | 16387     | 0           | 78        | 21        | 3.264661   | 0.387       |
| **p2p-gnutella31**   | KCoreGC_M     | 62587     | 147892      | 250348    | 5         | N/A        | 0.522       |
| **soc-Slashdot0902** | KCoreWG_BZ    | 78441     | 0           | 2511      | 685       | 7.193496   | 0.45        |
| **soc-Slashdot0902** | KCoreWG_M     | 78441     | 0           | 2511      | 62        | 6.293635   | 0.754       |
| **soc-Slashdot0902** | KCoreGC_M     | 82169     | 0           | 2511      | 62        | 6.293635   | 0.754       |
| **Amazon0601**       | KCoreWG_BZ    | 402439    | 0           | 10        | 10        | 6.184999   | 1.135       |
| **Amazon0601**       | KCoreWG_M     | 402439    | 0           | 10        | 10        | 6.547084   | 2.278       |
| **Amazon0601**       | KCoreGC_M     | 403395    | 0           | 10        | 10        | 6.547084   | 1.64        |
| **web-BerkStan**     | KCoreWG_BZ    | 680486    | 0           | 249       | 248       | 10.129773  | 1.823       |
| **web-BerkStan**     | KCoreWG_M     | 680486    | 0           | 249       | 249       | 6.759513   | 3.273       |
| **web-BerkStan**     | KCoreGC_M     | 685232    | 0           | 249       | 249       | 6.759513   | 3.273       |
| **roadNet-TX**       | KCoreWG_BZ    | 1379917   | 0           | 12        | 3         | 1.793598   | 2.824       |
| **roadNet-TX**       | KCoreWG_M     | 1379917   | 0           | 12        | 6         | 1.831505   | 3.679       |
| **roadNet-TX**       | KCoreGC_M     | 1393384   | 0           | 12        | 122       | N/A        | 15.274      |
| **roadNet-CA**       | KCoreWG_BZ    | 1965206   | 0           | 12        | 3         | 1.812313   | 3.735       |
| **roadNet-CA**       | KCoreWG_M     | 1965206   | 0           | 12        | 6         | 1.851169   | 5.071       |
| **roadNet-CA**       | KCoreGC_M     | 1971282   | 0           | 12        | 72        | N/A        | 10.458      |
| **wiki-Talk**        | KCoreWG_BZ    | 147602    | 0           | 100022    | 100012    | 29.77148   | 4.291       |
| **wiki-Talk**        | KCoreWG_M     | 147602    | 0           | 100022    | 456       | 4.841743   | 5.831       |
| **wiki-Talk**        | KCoreGC_M     | 2394386   | 0           | 9577544   | 3         | N/A        | 4.055       |
| **LiveJournal**      | KCoreWG_BZ    | 4308452   | 0           | 20293     | 17698     | 9.239013   | 13.193      |
| **LiveJournal**      | KCoreWG_M     | 4308452   | 0           | 20293     | 254       | 8.193737   | 42.443      |
| **LiveJournal**      | KCoreGC_M     | 4847572   | 0           | 19390288  | 194       | N/A        | 246.972     |
| **Uk-2005**          | KCoreWG_BZ    | 35051939  | 0           | 5213      | 5213      | 19.482907  | 257.491     |
| **Uk-2005**          | KCoreWG_M     | 35051939  | 0           | 5213      | 5213      | 19.482907  | 257.491     |
| **Twitter-2010**     | KCoreWG_BZ    | 40103281  | 0           | 2997469   | 2997286   | 26.808592  | 376.324     |
| **Twitter-2010**     | KCoreWG_M     | 40103281  | 0           | 2997469   | 2997469   | 36.08964   | 1905.405    |

### Analysis:
The evaluation showcases the robustness of GraphChi and Webgraph implementations in handling large-scale graph processing. GraphChi, designed for disk-based processing, efficiently decomposed massive graphs like the Twitter network, boasting 2.4 billion edges, within 9,360 seconds. Its convergence metrics, with a less than 1% update rate within 20 iterations and a maximum error of 1 within 60 iterations, denote rapid convergence across all test graphs.

Webgraph’s optimized BZ algorithm demonstrated remarkable efficiency, compressing graph data to fit within 4GB memory for most datasets. Notably, the Twitter graph, requiring 6GB, showcased a runtime of 330 seconds. The flattened array optimization allowed O(1) lookups despite linear time complexity, proving advantageous for large-scale graphs. The vertex-centric algorithm in Webgraph, leveraging memory-mapped file access and strategic caching, achieved impressive runtimes, completing the Twitter graph within minutes and the UK web graph in mere seconds, marking up to 10x faster performance compared to other algorithms like GraphChi and EMcore. These findings affirm GraphChi and Webgraph's prowess in efficiently handling big data graphs on consumer-grade hardware.

## Conclusion:

The investigation aimed to optimize k-core decomposition methodologies within resource-constrained environments of single consumer-grade PCs. The study's technical contributions and algorithm evaluations yielded significant insights:

### 1). GraphChi:

| Feature                      | Algorithm 1 & 2              | KCoreGC_M.java                            |
|------------------------------|------------------------------|-------------------------------------------|
| **Execution platform**       | Distributed cluster          | Single multicore commodity machine        |
| **Processing methodology**   | In-memory                    | External memory                           |
| **Time complexity**          | Linearithmic overall         | Linearithmic overall                      |
| **Scheduling approach**      | Central scheduler            | Distributed scheduler                     |
| **Parallelization strategy** | Vertex-based partitioning    | Edge-based sharding using PSW             |
| **Load balancing**           | Equal vertex distribution    | Sharding by edge count distribution       |
| **Fault tolerance**          | Inherently supports failures | No explicit fault tolerance               |
| **Programming effort**       | Moderate                     | Low due to GraphChi APIs                  |
| **Memory footprint**         | Depends on cluster resources | Controlled by sharding parameters         |
| **Tuning difficulty**        | Moderate                     | Easy from command line configs            |

In summary, the key technical differences are:

- KCoreGC_M.java allows the algorithm to work on a single commodity PC rather than a distributed cluster through disk-based processing.

- It uses GraphChi's edge sharding and Parallel Sliding Windows mechanism to efficiently process large graphs from disk.

- Scheduling and parallelization mechanisms differ although complexity remains equivalent.

- Fault tolerance handled transparently in distributed platform but needs explicit handling in GraphChi.

- GraphChi provides easy programming abstractions and execution configurability.

### 2). KCoreWG_BZ:

| Feature              | Algorithm 3 (set array D)                      | Algorithm 4 (flat array D)                                   | KCoreWG_BZ algorithm                                                           |
|----------------------|------------------------------------------------|--------------------------------------------------------------|--------------------------------------------------------------------------------|
| **Data structures**  | Arrays L, d, D (set array)                     | Arrays L, d, b, p, D (flat array)                            | Arrays vert, pos, deg, bin                                                     |  
| **Initialization**   | Initialize L, d, D                             | Initialize d, b, p, D                                        | Initialize vert, pos, deg, bin                                                 |
| **Sorting vertices** | Not specified                                  | Sort vertices by degree using bin sort, with D tagging along | Sort vertices by degree using bin sort, with vert and pos tagging along        |
| **Main algorithm**   | Delete min degree vertex <br> Update neighbors | Scan vertices <br> Update vertices and bins                  | Stream over vertices <br> Update vertices and bins                             |
| **Time complexity**  | Not analyzed                                   | O(m)                                                         | O(m)                                                                           |

The main differences are:

- KCoreWG_BZ does not maintain the coreness array L, it only computes the degree array deg which contains the coreness at the end

- It uses arrays vert and pos for efficiently swapping vertices during binning instead of having D tag along in the sorting

- It streams over the vertices instead of deleting the min degree vertex explicitly

- It updates the vertices and bins in each iteration instead of having a separate update phase

- It uses custom swap and update logic instead of abstract operations on D

### 3). KCoreWG_M:

| Aspect                         | KCoreWG_M Implementation                                                                 | Paper Algorithms                                                         |
|--------------------------------|------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| **Vertex scheduling**          | Uses isScheduled array                                                                   | Uses scheduled array                                                     |
| **Subsequent iterations**      | Computes local estimate using computeEstimatedCore() and updates core number if smaller  | Algorithm 6 updates core number using computeUpperBound()                |
| **Local estimate computation** | Uses core frequency array, handles differently than paper                                | Algorithm 7 uses c array directly based on neighbors' core numbers       |
| **Convergence check**          | Checks if hasChanged flag is set each iteration                                          | Checks global change flag                                                |

In summary, the main differences are:

- The KCoreWG_M implementation uses a separate isScheduled array rather than reusing the scheduled array.

- It has a different implementation for computing the local core number estimate, using a core frequency array.

- It checks a hasChanged flag rather than a global change flag for convergence.
