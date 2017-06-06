package parsing;

import datastructure.Node;
import datastructure.NodeGraph;
import javafx.application.Platform;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;

/**
 * Created by 101010.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Platform.class})
public class ParserTest {

    @After
    public void tearDown() {
        String workingDirectory = System.getProperty("user.dir");
        String absoluteFilePath = workingDirectory + File.separator;
        File cache = new File(absoluteFilePath + "/src/main/resources/test2.txt");
        File segments = new File(absoluteFilePath + "/src/main/resources/test2Segments.txt");
        File genomes = new File(absoluteFilePath + "/src/main/resources/test2Genomes.txt");

        if (cache.exists()) {
            cache.delete();
        }
        if (segments.exists()) {
            segments.delete();
        }
        if (genomes.exists()) {
            genomes.delete();
        }
    }


    @Test
    public void getInstance() {
        Object parser = parsing.Parser.getInstance();
        assertTrue(parser instanceof parsing.Parser);
    }

    @Test
    public void parse() {
        PowerMockito.mockStatic(Platform.class);
        PowerMockito.doNothing().when(Platform.class);
        Platform.runLater(any());

        parsing.Parser parser = parsing.Parser.getInstance();
        String workingDirectory = System.getProperty("user.dir");

        String absoluteFilePath = workingDirectory + File.separator;
        NodeGraph data = parser.parse(new File(absoluteFilePath + "/src/main/resources/test2.gfa"));
        try {
            Parser.getThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(2221, data.getSegment(7).length());
        assertTrue(data.getSegment(7).length() != 0);

        NodeGraph data2 = parser.parse(new File(absoluteFilePath + "/src/main/resources/test2.gfa"));
        try {
            Parser.getThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(2221, data.getSegment(7).length());
        assertTrue(data.getSegment(7).length() != 0);
    }

    @Test
    public void createCache() {
        PowerMockito.mockStatic(Platform.class);
        PowerMockito.doNothing().when(Platform.class);
        Platform.runLater(any());

        parsing.Parser parser = parsing.Parser.getInstance();
        String workingDirectory = System.getProperty("user.dir");

        String absoluteFilePath = workingDirectory + File.separator;
        NodeGraph data = parser.parse(new File(absoluteFilePath + "/src/main/resources/test2.gfa"));

        try {
            Parser.getThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(absoluteFilePath + "/src/main/resources/test2.txt"))));
            assertEquals(data.getSize(), Integer.parseInt(br.readLine()));
            int[] in;
            int[] out;
            for(int i = 0; i < data.getSize(); i++) {
                assertEquals(data.getNode(i).getLength(), Integer.parseInt(br.readLine()));
                out = data.getNode(i).getOutgoingEdges();
                in = data.getNode(i).getIncomingEdges();

                assertEquals(out.length, Integer.parseInt(br.readLine()));
                String[] tempLine = br.readLine().split("\t");
                for (int j = 0; j < out.length; j++) {
                    assertEquals(out[j], Integer.parseInt(tempLine[j]));
                }

                assertEquals(in.length, Integer.parseInt(br.readLine()));
                tempLine = br.readLine().split("\t");
                for (int j = 0; j < in.length; j++) {
                    assertEquals(in[j], Integer.parseInt(tempLine[j]));
                }
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void parseCache() {
        PowerMockito.mockStatic(Platform.class);
        PowerMockito.doNothing().when(Platform.class);
        Platform.runLater(any());

        parsing.Parser parser = parsing.Parser.getInstance();
        String workingDirectory = System.getProperty("user.dir");
        String absoluteFilePath = workingDirectory + File.separator;
        File file = new File(absoluteFilePath + "/src/test/resources/testCache.txt");
        File file2 = new File(absoluteFilePath + "/src/test/resources/testCacheGenomes.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("4\n" +
                    "5\n" +
                    "3\n" +
                    "1\t2\t3\n" +
                    "0\n" +
                    "\n" +
                    "6\n" +
                    "1\n" +
                    "3\n" +
                    "1\n" +
                    "0\n" +
                    "7\n" +
                    "0\n" +
                    "\n" +
                    "1\n" +
                    "0\n" +
                    "8\n" +
                    "0\n" +
                    "\n" +
                    "2\n" +
                    "1\t2");
            bw.close();
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(file2));
            bw2.write("3\n" +
                    "1\n" +
                    "1\n" +
                    "3\n" +
                    "2\n");
            bw2.close();
            NodeGraph graph = new NodeGraph();
            Node node1 = new Node();
            Node node2 = new Node();
            Node node3 = new Node();
            Node node4 = new Node();
            node1.setLength(5);
            node2.setLength(6);
            node3.setLength(7);
            node4.setLength(8);
            node1.addOutgoingEdge(1);
            node1.addOutgoingEdge(2);
            node1.addOutgoingEdge(3);
            node2.addOutgoingEdge(3);
            node2.addIncomingEdge(0);
            node3.addIncomingEdge(0);
            node4.addIncomingEdge(0);
            node4.addIncomingEdge(1);
            graph.addNode(0, node1);
            graph.addNode(1, node2);
            graph.addNode(2, node3);
            graph.addNode(3, node4);
            NodeGraph testGraph = new NodeGraph();
            parser.parseCache(testGraph, file);

            try {
                Parser.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            file.delete();
            file2.delete();
            assertEquals(graph.getSize(), testGraph.getSize());
            Node testNode1;
            Node testNode2;
            int[] out1;
            int[] out2;
            for (int i = 0; i < graph.getSize(); i++) {
                testNode1 = graph.getNode(i);
                testNode2 = testGraph.getNode(i);
                out1 = testNode1.getOutgoingEdges();
                out2= testNode2.getOutgoingEdges();
                for (int j = 0; j < out1.length; j++) {
                    assertEquals(out1[j], out2[j]);
                }
            }
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void addGenomes() {
        PowerMockito.mockStatic(Platform.class);
        PowerMockito.doNothing().when(Platform.class);
        Platform.runLater(any());

        Parser parser = Parser.getInstance();
        String workingDirectory = System.getProperty("user.dir");
        String absoluteFilePath = workingDirectory + File.separator;
        try {
            parser.parse(new File(absoluteFilePath + "/src/main/resources/test2.gfa"));
            try {
                Parser.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(absoluteFilePath + "/src/main/resources/test2Genomes.txt"))));
            assertEquals("10\tTKK-01-0015\tTKK-01-0026\tTKK-01-0029\tTKK-01-0058\tTKK-01-0066\tTKK_02_0018\tTKK_02_0068\tTKK_04_0002\tTKK_04_0031\tTKK_REF\t", br.readLine());
            assertEquals("2\tTKK-01-0066\tTKK_REF\t", br.readLine());
            assertEquals("6\tTKK_04_0031\tTKK_02_0018\tTKK-01-0026\tTKK_02_0068\tTKK-01-0066\tTKK_REF\t", br.readLine());
            assertEquals("2\tTKK-01-0066\tTKK_REF\t", br.readLine());
            assertEquals("4\tTKK_02_0068\tTKK_04_0031\tTKK_02_0018\tTKK-01-0026\t", br.readLine());
            assertEquals("6\tTKK_04_0031\tTKK_02_0018\tTKK-01-0026\tTKK_02_0068\tTKK-01-0066\tTKK_REF\t", br.readLine());
            assertEquals("1\tTKK_04_0031\t", br.readLine());
            assertEquals("5\tTKK_02_0068\tTKK-01-0066\tTKK_REF\tTKK_02_0018\tTKK-01-0026\t", br.readLine());
            assertEquals("6\tTKK_04_0031\tTKK_02_0018\tTKK-01-0026\tTKK_02_0068\tTKK-01-0066\tTKK_REF\t", br.readLine());
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
