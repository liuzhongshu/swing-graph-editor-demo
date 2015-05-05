package com.zhongshu.sged;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.io.GraphMLReader;
import edu.uci.ics.jung.io.GraphMLWriter;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.PersistentLayout;
import edu.uci.ics.jung.visualization.layout.PersistentLayoutImpl;


public class JungPanel extends JPanel{


	private static final long serialVersionUID = -2023243689258876709L;

	GraphMLReader<Graph<Number,Number>, Number, Number> gmlr;
	GraphMLWriter<Number, Number> gmlw;
    Graph<Number,Number> graph;
    
    PersistentLayout<Number,Number> layout;

    VisualizationViewer<Number,Number> vv;
    
    final EditingModalGraphMouse<Number,Number> graphMouse;
    

    public JungPanel() {
        Factory<Number> vertexFactory = new VertexFactory();
        Factory<Number> edgeFactory = new EdgeFactory();
        try {
			gmlr = new GraphMLReader<Graph<Number,Number>, Number, Number>(vertexFactory, edgeFactory);
			gmlw = new GraphMLWriter<Number, Number>();
		
		} catch (Exception e) {
		}
        
    	setLayout(new BorderLayout(0, 0));
    	
    	graph = new SparseMultigraph<Number,Number>();

        //layout = new StaticLayout<Number,Number>(graph,new Dimension(600,600));
        layout = new PersistentLayoutImpl<Number,Number>(new FRLayout<Number,Number>(graph));
        vv =  new VisualizationViewer<Number,Number>(layout);
        vv.setBackground(Color.white);

        vv.getRenderContext().setVertexLabelTransformer(MapTransformer.<Number,String>getInstance(
        		LazyMap.<Number,String>decorate(new HashMap<Number,String>(), new ToStringLabeller<Number>())));
        
        vv.getRenderContext().setEdgeLabelTransformer(MapTransformer.<Number,String>getInstance(
        		LazyMap.<Number,String>decorate(new HashMap<Number,String>(), new ToStringLabeller<Number>())));

        vv.setVertexToolTipTransformer(vv.getRenderContext().getVertexLabelTransformer());
        

        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        this.add(panel);

        
        graphMouse = new EditingModalGraphMouse<Number,Number>(vv.getRenderContext(), vertexFactory, edgeFactory);
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

        graphMouse.setMode(ModalGraphMouse.Mode.EDITING);

    }
    
    public void newGraph() {
        graph = new SparseMultigraph<Number,Number>();
        layout.setGraph(graph);
        vv.repaint();
    }
    
    public void writeJPEGImage(File file) {
        int width = vv.getWidth();
        int height = vv.getHeight();

        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();
        vv.paint(graphics);
        graphics.dispose();
        
        try {
            ImageIO.write(bi, "jpeg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setEditMode() {
    	graphMouse.setMode(ModalGraphMouse.Mode.EDITING);
    }
    
    public void setPickMode() {
    	graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
    }
    
    public void setTransformMode() {
    	graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
    }
    
    public void setAnnotatingMode() {
    	graphMouse.setMode(ModalGraphMouse.Mode.ANNOTATING);
    }
    
    public void loadGraph(String filename) {
    	try {
    		Graph<Number,Number> newGraph = new SparseMultigraph<Number,Number>();
			gmlr.load(filename, newGraph);
			graph = newGraph;
			layout.setGraph(graph);
    		//layout.restore(filename);			
    		vv.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void saveGraph(String saveFile) {
    	try {
    		//layout.persist(saveFile);
			gmlw.save(graph, new FileWriter(saveFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    class VertexFactory implements Factory<Number> {

    	int i=0;

		public Number create() {
			return i++;
		}
    }
    
    class EdgeFactory implements Factory<Number> {

    	int i=0;
    	
		public Number create() {
			return i++;
		}
    }

}