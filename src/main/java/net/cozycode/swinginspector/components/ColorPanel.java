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

package net.cozycode.swinginspector.components;

import static net.cozycode.constructs.Tuple.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import net.cozycode.constructs.IClosure;
import net.cozycode.constructs.Tuple2;
import net.cozycode.swing.components.LinkButton;
import net.miginfocom.swing.MigLayout;


public class ColorPanel extends JPanel {
   private static final long serialVersionUID = 1L;

   private final MyMouseListener mouseListener = new MyMouseListener();
   private final IClosure<Tuple2<ColorPanel,Color>> closure;
   private final JPanel pane = new JPanel();

   private final LinkButton nullButton = new LinkButton( "null" );

   public ColorPanel( ) {
      this( null );
   }

   public ColorPanel( IClosure<Tuple2<ColorPanel,Color>> closure ) {
      this( closure, false );
   }

   public ColorPanel( IClosure<Tuple2<ColorPanel,Color>> closure, boolean nullable ) {
      super( new MigLayout( "fill, ins 0", "[fill,50!][][fill,grow]", "[fill]" ));
      this.closure = closure;
      add( pane );
      if( nullable ) {
         add( nullButton );
         nullButton.addActionListener( new NullListener() );
      }

      if( closure != null ) {
         pane.addMouseListener( mouseListener );
      }
   }

   public void setColor( Color color ) {
      int red = color.getRed();
      int green = color.getGreen();
      int blue = color.getBlue();
      pane.setBackground( color );

      String tooltip = 
          "<html><font color=red>Red: "+ red +"</font>" 
         +"<br><font color=green>Green: "+ green +"</font>"
         +"<br><font color=blue>Blue: "+ blue +"</font>";

      pane.setToolTipText( tooltip );
      pane.setBorder( BorderFactory.createLineBorder( Color.DARK_GRAY, 1 ));
   }

   private class MyMouseListener extends MouseAdapter {
      @Override
      public void mouseClicked(MouseEvent e) {
         Color color = JColorChooser.showDialog( pane, "Select Color", pane.getBackground() );
         closure.yield( tuple(ColorPanel.this, color ));
      }
   }

   private class NullListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         closure.yield( tuple( ColorPanel.this, (Color)null ));
      } 
   }
}
