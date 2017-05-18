package parsing;

import datastructure.Node;
import datastructure.NodeGraph;
import datastructure.SegmentDB;

import java.util.ArrayDeque;
import java.util.HashSet;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Queue;


/**
 * Created by 101010.
 */
public class Parser {
    /**
     * Initial Parser.
     */
    private static Parser instance = null;

    /**
     * Constructor of the parser.
     */
    protected Parser() { }

    /**
     * Getter for the Singleton parser.
     * @return The singleton parser.
     */
    public static Parser getInstance() {
        if (instance == null) {
            instance = new Parser();
        }
        return instance;
    }

    /**
     * Parses the data of the inputted file.
     * @param file The name of the file.
     * @return The graph created from the .gfa file.
     */
    public NodeGraph parse(final File file) {
        NodeGraph graph = new NodeGraph();
        return parse(file, graph);
    }

    /**
     * Parses a .gfa file to a graph.
     * @param file The name of the target .gfa file.
     * @param graph The graph the data gets put into.
     * @return The graph created from the .gfa file.
     */
    public NodeGraph parse(final File file, NodeGraph graph) {

        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(file));
            String line = in.readLine();
            boolean newCache = true;
            line = line.substring(line.indexOf("\t") + 1);
            line = line.replaceAll(":", "");
            String cacheName = file.getName().substring(0, file.getName().length() - 4);
            String absoluteFilePath = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4);

            graph.setSegmentDB(new SegmentDB(absoluteFilePath + "Segments.txt"));
            File segments = new File(absoluteFilePath + "Segments.txt");

            System.out.println(absoluteFilePath + "Segments.txt");

            if (!segments.exists()) {
                segments.createNewFile();
            }

            BufferedWriter out = new BufferedWriter(new FileWriter(segments, true));
            File newFile = new File(absoluteFilePath + ".txt");
            if (newFile.exists()) {
                newCache = false;
                BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(newFile)));
                line = in2.readLine();
                int graphSize = Integer.parseInt(line);
                for (int i = 0; i < graphSize; i++) {
                    line = in2.readLine();
                    int length = Integer.parseInt(line);
                    line = in2.readLine();
                    double x = Double.parseDouble(line);
                    line = in2.readLine();
                    double y = Double.parseDouble(line);
//                    int inLength = Integer.parseInt(line);
//                    int[] ingoing = new int[inLength];
//                    for (int j = 0; j < inLength; j++) {
//                        line = in2.readLine();
//                        ingoing[j] = Integer.parseInt(line);
//                    }
                    line = in2.readLine();
                    int outLength = Integer.parseInt(line);
                    int[] outgoing = new int[outLength];
                    for (int j = 0; j < outLength; j++) {
                        line = in2.readLine();
                        outgoing[j] = Integer.parseInt(line);
                    }
                    Node temp = new Node(length, outgoing, new int[0]);
                    temp.setX(x);
                    temp.setY(y);
                    graph.addNodeCache(i, temp);
                }
            }

            if (newCache) {
                while (line != null) {
                    if (line.startsWith("S")) {
                        int id;
                        String segment;
                        line = line.substring(line.indexOf("\t") + 1);
                        id = Integer.parseInt(line.substring(0, line.indexOf("\t"))) - 1;
                        line = line.substring(line.indexOf("\t") + 1);
                        segment = line.substring(0, line.indexOf("\t"));
                        graph.addNode(id, new Node(segment.length(), new int[0], new int[0]));
                        out.write(segment + "\n");
                        out.flush();
                        line = in.readLine();
                        while (line != null && line.startsWith("L")) {
                            int from;
                            int to;
                            line = line.substring(line.indexOf("\t") + 1);
                            from = Integer.parseInt(line.substring(0, line.indexOf("\t"))) - 1;
                            line = line.substring(line.indexOf("+") + 2);
                            to = Integer.parseInt(line.substring(0, line.indexOf("\t"))) - 1;
                            graph.addEdge(from, to);
                            line = in.readLine();
                        }
                    } else {
                        line = in.readLine();
                    }
                }
                for (int i = 0; i < graph.getSize(); i++) {
                    graph.getNode(i).setInDegree(graph.getNode(i).getIncomingEdges().length);
                }
                kahnAlgorithm(graph);
                createCache(absoluteFilePath, graph);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Wrong file Destination");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error while reading file");
            e.printStackTrace();
        }


        return graph;
    }

    private void kahnAlgorithm(NodeGraph graph) {
        Queue<Node> q = new ArrayDeque<>();
        int visited = 0;
        int x = 503;
        int y = 291;
        for (int i = 0; i < graph.getSize(); i++) {
            if (graph.getNode(i).getIncomingEdges().length == 0)
                q.add(graph.getNode(i));
        }
        while (!q.isEmpty()) {
            Node current = q.poll();
            x += 40;
            current.setX(x);
            current.setY(y);
            for (int i : current.getOutgoingEdges()) {
                System.out.println(i);
                graph.getNode(i).setInDegree(graph.getNode(i).getInDegree() - 1);
                if (graph.getNode(i).getInDegree() == 0) {
                    q.add(graph.getNode(i));
                }
            }
            visited++;
        }
    }

    /**
     * Creates cache file.
     * @param filename the name of the file.
     * @param graph the graph to be cached.
     */
    private void createCache(String filename, NodeGraph graph) {
        try {
            File file = new File(filename + ".txt");
            int graphSize = graph.getSize();
            OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            BufferedWriter writer = new BufferedWriter(ow);
            writer.write("" + graphSize);
            writer.newLine();
            int size;
            for (int i = 0; i < graphSize; i++) {
                Node temp = graph.getNode(i);
                int length = temp.getLength();
                writer.write("" + length);
                writer.newLine();
                double x = temp.getX();
                double y = temp.getY();
                writer.write("" + x);
                writer.newLine();
                writer.write("" + y);
                writer.newLine();
//                int[] tempList = temp.getIncomingEdges();
//                size = tempList.length;
//                writer.write("" + size);
//                writer.newLine();
//                for (int j = 0; j < size; j++) {
//                    writer.write("" + tempList[j]);
//                    writer.newLine();
//                }
                int[] tempList = temp.getOutgoingEdges();
                size = tempList.length;
                writer.write("" + size);
                writer.newLine();
                for (int j = 0; j < size; j++) {
                    writer.write("" + tempList[j]);
                    writer.newLine();
                }

            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
