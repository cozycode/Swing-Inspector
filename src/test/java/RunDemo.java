/*
 * Copyright (C) 2011 Cozycode.net
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.cozycode.swinginspector.SwingInspector;
import net.cozycode.swinginspector.components.ComponentTree;


public class RunDemo {
   public static void main( String[] args ) {
      JFrame.setDefaultLookAndFeelDecorated( true );
      try {
         //         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         //         UIManager.setLookAndFeel( "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel" );
      } catch( Exception e ) {
         e.printStackTrace();
      }

      createFrame( "Frame 1" );
      //      createFrame( "Frame 2" );
      //      createFrame( "Frame 3" );


      //      SwingInspector.installKeyListener( KeyEvent.VK_Z, MouseEvent.CTRL_DOWN_MASK | MouseEvent.ALT_DOWN_MASK );
      SwingInspector.installMouseListener( MouseEvent.BUTTON1, MouseEvent.CTRL_DOWN_MASK | MouseEvent.ALT_DOWN_MASK );

      //      com.sun.swingset3.SwingSet3.main (args );
   }

   public static void createFrame( String title ) {
      final Random rand = new Random();

      final JButton west = new JButton( "West" );
      final JButton east = new JButton( "East" );

      west.addActionListener( new ActionListener() {
         public void actionPerformed( ActionEvent e ) {
            int red   = rand.nextInt( 255 );
            int green = rand.nextInt( 255 );
            int blue  = rand.nextInt( 255 );
            int width = rand.nextInt( 5 ) + 1;

            east.setBorder( BorderFactory.createLineBorder( new Color( red, green, blue ), width ));
         }
      });

      east.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            int red   = rand.nextInt( 255 );
            int green = rand.nextInt( 255 );
            int blue  = rand.nextInt( 255 ); 

            west.setBackground( new Color( red, green, blue ));
         }
      });

      JPanel subPanel = new JPanel( new BorderLayout());
      subPanel.add( BorderLayout.WEST, west );
      subPanel.add( BorderLayout.EAST, east );

      JPanel panel = new JPanel();
      panel.add( new JLabel("label" ));
      panel.add( new JButton("button1" ));
      panel.add( new JTextField( "Starting Text", 20 ));
      panel.add( subPanel );

      ComponentTree tree = new ComponentTree();

      JPanel mainPanel = new JPanel( new BorderLayout() );
      mainPanel.add( panel, BorderLayout.NORTH );
      mainPanel.add( new JScrollPane( tree ), BorderLayout.CENTER );

      JFrame frame = new JFrame( title );
      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      frame.setContentPane( mainPanel );
      frame.pack();
      frame.setVisible(true);

      frame.equals( frame );
   }
}
