package com.zhongshu.sged;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;


public class JungPanel extends JPanel{


	private static final long serialVersionUID = -2023243689258876709L;


    Graph<Number,Number> graph;
    
    AbstractLayout<Number,Number> layout;

    VisualizationViewer<Number,Number> vv;
    
    final EditingModalGraphMouse<Number,Number> graphMouse;
    

    public JungPanel() {
    	setLayout(new BorderLayout(0, 0));
    	
        // create a simple graph for the demo
        graph = new SparseMultigraph<Number,Number>();

        layout = new StaticLayout<Number,Number>(graph,new Dimension(600,600));
        
        vv =  new VisualizationViewer<Number,Number>(layout);
        vv.setBackground(Color.white);

        vv.getRenderContext().setVertexLabelTransformer(MapTransformer.<Number,String>getInstance(
        		LazyMap.<Number,String>decorate(new HashMap<Number,String>(), new ToStringLabeller<Number>())));
        
        vv.getRenderContext().setEdgeLabelTransformer(MapTransformer.<Number,String>getInstance(
        		LazyMap.<Number,String>decorate(new HashMap<Number,String>(), new ToStringLabeller<Number>())));

        vv.setVertexToolTipTransformer(vv.getRenderContext().getVertexLabelTransformer());
        

        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        this.add(panel);
        Factory<Number> vertexFactory = new VertexFactory();
        Factory<Number> edgeFactory = new EdgeFactory();
        
        graphMouse = new EditingModalGraphMouse<Number,Number>(vv.getRenderContext(), vertexFactory, edgeFactory);
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

        graphMouse.setMode(ModalGraphMouse.Mode.EDITING);


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