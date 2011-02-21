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
import static net.cozycode.swinginspector.components.SwingInspectorUtilities.*;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.cozycode.constructs.IClosure;
import net.cozycode.constructs.Tuple2;
import net.cozycode.swing.components.LinkButton;
import net.cozycode.swing.formatters.IntegerFormatter;
import net.cozycode.swing.listeners.FocusListeners;
import net.miginfocom.swing.MigLayout;

public class PointPanel extends JPanel {
   private static final long serialVersionUID = 1L;

   private final MyDocumentListener documentListener = new MyDocumentListener();
   private final MyFocusListener focusListener = new MyFocusListener();
   private final MyActionListener actionListener = new MyActionListener();
   private final IClosure<Tuple2<PointPanel,Point>> closure;

   private final JFormattedTextField x 
      = destylize( new JFormattedTextField( new IntegerFormatter( false )));

   private final JFormattedTextField y 
      = destylize( new JFormattedTextField( new IntegerFormatter( false )));

   private final LinkButton nullButton = new LinkButton( "null" );

   private boolean updating = false;

   public PointPanel() {
      this( null, false );
   }

   public PointPanel( final IClosure<Tuple2<PointPanel,Point>> closure ) {
      this( closure, false );
   }

   public PointPanel( final IClosure<Tuple2<PointPanel,Point>> closure, boolean nullable ) {
      super( new MigLayout( "insets 0" ));
      this.closure = closure;

      add( mildFont( new JLabel( "(" )));
      add( x );
      add( mildFont( new JLabel( "," )));
      add( y );
      add( mildFont( new JLabel( ")" )));

      if( nullable ) {
         add( nullButton );
         nullButton.addActionListener( new NullListener() );
      }

      x.addFocusListener( focusListener );
      y.addFocusListener( focusListener );

      x.addFocusListener( FocusListeners.SELECT_ALL );
      y.addFocusListener( FocusListeners.SELECT_ALL );

      x.addActionListener( actionListener );
      y.addActionListener( actionListener );

      x.getDocument().addDocumentListener( documentListener );
      y.getDocument().addDocumentListener( documentListener );
   }

   public void setPoint( Point p ) {
      if( !updating ) {
         updating = true;

         x.setValue( p.x );
         y.setValue( p.y );

         updating = false;
      }
   }

   private void textChanged() {
      if( closure != null && !updating ) {
         updating = true;

         int xx = (Integer)x.getValue();
         int yy = (Integer)y.getValue();
         closure.yield( tuple( this, new Point( xx, yy )));

         updating = false;
      }
   }

   @Override
   public void setEnabled(boolean enabled) {
      x.setEditable( enabled );
      y.setEditable( enabled );
      x.setFocusable( enabled );
      y.setFocusable( enabled );
      nullButton.setEnabled( enabled );
      super.setEnabled( enabled );
   }

   private class NullListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         closure.yield( tuple( PointPanel.this, (Point)null ));
      } 
   }

   private class MyActionListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         textChanged();
      }
   }

   private class MyFocusListener implements FocusListener {
      @Override
      public void focusGained(FocusEvent e) { /* noop */ }

      @Override
      public void focusLost(FocusEvent e) {
         // This corrects an order of operations issue
         // where the focus handler is running before
         // the field has a chance to commit the edit.
         SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
               textChanged();
            }
         });
      }
   }

   private class MyDocumentListener implements DocumentListener {
      @Override
      public void insertUpdate(DocumentEvent e) {
         revalidate();
         repaint();     
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
         revalidate();
         repaint();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
         revalidate();
         repaint();
      }
   }

}
