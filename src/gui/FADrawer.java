package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.github.jabbalaci.graphviz.GraphViz;

import faops.FA;

public class FADrawer extends JFrame{
    int margin=50;
    BufferedImage img = null;
     public FADrawer(FA fa, int num){
          this.setTitle("FA #" + num);
          setResizable(false);
          setLocationRelativeTo(null);
          setVisible(true);
          GraphViz gv = new GraphViz();
          gv.maxDpi();
          try {
			img = ImageIO.read(new ByteArrayInputStream(new GraphViz().getGraph(fa.toString(), "png", "dot")));
		} catch (IOException e) {
			e.printStackTrace();
		}
          this.setSize(img.getWidth()+margin,img.getHeight()+margin);
          //new GraphViz().writeGraphToFile(new GraphViz().getGraph(fa.toString(), "png", "dot"), "test1.png");
          this.toFront();
          //this.setAlwaysOnTop(true);
          this.requestFocusInWindow();
     }

     public void paint(Graphics g){
    	g.setColor(Color.WHITE);
    	if(img != null){
    		g.fillRect(0, 0, img.getWidth()+margin, img.getHeight()+margin);
    		g.drawImage(img, margin/2, margin/2 + 10, this.getWidth()-margin, this.getHeight()-margin, Color.WHITE, null);
    	}
     }
}